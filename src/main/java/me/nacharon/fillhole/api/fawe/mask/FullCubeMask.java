package me.nacharon.fillhole.api.fawe.mask;

import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.function.mask.BlockMask;
import com.sk89q.worldedit.function.mask.Mask;

public class FullCubeMask extends BlockMask {

    public FullCubeMask(Extent extent) {
        super(extent);
        add(state -> state.getMaterial().isFullCube());
    }

    @Override
    public Mask copy() {
        return new FullCubeMask(getExtent());
    }
}
