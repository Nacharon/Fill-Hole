package me.nacharon.fillhole.api.fawe.mask;

import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.function.mask.BlockMask;
import com.sk89q.worldedit.function.mask.Mask;

/**
 * A mask that filters non-full cube or translucent blocks.
 */
public class HoleMask extends BlockMask {
    /**
     * Constructs a HoleMask for a given extent.
     *
     * @param extent The extent to apply the mask to.
     */

    public HoleMask(Extent extent) {
        super(extent);
        add(state -> !state.getMaterial().isFullCube() || state.getMaterial().isTranslucent());
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
