package me.nacharon.fillhole.api.fawe.mask;

import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.function.mask.BlockMask;
import com.sk89q.worldedit.function.mask.Mask;

public class TranslucentMask extends BlockMask {

    public TranslucentMask(Extent extent) {
        super(extent);
        add(state -> state.getMaterial().isTranslucent());
    }

    @Override
    public Mask copy() {
        return new TranslucentMask(getExtent());
    }
}
