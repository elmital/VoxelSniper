package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.VoxelMessage;
import com.thevoxelbox.voxelsniper.brush.perform.PerformerBrush;
import com.thevoxelbox.voxelsniper.snipe.SnipeData;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

/**
 * @author Voxel
 */
public class VoxelDiscFaceBrush extends PerformerBrush {

    /**
     *
     */
    public VoxelDiscFaceBrush() {
        this.setName("Voxel Disc Face");
    }

    private void disc(final SnipeData v, Block targetBlock) {
        for (int x = v.getBrushSize(); x >= -v.getBrushSize(); x--) {
            for (int y = v.getBrushSize(); y >= -v.getBrushSize(); y--) {
                this.currentPerformer.perform(this.clampY(targetBlock.getX() + x, targetBlock.getY(), targetBlock.getZ() + y));
            }
        }

        v.owner().storeUndo(this.currentPerformer.getUndo());
    }

    private void discNS(final SnipeData v, Block targetBlock) {
        for (int x = v.getBrushSize(); x >= -v.getBrushSize(); x--) {
            for (int y = v.getBrushSize(); y >= -v.getBrushSize(); y--) {
                this.currentPerformer.perform(this.clampY(targetBlock.getX() + x, targetBlock.getY() + y, targetBlock.getZ()));
            }
        }

        v.owner().storeUndo(this.currentPerformer.getUndo());
    }

    private void discEW(final SnipeData v, Block targetBlock) {
        for (int x = v.getBrushSize(); x >= -v.getBrushSize(); x--) {
            for (int y = v.getBrushSize(); y >= -v.getBrushSize(); y--) {
                this.currentPerformer.perform(this.clampY(targetBlock.getX(), targetBlock.getY() + x, targetBlock.getZ() + y));
            }
        }

        v.owner().storeUndo(this.currentPerformer.getUndo());
    }

    private void pre(final SnipeData v, final BlockFace bf, Block targetBlock) {
        if (bf == null) {
            return;
        }
        switch (bf) {
            case NORTH, SOUTH -> this.discNS(v, targetBlock);
            case EAST, WEST -> this.discEW(v, targetBlock);
            case UP, DOWN -> this.disc(v, targetBlock);
            default -> {
            }
        }
    }

    @Override
    protected final void arrow(final SnipeData v) {
        this.pre(v, this.getTargetBlock().getFace(this.getLastBlock()), this.getTargetBlock());
    }

    @Override
    protected final void powder(final SnipeData v) {
        this.pre(v, this.getTargetBlock().getFace(this.getLastBlock()), this.getLastBlock());
    }

    @Override
    public final void info(final VoxelMessage vm) {
        vm.brushName(this.getName());
        vm.size();
    }

    @Override
    public String getPermissionNode() {
        return "voxelsniper.brush.voxeldiscface";
    }
}
