package com.thevoxelbox.voxelsniper.brush;

import com.google.common.collect.Lists;
import com.thevoxelbox.voxelsniper.VoxelMessage;
import com.thevoxelbox.voxelsniper.brush.perform.PerformerBrush;
import com.thevoxelbox.voxelsniper.snipe.SnipeData;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import java.util.ArrayList;
import java.util.List;

/**
 * http://www.voxelwiki.com/minecraft/Voxelsniper#The_Disc_Face_Brush
 *
 * @author Voxel
 */
public class DiscFaceBrush extends PerformerBrush {

    private static final double SMOOTH_CIRCLE_VALUE = 0.5;
    private static final double VOXEL_CIRCLE_VALUE = 0.0;

    private boolean smoothCircle = false;

    /**
     *
     */
    public DiscFaceBrush() {
        this.setName("Disc Face");
    }

    private void discUD(final SnipeData v, Block targetBlock) {
        final int brushSize = v.getBrushSize();
        final double brushSizeSquared = Math.pow(brushSize + (smoothCircle ? SMOOTH_CIRCLE_VALUE : VOXEL_CIRCLE_VALUE), 2);

        for (int x = brushSize; x >= 0; x--) {
            final double xSquared = Math.pow(x, 2);

            for (int z = brushSize; z >= 0; z--) {
                if ((xSquared + Math.pow(z, 2)) <= brushSizeSquared) {
                    currentPerformer.perform(targetBlock.getRelative(x, 0, z));
                    currentPerformer.perform(targetBlock.getRelative(x, 0, -z));
                    currentPerformer.perform(targetBlock.getRelative(-x, 0, z));
                    currentPerformer.perform(targetBlock.getRelative(-x, 0, -z));
                }
            }
        }

        v.owner().storeUndo(this.currentPerformer.getUndo());
    }

    private void discNS(final SnipeData v, Block targetBlock) {
        final int brushSize = v.getBrushSize();
        final double brushSizeSquared = Math.pow(brushSize + (smoothCircle ? SMOOTH_CIRCLE_VALUE : VOXEL_CIRCLE_VALUE), 2);

        for (int x = brushSize; x >= 0; x--) {
            final double xSquared = Math.pow(x, 2);
            for (int y = brushSize; y >= 0; y--) {
                if ((xSquared + Math.pow(y, 2)) <= brushSizeSquared) {
                    currentPerformer.perform(targetBlock.getRelative(x, y, 0));
                    currentPerformer.perform(targetBlock.getRelative(x, -y, 0));
                    currentPerformer.perform(targetBlock.getRelative(-x, y, 0));
                    currentPerformer.perform(targetBlock.getRelative(-x, -y, 0));
                }
            }
        }

        v.owner().storeUndo(this.currentPerformer.getUndo());
    }

    private void discEW(final SnipeData v, Block targetBlock) {
        final int brushSize = v.getBrushSize();
        final double brushSizeSquared = Math.pow(brushSize + (smoothCircle ? SMOOTH_CIRCLE_VALUE : VOXEL_CIRCLE_VALUE), 2);

        for (int x = brushSize; x >= 0; x--) {
            final double xSquared = Math.pow(x, 2);
            for (int y = brushSize; y >= 0; y--) {
                if ((xSquared + Math.pow(y, 2)) <= brushSizeSquared) {
                    currentPerformer.perform(targetBlock.getRelative(0, x, y));
                    currentPerformer.perform(targetBlock.getRelative(0, x, -y));
                    currentPerformer.perform(targetBlock.getRelative(0, -x, y));
                    currentPerformer.perform(targetBlock.getRelative(0, -x, -y));
                }
            }
        }

        v.owner().storeUndo(this.currentPerformer.getUndo());
    }

    private void pre(final SnipeData v, Block targetBlock) {
        BlockFace blockFace = getTargetBlock().getFace(this.getLastBlock());
        if (blockFace == null) {
            return;
        }
        switch (blockFace) {
            case NORTH:
            case SOUTH:
                this.discNS(v, targetBlock);
                break;

            case EAST:
            case WEST:
                this.discEW(v, targetBlock);
                break;

            case UP:
            case DOWN:
                this.discUD(v, targetBlock);
                break;

            default:
                break;
        }
    }

    @Override
    protected final void arrow(final SnipeData v) {
        this.pre(v, this.getTargetBlock());
    }

    @Override
    protected final void powder(final SnipeData v) {
        this.pre(v, this.getLastBlock());
    }

    @Override
    public final void info(final VoxelMessage vm) {
        vm.brushName(this.getName());
        vm.size();
    }

    @Override
    public final void parseParameters(final String triggerHandle, final String[] params, final SnipeData v) {
        if (params[0].equalsIgnoreCase("info")) {
            v.sendMessage(Component.text("Disc Face Brush Parameters:").color(NamedTextColor.GOLD));
            v.sendMessage(Component.text("/b " + triggerHandle + " smooth  -- Toggle smooth circle (default: false)").color(NamedTextColor.AQUA));
            return;
        }

        if (params[0].startsWith("smooth")) {
            this.smoothCircle = !this.smoothCircle;
            v.sendMessage(Component.text("Using smooth circle: " + this.smoothCircle).color(NamedTextColor.AQUA));

            return;
        }

        v.sendMessage(Component.text("Invalid parameter! Use ").color(NamedTextColor.RED)
                .append(Component.text("'/b " + triggerHandle + " info'").color(NamedTextColor.LIGHT_PURPLE))
                .append(Component.text(" to display valid parameters."))
        );
        sendPerformerMessage(triggerHandle, v);
    }

    @Override
    public List<String> registerArguments() {
        List<String> arguments = new ArrayList<>();
        arguments.addAll(Lists.newArrayList("smooth"));

        arguments.addAll(super.registerArguments());
        return arguments;
    }

    @Override
    public String getPermissionNode() {
        return "voxelsniper.brush.discface";
    }
}
