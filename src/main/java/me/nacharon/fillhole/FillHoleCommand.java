package me.nacharon.fillhole;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extension.input.ParserContext;
import com.sk89q.worldedit.function.pattern.Pattern;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.world.registry.BlockMaterial;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class FillHoleCommand implements CommandExecutor {

    /*
     *
     */
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        // check if the sender is a player
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command must be executed by a player!");
            return true;
        }

        // check if the player has permission to use this command
        if (!player.hasPermission("fillhole.use")) {
            player.sendMessage("You do not have permission to use this command.");
            return true;
        }

        // check that the command is used correctly
        if (args.length < 1) {
            player.sendMessage("Usage: /fillhole <pattern>");
            return true;
        }

        try {
            // get pattern argument
            String patternInput = args[0];

            ParserContext context = new ParserContext();
            context.setActor(BukkitAdapter.adapt(player));
            context.setExtent(BukkitAdapter.adapt(player.getWorld()));

            Pattern pattern = WorldEdit.getInstance()
                    .getPatternFactory()
                    .parseFromInput(patternInput, context);


            Region selection;
            try {
                selection = WorldEdit.getInstance()
                        .getSessionManager()
                        .get(BukkitAdapter.adapt(player))
                        .getSelection();
            } catch (Exception e) {
                player.sendMessage("Please select a region first!");
                return true;
            }

            // get the FAWE edit seesion
            LocalSession session = WorldEdit.getInstance()
                    .getSessionManager()
                    .get(BukkitAdapter.adapt(player));
            EditSession editSession = session.createEditSession(BukkitAdapter.adapt(player));

            try {
                // detect and fill hole
                Set<BlockVector3> blockChange = getHoleBlocks(selection, editSession);
                editSession.setBlocks(blockChange, pattern);

                player.sendMessage(blockChange.isEmpty() ?
                        "No holes were found within the specified size." :
                        "All the holes have been filled!");
            } finally {
                // saves the operation in the history and can be used undo
                editSession.flushQueue();
                session.remember(editSession);
            }

        } catch (Exception e) {
            player.sendMessage("Error : " + e.getMessage());
        }

        return true;
    }

    /*
     *
     */
    private List<BlockVector3> getAdjacentBlocks(BlockVector3 block) {
        return List.of(
                block.add(1, 0, 0),  // Est
                block.add(-1, 0, 0), // Ouest
                block.add(0, 1, 0),  // Haut
                block.add(0, -1, 0), // Bas
                block.add(0, 0, 1),  // Sud
                block.add(0, 0, -1)  // Nord
        );
    }

    /*
     *
     */
    private boolean isValidMaterial(EditSession editSession, BlockVector3 block) {
        BlockMaterial material = editSession.getBlock(block).getBlockType().getMaterial();
        return material.isAir() || material.isLiquid() || material.isTranslucent() || !material.isFullCube();
    }

    /*
     *
     */
    private Set<BlockVector3> getHoleBlocks(Region selection, EditSession editSession) {
        BlockVector3 min = selection.getMinimumPoint();
        BlockVector3 max = selection.getMaximumPoint();

        int dx = max.x() - min.x() + 1;
        int dy = max.y() - min.y() + 1;
        int dz = max.z() - min.z() + 1;

        boolean[][][] visited = new boolean[dx][dy][dz];

        Deque<BlockVector3> nextVisit = new ArrayDeque<>();
        Set<BlockVector3> change = new HashSet<>();

        // initialise visited to false
        for (BlockVector3 block : selection) {
            if (isValidMaterial(editSession, block)) {
                int x = block.x() - min.x();
                int y = block.y() - min.y();
                int z = block.z() - min.z();
                visited[x][y][z] = false;
            }
        }

        for (BlockVector3 block : selection) {
            int x = block.x() - min.x();
            int y = block.y() - min.y();
            int z = block.z() - min.z();

            if (!visited[x][y][z] && isValidMaterial(editSession, block)) {
                nextVisit.add(BlockVector3.at(block.x(), block.y(), block.z()));
                visited[x][y][z] = true;

                Set<BlockVector3> currentChain = new HashSet<>();
                boolean touchesBorder = false;

                //check adjacent block to detect hole
                while (!nextVisit.isEmpty()) {
                    BlockVector3 current = nextVisit.pollFirst();
                    currentChain.add(current);

                    for (BlockVector3 adjacent : getAdjacentBlocks(current)) {
                        int adjX = adjacent.x() - min.x();
                        int adjY = adjacent.y() - min.y();
                        int adjZ = adjacent.z() - min.z();

                        // if a block in the hole touches the edge of the selection, the hole is not a hole
                        if (adjX < 0 || adjX >= dx || adjY < 0 || adjY >= dy || adjZ < 0 || adjZ >= dz) {
                            touchesBorder = true;
                        } else if (!visited[adjX][adjY][adjZ] && isValidMaterial(editSession, adjacent)) {
                            visited[adjX][adjY][adjZ] = true;
                            nextVisit.add(adjacent);
                        }
                    }
                }

                if (!touchesBorder) {
                    change.addAll(currentChain);
                }
            }
        }
        return change;
    }
}
