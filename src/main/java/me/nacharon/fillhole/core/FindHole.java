package me.nacharon.fillhole.core;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.function.mask.Mask;
import com.sk89q.worldedit.function.pattern.Pattern;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import me.nacharon.fillhole.Main;
import me.nacharon.fillhole.api.Config;
import me.nacharon.fillhole.api.fawe.FaweHook;
import me.nacharon.fillhole.api.fawe.mask.HoleMask;
import me.nacharon.fillhole.command.FillHoleCommand;
import me.nacharon.fillhole.utils.ProgressBar;
import me.nacharon.fillhole.utils.TextUtils;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

/**
 * The FindHole class is responsible for detecting and filling holes within a selected region
 * using Fast Async WorldEdit (FAWE). It iterates over the region, filters blocks based on
 * specific criteria, and fills detected holes with a given pattern.
 */
public class FindHole {

    private final Iterator<BlockVector3> selectionIterator;
    private final EditSession editSession;
    private final BlockInfo minBlock;

    private final int dx;
    private final int dy;
    private final int dz;

    private final boolean[][][] visited;
    private final Deque<BlockVector3> nextVisit = new ArrayDeque<>();
    private final Set<BlockVector3> change = new HashSet<>();
    private Set<BlockVector3> currentChain = new HashSet<>();
    private boolean touchesBorder = false;
    private final Mask mask; // Attribut

    private final long selectionSize;
    private long nbVisitedBlock = 0;
    private boolean subTaskFinish = true;
    private final int fillHoleMaxCycle;
    private final long fillHoleTaskDelay;


    /**
     * Constructs a FindHole instance for processing a given selection.
     *
     * @param selection   The region to process.
     * @param editSession The WorldEdit edit session.
     */
    public FindHole(Region selection, EditSession editSession) {
        this.selectionIterator = selection.iterator();
        this.editSession = editSession;

        this.fillHoleMaxCycle = Config.getFillHoleProcessedCycle();
        this.fillHoleTaskDelay = Config.getFillHoleTickCycle();

        BlockInfo maxBlock = new BlockInfo(selection.getMaximumPoint());
        this.minBlock = new BlockInfo(selection.getMinimumPoint());

        this.dx = maxBlock.x - this.minBlock.x + 1;
        this.dy = maxBlock.y - this.minBlock.y + 1;
        this.dz = maxBlock.z - this.minBlock.z + 1;
        this.selectionSize = selection.getVolume();
        this.visited = new boolean[this.dx][this.dy][this.dz];

        this.mask = new HoleMask(this.editSession.getExtent());
    }

    /**
     * Gets the adjacent blocks of a given block.
     *
     * @param block the block to get adjacent blocks for
     * @return a list of adjacent blocks
     */
    private static List<BlockInfo> getAdjacentBlocks(BlockVector3 block) {
        return List.of(
                new BlockInfo(block.add(1, 0, 0)),  // Est
                new BlockInfo(block.add(-1, 0, 0)), // Ouest
                new BlockInfo(block.add(0, 1, 0)),  // Haut
                new BlockInfo(block.add(0, -1, 0)), // Bas
                new BlockInfo(block.add(0, 0, 1)),  // Sud
                new BlockInfo(block.add(0, 0, -1))  // Nord
        );
    }

    /**
     * Checks if a given block material is considered valid for hole detection.
     *
     * @param blockInfo The block to check.
     * @return True if the material is translucent or not a full cube.
     */
    public boolean isValidMaterial(BlockInfo blockInfo) {
        return isValidMaterial(blockInfo.getBlock());
    }

    /**
     * Checks if a given block material is considered valid for hole detection.
     *
     * @param block The block to check.
     * @return True if the material is translucent or not a full cube.
     */
    public boolean isValidMaterial(BlockVector3 block) {

        return mask.test(block);
    }

    /**
     * Initiates the hole-filling process by filtering blocks and identifying regions to fill.
     *
     * @param player       The player executing the command.
     * @param localSession The WorldEdit local session.
     * @param pattern      The pattern to use for filling holes.
     */
    public void fillHoleBlocks(Player player, LocalSession localSession, Pattern pattern) {
        BukkitRunnable progressBarTask = new BukkitRunnable() {
            @Override
            public void run() {
                ProgressBar.sendProgressBar(player, "Search Hole", selectionSize, nbVisitedBlock);
            }
        };

        BukkitRunnable findHoleTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (subTaskFinish) {
                    findHole();
                }

                if (!selectionIterator.hasNext()) {
                    ProgressBar.sendProgressBar(player, "Search Hole", selectionSize, nbVisitedBlock);

                    if (change.isEmpty()) {
                        player.sendMessage(TextUtils.textGray("No holes were found in this selection."));
                    } else {
                        FaweHook.setBlocks(change, pattern, localSession, editSession);
                        player.sendMessage(TextUtils.textGreen(change.size() + " blocks have been filled"));
                    }

                    FillHoleCommand.removeTask(player);
                    cancel();
                    progressBarTask.cancel();
                }
            }
        };

        findHoleTask.runTaskTimer(Main.getInstance(), 0, fillHoleTaskDelay);
        progressBarTask.runTaskTimer(Main.getInstance(), 0, Config.getTaskBarDelay());
    }


    /**
     * Processes the next visit queue to detect hole regions.
     *
     * @param blocksProcessed number of already block process
     * @return number of block process after the function
     */
    private int nextVisitProcess(int blocksProcessed) {
        while (!this.nextVisit.isEmpty() && blocksProcessed < this.fillHoleMaxCycle) {
            BlockVector3 current = this.nextVisit.pollFirst();
            this.currentChain.add(current);
            blocksProcessed++;

            for (BlockInfo adjacentInfo : getAdjacentBlocks(current)) {
                int adjX = adjacentInfo.x - this.minBlock.x;
                int adjY = adjacentInfo.y - this.minBlock.y;
                int adjZ = adjacentInfo.z - this.minBlock.z;

                if (adjX < 0 || adjX >= this.dx || adjY < 0 || adjY >= this.dy || adjZ < 0 || adjZ >= this.dz) {
                    this.touchesBorder = true;
                } else if (!this.visited[adjX][adjY][adjZ] && isValidMaterial(adjacentInfo)) {
                    this.visited[adjX][adjY][adjZ] = true;
                    this.nextVisit.add(adjacentInfo.getBlock());
                } else if (!this.visited[adjX][adjY][adjZ]) {
                    this.visited[adjX][adjY][adjZ] = true;
                    blocksProcessed++;
                }
            }
        }
        if (nextVisit.isEmpty() && !this.touchesBorder) {
            this.change.addAll(this.currentChain);
        }
        return blocksProcessed;
    }

    /**
     * Finds the next hole region to process.
     *
     * @param blocksProcessed number of already block process
     * @return number of block process after the function
     */
    private int findNextHole(int blocksProcessed) {
        while (selectionIterator.hasNext() && blocksProcessed < this.fillHoleMaxCycle) {
            BlockInfo blockInfo = new BlockInfo(selectionIterator.next());

            int x = blockInfo.x - this.minBlock.x;
            int y = blockInfo.y - this.minBlock.y;
            int z = blockInfo.z - this.minBlock.z;

            if (!this.visited[x][y][z] && isValidMaterial(blockInfo)) {
                this.visited[x][y][z] = true;
                this.nextVisit.add(blockInfo.getBlock());

                this.currentChain = new HashSet<>();
                this.touchesBorder = false;

                blocksProcessed = this.nextVisitProcess(blocksProcessed);
            } else if (!this.visited[x][y][z]) {
                this.visited[x][y][z] = true;
                blocksProcessed++;
            }
        }
        return blocksProcessed;
    }

    /**
     * Finds and processes holes within the selected region.
     */
    private void findHole() {
        subTaskFinish = false;
        int blocksProcessed = 0;

        blocksProcessed = this.nextVisitProcess(blocksProcessed);
        blocksProcessed = this.findNextHole(blocksProcessed);
        this.nbVisitedBlock += blocksProcessed;

        subTaskFinish = true;
    }
}
