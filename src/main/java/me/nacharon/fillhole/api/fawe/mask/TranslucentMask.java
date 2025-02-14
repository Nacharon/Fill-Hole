package me.nacharon.fillhole.api.fawe.mask;

import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.function.mask.BlockMask;
import com.sk89q.worldedit.function.mask.Mask;

/**
 * A mask that filters translucent blocks.
 */
public class TranslucentMask extends BlockMask {

    /**
     * Constructs a TranslucentMask for a given extent.
     *
     * @param extent The extent to apply the mask to.
     */
    public TranslucentMask(Extent extent) {
        super(extent);
        add(state -> state.getMaterial().isTranslucent());
    }

    /**
     * Creates a copy of this mask.
     *
     * @return A new TranslucentMask instance.
     */
    @Override
    public Mask copy() {
        return new TranslucentMask(getExtent());
    }
}
