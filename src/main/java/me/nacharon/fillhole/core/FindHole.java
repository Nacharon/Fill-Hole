package me.nacharon.fillhole.core;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.function.mask.Mask;
import com.sk89q.worldedit.function.pattern.Pattern;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.world.registry.BlockMaterial;
import me.nacharon.fillhole.Main;
import me.nacharon.fillhole.api.Config;
import me.nacharon.fillhole.api.fawe.FaweHook;
import me.nacharon.fillhole.api.fawe.mask.HoleMask;
import me.nacharon.fillhole.command.FillHoleCommand;
import me.nacharon.fillhole.utils.ProgressBar;
import me.nacharon.fillhole.utils.TextUtils;
import org.bukkit.Bukkit;
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
    private final Deque<BlockInfo> filteredBlocks = new ArrayDeque<>();
    private final Set<BlockVector3> change = new HashSet<>();
    private Set<BlockVector3> currentChain = new HashSet<>();
    private boolean touchesBorder = false;

    private final long selectionSize;
    private long nbValidBlock;
    private long nbVisitedBlock = 0;
    private boolean subTaskFinish = true;
    private final int initMaxCycle;
    private final long initTaskDelay;
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

        this.initMaxCycle = Config.getInitProcessedCycle();
        this.initTaskDelay = Config.getInitTickCycle();
        this.fillHoleMaxCycle = Config.getFillHoleProcessedCycle();
        this.fillHoleTaskDelay = Config.getFillHoleTickCycle();

        BlockInfo maxBlock = new BlockInfo(selection.getMaximumPoint());
        this.minBlock = new BlockInfo(selection.getMinimumPoint());

        this.dx = maxBlock.x - this.minBlock.x + 1;
        this.dy = maxBlock.y - this.minBlock.y + 1;
        this.dz = maxBlock.z - this.minBlock.z + 1;
        this.selectionSize = selection.getVolume();
        this.visited = new boolean[this.dx][this.dy][this.dz];

        nbValidBlock = filteredBlocks.size();
    }

    /**
     * Gets the adjacent blocks of a given block.
     *
     * @param block the block to get adjacent blocks for
     * @return a list of adjacent blocks
     */
    private static List<BlockVector3> getAdjacentBlocks(BlockVector3 block) {
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
     * Checks if a given block material is considered valid for hole detection.
     *
     * @param block The block to check.
     * @return True if the material is translucent or not a full cube.
     */
    public boolean isValidMaterial(BlockVector3 block) {
        BlockMaterial material = this.editSession.getBlock(block).getBlockType().getMaterial();
        return material.isTranslucent() || !material.isFullCube();
    }

    /**
     * Initiates the hole-filling process by filtering blocks and identifying regions to fill.
     *
     * @param player       The player executing the command.
     * @param localSession The WorldEdit local session.
     * @param pattern      The pattern to use for filling holes.
     */
    public void fillHoleBlocks(Player player, LocalSession localSession, Pattern pattern) {
        Mask mask = new HoleMask(editSession.getExtent());

        BukkitRunnable findHoleTask = new BukkitRunnable() {

            @Override
            public void run() {

                if (subTaskFinish) {
                    ProgressBar.sendProgressBar(player, "Search Hole", nbValidBlock, nbVisitedBlock);
                    findHole();
                }

                if (nbVisitedBlock >= nbValidBlock) {
                    ProgressBar.sendProgressBar(player, "Search Hole", nbValidBlock, nbVisitedBlock);
                    if (change.isEmpty())
                        player.sendMessage(TextUtils.textGray("No holes were found in this selection."));
                    else {
                        FaweHook.setBlocks(change, pattern, localSession, editSession);
                        player.sendMessage(TextUtils.textGreen(change.size() + " blocks have been filled"));
                    }
                    FillHoleCommand.removeTask(player);
                    cancel();
                }
            }
        };

        BukkitRunnable initTask = new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    if (subTaskFinish) {
                        ProgressBar.sendProgressBar(player, "Filter Block", selectionSize, nbVisitedBlock);
                        subTaskFinish = false;
                        int count = 0;
                        while (selectionIterator.hasNext() && count < initMaxCycle) {
                            BlockVector3 pos = selectionIterator.next();
                            if (mask.test(pos)) {
                                filteredBlocks.add(new BlockInfo(pos));
                            }
                            count++;
                        }
                        nbVisitedBlock += count;
                        subTaskFinish = true;
                        if (!selectionIterator.hasNext()) {
                            ProgressBar.sendProgressBar(player, "Filter Block", selectionSize, nbVisitedBlock);

                            nbVisitedBlock = 0;
                            nbValidBlock = filteredBlocks.size();

                            findHoleTask.runTaskTimer(Main.getInstance(), 1L, fillHoleTaskDelay);
                            cancel();
                        }
                    }
                } catch (OutOfMemoryError error) {
                    Bukkit.getLogger().severe("Out of memory error detected! Cancelling fillhole command...");
                    player.sendMessage(TextUtils.textRed("The selection is too big, the command are cancel"));

                    filteredBlocks.clear();
                    FillHoleCommand.removeTask(player);
                    cancel();
                }
            }
        };
        initTask.runTaskTimer(Main.getInstance(), 0L, initTaskDelay);
    }

    /**
     * Processes the next visit queue to detect hole regions.
     */
    private int nextVisitProcess(int blocksProcessed) {
        while (!this.nextVisit.isEmpty() && blocksProcessed < this.fillHoleMaxCycle) {
            BlockVector3 current = this.nextVisit.pollFirst();
            this.currentChain.add(current);
            blocksProcessed++;

            for (BlockVector3 adjacent : getAdjacentBlocks(current)) {
                BlockInfo adjacentInfo = new BlockInfo(adjacent);
                int adjX = adjacentInfo.x - this.minBlock.x;
                int adjY = adjacentInfo.y - this.minBlock.y;
                int adjZ = adjacentInfo.z - this.minBlock.z;

                if (adjX < 0 || adjX >= this.dx || adjY < 0 || adjY >= this.dy || adjZ < 0 || adjZ >= this.dz) {
                    this.touchesBorder = true;
                } else if (!this.visited[adjX][adjY][adjZ] && isValidMaterial(adjacent)) {
                    this.visited[adjX][adjY][adjZ] = true;
                    this.nextVisit.add(adjacentInfo.getBlock());
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
     */
    private int findNextHole(int blocksProcessed) {
        while (!filteredBlocks.isEmpty() && blocksProcessed < this.fillHoleMaxCycle) {
            BlockInfo blockInfo = filteredBlocks.pop();

            int x = blockInfo.x - this.minBlock.x;
            int y = blockInfo.y - this.minBlock.y;
            int z = blockInfo.z - this.minBlock.z;

            if (!this.visited[x][y][z]) {
                this.nextVisit.add(blockInfo.getBlock());
                this.visited[x][y][z] = true;

                this.currentChain = new HashSet<>();
                this.touchesBorder = false;

                blocksProcessed = this.nextVisitProcess(blocksProcessed);

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
