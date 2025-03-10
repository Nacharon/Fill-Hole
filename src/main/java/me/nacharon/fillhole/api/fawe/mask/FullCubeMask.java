package me.nacharon.fillhole.api.fawe.mask;

import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.function.mask.BlockMask;
import com.sk89q.worldedit.function.mask.Mask;

/**
 * A mask that filters only full cube blocks.
 */
public class FullCubeMask extends BlockMask {
    /**
     * Constructs a FullCubeMask for a given extent.
     *
     * @param extent The extent to apply the mask to.
     */
    public FullCubeMask(Extent extent) {
        super(extent);
        add(state -> state.getMaterial().isFullCube());
    }

    /**
     * Creates a copy of this mask.
     *
     * @return A new FullCubeMask instance.
     */
    @Override
    public Mask copy() {
        return new FullCubeMask(getExtent());
    }
}
