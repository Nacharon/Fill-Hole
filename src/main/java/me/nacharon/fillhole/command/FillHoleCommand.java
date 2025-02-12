package me.nacharon.fillhole.command;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.function.pattern.Pattern;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.world.registry.BlockMaterial;
import me.nacharon.fillhole.api.fawe.FaweHook;
import me.nacharon.fillhole.utils.PluginUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;


/**
 * Define FillHole Command
 */
public class FillHoleCommand implements CommandExecutor {

    /**
     * Handles the execution of the /fillhole command.
     *
     * @param sender  the sender of the command
     * @param command the command being executed
     * @param label   the alias used to trigger the command
     * @param args    the arguments passed with the command
     * @return true if the command was executed successfully, false otherwise
     */
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        // check if the sender is a player
        if (!(sender instanceof Player player)) {
            sender.sendMessage(PluginUtils.textRed("This command must be executed by a player!"));
            return true;
        }

        // check if the player has permission to use this command
        if (!player.hasPermission("fillhole.use")) {
            player.sendMessage(PluginUtils.textRed("You do not have permission to use this command."));
            return true;
        }

        // check that the command is used correctly
        if (args.length < 1) {
            player.sendMessage(PluginUtils.textRed("Usage: /fillhole <pattern>"));
            return true;
        }

        try {
            String patternInput = args[0];

            Pattern pattern = FaweHook.getPattern(patternInput, player);

            // get FAWE local session
            LocalSession localSession = FaweHook.getLocalSession(player);

            Region selection;
            try {
                selection = FaweHook.getSelection(localSession);
            } catch (Exception e) {
                player.sendMessage(PluginUtils.textRed("Please select a cuboid selection first!"));
                return true;
            }

            // get FAWE edit session
            EditSession editSession = localSession.createEditSession(BukkitAdapter.adapt(player));

            // detect and fill hole
            Set<BlockVector3> blockChange = getHoleBlocks(selection, editSession);
            FaweHook.setBlocks(blockChange, pattern, localSession, editSession);

            if (blockChange.isEmpty())
                player.sendMessage(PluginUtils.textGray("No holes were found in this selection."));
            else
                player.sendMessage(PluginUtils.textGray(blockChange.size() + " blocks have been filled"));

        } catch (Exception e) {
            player.sendMessage(PluginUtils.textRed("Error : " + e.getMessage()));
        }

        return true;
    }

    /**
     * Gets the adjacent blocks of a given block.
     *
     * @param block the block to get adjacent blocks for
     * @return a list of adjacent blocks
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

    /**
     * Checks if the material of a block is valid for processing.
     *
     * @param editSession the current edit session
     * @param block       the block to check
     * @return true if the block material is valid, false otherwise
     */
    private boolean isValidMaterial(EditSession editSession, BlockVector3 block) {
        BlockMaterial material = editSession.getBlock(block).getBlockType().getMaterial();
        return material.isAir() || material.isLiquid() || material.isTranslucent() || !material.isFullCube();
    }

    /**
     * Detects holes within a region and returns the blocks to be filled.
     *
     * @param selection   the region to scan for holes
     * @param editSession the current edit session
     * @return a set of blocks representing the holes
     */
    private Set<BlockVector3> getHoleBlocks(Region selection, EditSession editSession) {
        BlockVector3 min = selection.getMinimumPoint();
        BlockVector3 max = selection.getMaximumPoint();

        int dx = FaweHook.getX(max) - FaweHook.getX(min) + 1;
        int dy = FaweHook.getY(max) - FaweHook.getY(min) + 1;
        int dz = FaweHook.getZ(max) - FaweHook.getZ(min) + 1;

        boolean[][][] visited = new boolean[dx][dy][dz];

        Deque<BlockVector3> nextVisit = new ArrayDeque<>();
        Set<BlockVector3> change = new HashSet<>();

        // initialise visited to false
        for (BlockVector3 block : selection) {
            if (isValidMaterial(editSession, block)) {
                int x = FaweHook.getX(block) - FaweHook.getX(min);
                int y = FaweHook.getY(block) - FaweHook.getY(min);
                int z = FaweHook.getZ(block) - FaweHook.getZ(min);
                visited[x][y][z] = false;
            }
        }

        for (BlockVector3 block : selection) {
            int x = FaweHook.getX(block) - FaweHook.getX(min);
            int y = FaweHook.getY(block) - FaweHook.getY(min);
            int z = FaweHook.getZ(block) - FaweHook.getZ(min);

            if (!visited[x][y][z] && isValidMaterial(editSession, block)) {
                nextVisit.add(BlockVector3.at(FaweHook.getX(block), FaweHook.getY(block), FaweHook.getZ(block)));
                visited[x][y][z] = true;

                Set<BlockVector3> currentChain = new HashSet<>();
                boolean touchesBorder = false;

                //check adjacent block to detect hole
                while (!nextVisit.isEmpty()) {
                    BlockVector3 current = nextVisit.pollFirst();
                    currentChain.add(current);

                    for (BlockVector3 adjacent : getAdjacentBlocks(current)) {
                        int adjX = FaweHook.getX(adjacent) - FaweHook.getX(min);
                        int adjY = FaweHook.getY(adjacent) - FaweHook.getY(min);
                        int adjZ = FaweHook.getZ(adjacent) - FaweHook.getZ(min);

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
