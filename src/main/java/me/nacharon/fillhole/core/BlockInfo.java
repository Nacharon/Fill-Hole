package me.nacharon.fillhole.core;

import com.sk89q.worldedit.math.BlockVector3;
import me.nacharon.fillhole.api.fawe.FaweHook;

/**
 * Represents block information including its coordinates.
 */
public class BlockInfo {
    public final int x;
    public final int y;
    public final int z;

    /**
     * Constructs a BlockInfo object from a BlockVector3.
     *
     * @param block The block vector containing the coordinates.
     */
    public BlockInfo(BlockVector3 block) {
        x = FaweHook.getX(block);
        y = FaweHook.getY(block);
        z = FaweHook.getZ(block);
    }

    /**
     * Retrieves the block vector representation of this BlockInfo.
     *
     * @return The corresponding BlockVector3 instance.
     */
    public BlockVector3 getBlock() {
        return BlockVector3.at(x, y, z);
    }
}
