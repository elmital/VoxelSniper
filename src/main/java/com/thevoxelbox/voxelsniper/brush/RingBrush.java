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
 * @author Voxel
 */
public class RingBrush extends PerformerBrush {

    private static final double SMOOTH_CIRCLE_VALUE = 0.5;
    private static final double VOXEL_CIRCLE_VALUE = 0.0;

    private boolean smoothCircle = false;

    private double innerSize = 0;

    /**
     *
     */
    public RingBrush() {
        this.setName("Ring");
    }

    private void ring(final SnipeData v, Block targetBlock) {
        final int brushSize = v.getBrushSize();
        final double outerSquared = Math.pow(brushSize + (smoothCircle ? SMOOTH_CIRCLE_VALUE : VOXEL_CIRCLE_VALUE), 2);
        final double innerSquared = Math.pow(this.innerSize, 2);

        for (int x = brushSize; x >= 0; x--) {
            final double xSquared = Math.pow(x, 2);
            for (int z = brushSize; z >= 0; z--) {
                final double ySquared = Math.pow(z, 2);
                if ((xSquared + ySquared) <= outerSquared && (xSquared + ySquared) >= innerSquared) {
                    currentPerformer.perform(targetBlock.getRelative(x, 0, z));
                    currentPerformer.perform(targetBlock.getRelative(x, 0, -z));
                    currentPerformer.perform(targetBlock.getRelative(-x, 0, z));
                    currentPerformer.perform(targetBlock.getRelative(-x, 0, -z));
                }
            }
        }

        v.owner().storeUndo(this.currentPerformer.getUndo());
    }

    @Override
    protected final void arrow(final SnipeData v) {
        this.ring(v, this.getTargetBlock());
    }

    @Override
    protected final void powder(final SnipeData v) {
        this.ring(v, this.getLastBlock());
    }

    @Override
    public final void info(final VoxelMessage vm) {
        vm.brushName(this.getName());
        vm.size();
        vm.custom(Component.text("The inner radius is " + this.innerSize).color(NamedTextColor.AQUA));
    }

    @Override
    public final void parseParameters(final String triggerHandle, final String[] params, final SnipeData v) {
        if (params[0].equalsIgnoreCase("info")) {
            v.getVoxelMessage().commandParameters("Ring Brush Parameters:"
                    , null
                    , "/b " + triggerHandle + " smooth  -- Toggle smooth circle (default: false)"
                    , "/b " + triggerHandle + " inner [decimal]  -- Set inner radius to specified value"
            );
            return;
        }

        if (params[0].startsWith("smooth")) {
            this.smoothCircle = !this.smoothCircle;
            v.sendMessage(Component.text("Using smooth circle: " + this.smoothCircle).color(NamedTextColor.AQUA));
            return;
        }

        try {
            if (params[0].startsWith("inner")) {
                this.innerSize = Double.parseDouble(params[1]);
                v.sendMessage(Component.text("The inner radius has been set to ").color(NamedTextColor.AQUA).append(Component.text(this.innerSize).color(NamedTextColor.YELLOW)).append(Component.text(".")));
                return;
            }
        } catch (final NumberFormatException ignored) {
        }

       v.getVoxelMessage().invalidUseParameter(triggerHandle);
        sendPerformerMessage(triggerHandle, v);
    }

    @Override
    public List<String> registerArguments() {
        List<String> arguments = new ArrayList<>();
        arguments.addAll(Lists.newArrayList("smooth", "inner"));

        arguments.addAll(super.registerArguments());
        return arguments;
    }

    @Override
    public HashMap<String, List<String>> registerArgumentValues() {
        // Number variables
        HashMap<String, List<String>> argumentValues = new HashMap<>();
        
        argumentValues.put("inner", Lists.newArrayList("[decimal]"));

        argumentValues.putAll(super.registerArgumentValues());
        return argumentValues;
    }

    @Override
    public String getPermissionNode() {
        return "voxelsniper.brush.ring";
    }
}
