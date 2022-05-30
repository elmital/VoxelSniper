package com.thevoxelbox.voxelsniper.brush;

import com.google.common.collect.Lists;
import com.thevoxelbox.voxelsniper.VoxelMessage;
import com.thevoxelbox.voxelsniper.brush.perform.PerformerBrush;
import com.thevoxelbox.voxelsniper.snipe.SnipeData;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author Kavutop
 */
public class CylinderBrush extends PerformerBrush {

    private static final double SMOOTH_CIRCLE_VALUE = 0.5;
    private static final double VOXEL_CIRCLE_VALUE = 0.0;

    private boolean smoothCircle = false;

    /**
     *
     */
    public CylinderBrush() {
        this.setName("Cylinder");
    }

    private void cylinder(final SnipeData v, Block targetBlock) {
        final int brushSize = v.getBrushSize();
        int yStartingPoint = targetBlock.getY() + v.getcCen();
        int yEndPoint = targetBlock.getY() + v.getVoxelHeight() + v.getcCen();

        if (yEndPoint < yStartingPoint) {
            yEndPoint = yStartingPoint;
        }
        if (yStartingPoint < this.getWorld().getMinHeight()) {
            yStartingPoint = this.getWorld().getMinHeight();
            v.sendMessage(Component.text("Warning: off-world start position.").color(NamedTextColor.DARK_PURPLE));
        } else if (yStartingPoint > this.getWorld().getMaxHeight() - 1) {
            yStartingPoint = this.getWorld().getMaxHeight() - 1;
            v.sendMessage(Component.text("Warning: off-world start position.").color(NamedTextColor.DARK_PURPLE));
        }
        if (yEndPoint < this.getWorld().getMinHeight()) {
            yEndPoint = this.getWorld().getMinHeight();
            v.sendMessage(Component.text("Warning: off-world start position.").color(NamedTextColor.DARK_PURPLE));
        } else if (yEndPoint > this.getWorld().getMaxHeight() - 1) {
            yEndPoint = this.getWorld().getMaxHeight() - 1;
            v.sendMessage(Component.text("Warning: off-world start position.").color(NamedTextColor.DARK_PURPLE));
        }

        final double bSquared = Math.pow(brushSize + (smoothCircle ? SMOOTH_CIRCLE_VALUE : VOXEL_CIRCLE_VALUE), 2);

        for (int y = yEndPoint; y >= yStartingPoint; y--) {
            for (int x = brushSize; x >= 0; x--) {
                final double xSquared = Math.pow(x, 2);

                for (int z = brushSize; z >= 0; z--) {
                    if ((xSquared + Math.pow(z, 2)) <= bSquared) {
                        this.currentPerformer.perform(this.clampY(targetBlock.getX() + x, y, targetBlock.getZ() + z));
                        this.currentPerformer.perform(this.clampY(targetBlock.getX() + x, y, targetBlock.getZ() - z));
                        this.currentPerformer.perform(this.clampY(targetBlock.getX() - x, y, targetBlock.getZ() + z));
                        this.currentPerformer.perform(this.clampY(targetBlock.getX() - x, y, targetBlock.getZ() - z));
                    }
                }
            }
        }
        v.owner().storeUndo(this.currentPerformer.getUndo());
    }

    @Override
    protected final void arrow(final SnipeData v) {
        this.cylinder(v, this.getTargetBlock());
    }

    @Override
    protected final void powder(final SnipeData v) {
        this.cylinder(v, this.getLastBlock());
    }

    @Override
    public final void info(final VoxelMessage vm) {
        vm.brushName(this.getName());
        vm.size();
        vm.height();
        vm.center();
    }

    @Override
    public final void parseParameters(final String triggerHandle, final String[] params, final SnipeData v) {
        if (params[0].equalsIgnoreCase("info")) {
            v.getVoxelMessage().commandParameters("Cylinder Brush Parameters:"
                    , null
                    , "/b " + triggerHandle + " height [number]  -- Set voxel height (default: 1)"
                    , "/b " + triggerHandle + " shift [number]  -- Shifts the cylinder by [number] blocks on the y-axis (default: 0)"
                    , "/b " + triggerHandle + " smooth  -- Toggle smooth circle (default: false)"
            );
            return;
        }

        if (params[0].startsWith("smooth")) {
            this.smoothCircle = !this.smoothCircle;
            v.sendMessage(Component.text("Using smooth circles: " + this.smoothCircle).color(NamedTextColor.AQUA));
            return;
        }

        if (params[0].startsWith("height")) {
            try {
                v.setVoxelHeight(Integer.parseInt(params[1]));
                v.getVoxelMessage().height();
                return;
            } catch (NumberFormatException | ArrayIndexOutOfBoundsException ignored) {
            }
        }

        if (params[0].startsWith("shift")) {
            try {
                v.setcCen(Integer.parseInt(params[1]));
                v.sendMessage(Component.text("Cylinder will shift by " + v.getcCen() + " blocks on y-axis").color(NamedTextColor.AQUA));
                return;
            } catch (NumberFormatException | ArrayIndexOutOfBoundsException ignored) {
            }
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
        arguments.addAll(Lists.newArrayList("shift", "height", "smooth"));

        arguments.addAll(super.registerArguments());
        return arguments;
    }

    @Override
    public HashMap<String, List<String>> registerArgumentValues() {
        HashMap<String, List<String>> argumentValues = new HashMap<>();

        
        argumentValues.put("shift", Lists.newArrayList("[number]"));
        
        argumentValues.put("height", Lists.newArrayList("[number]"));

        argumentValues.putAll(super.registerArgumentValues());
        return argumentValues;
    }

    @Override
    public String getPermissionNode() {
        return "voxelsniper.brush.cylinder";
    }
}
