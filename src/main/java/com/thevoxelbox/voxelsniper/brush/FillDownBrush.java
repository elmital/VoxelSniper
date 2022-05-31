package com.thevoxelbox.voxelsniper.brush;

import com.google.common.collect.Lists;
import com.thevoxelbox.voxelsniper.VoxelMessage;
import com.thevoxelbox.voxelsniper.brush.perform.PerformerBrush;
import com.thevoxelbox.voxelsniper.snipe.SnipeData;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Voxel
 */
public class FillDownBrush extends PerformerBrush {

    private static final double SMOOTH_CIRCLE_VALUE = 0.5;
    private static final double VOXEL_CIRCLE_VALUE = 0.0;

    private boolean smoothCircle = false;

    private boolean fillLiquid = true;
    private boolean fromExisting = false;

    /**
     *
     */
    public FillDownBrush() {
        this.setName("Fill Down");
    }

    private void fillDown(final SnipeData v) {
        final int brushSize = v.getBrushSize();
        final double brushSizeSquared = Math.pow(brushSize + (smoothCircle ? SMOOTH_CIRCLE_VALUE : VOXEL_CIRCLE_VALUE), 2);
        final Block targetBlock = this.getTargetBlock();
        for (int x = -brushSize; x <= brushSize; x++) {
            final double currentXSquared = Math.pow(x, 2);

            for (int z = -brushSize; z <= brushSize; z++) {
                if (currentXSquared + Math.pow(z, 2) <= brushSizeSquared) {
                    int y = 0;
                    boolean found = false;
                    if (this.fromExisting) {
                        for (y = -v.getVoxelHeight(); y < v.getVoxelHeight(); y++) {
                            final Block currentBlock = this.getWorld().getBlockAt(
                                    targetBlock.getX() + x,
                                    targetBlock.getY() + y,
                                    targetBlock.getZ() + z);
                            if (!currentBlock.isEmpty()) {
                                found = true;
                                break;
                            }
                        }
                        if (!found) {
                            continue;
                        }
                        y--;
                    }
                    for (; y >= -targetBlock.getY(); --y) {
                        final Block currentBlock = this.getWorld().getBlockAt(
                                targetBlock.getX() + x,
                                targetBlock.getY() + y,
                                targetBlock.getZ() + z);
                        if (currentBlock.isEmpty() || (fillLiquid && currentBlock.isLiquid())) {
                            this.currentPerformer.perform(currentBlock);
                        } else {
                            break;
                        }
                    }
                }
            }
        }

        v.owner().storeUndo(this.currentPerformer.getUndo());
    }

    @Override
    protected final void arrow(final SnipeData v) {
        this.fillDown(v);
    }

    @Override
    protected final void powder(final SnipeData v) {
        this.fillDown(v);
    }

    @Override
    public final void info(final VoxelMessage vm) {
        vm.brushName(this.getName());
        vm.size();
    }

    @Override
    public final void parseParameters(final String triggerHandle, final String[] params, final SnipeData v) {
        if (params[0].equalsIgnoreCase("info")) {
            v.getVoxelMessage().commandParameters("Fill Down Parameters:"
                    , null
                    , "/b " + triggerHandle + " smooth  -- Toggle use smooth circles (default: false)"
                    , "/b " + triggerHandle + " liquid  -- Toggle filling liquids (default: true)"
                    , "/b " + triggerHandle + " existing  -- Toggle filling existing blocks or all blocks. (Toggle)"
            );
            return;
        }

        if (params[0].equalsIgnoreCase("liquid")) {
            this.fillLiquid = !this.fillLiquid;
            v.sendMessage(Component.text("Now filling " + ((this.fillLiquid) ? "liquid and air" : "air only") + ".").color(NamedTextColor.AQUA));
            return;
        }

        if (params[0].equalsIgnoreCase("existing")) {
            this.fromExisting = !this.fromExisting;
            v.sendMessage(Component.text("Now filling down from " + ((this.fromExisting) ? "existing" : "all") + " blocks.").color(NamedTextColor.AQUA));
            return;
        }

        if (params[0].startsWith("smooth")) {
            this.smoothCircle = !this.smoothCircle;
            v.sendMessage(Component.text("Using smooth circle: " + this.smoothCircle).color(NamedTextColor.AQUA));
            return;
        }

        v.getVoxelMessage().invalidUseParameter(triggerHandle);
        sendPerformerMessage(triggerHandle, v);
    }

    @Override
    public List<String> registerArguments() {
        List<String> arguments = new ArrayList<>();
        arguments.addAll(Lists.newArrayList("smooth", "liquid", "existing"));

        arguments.addAll(super.registerArguments());
        return arguments;
    }

    @Override
    public String getPermissionNode() {
        return "voxelsniper.brush.filldown";
    }
}
