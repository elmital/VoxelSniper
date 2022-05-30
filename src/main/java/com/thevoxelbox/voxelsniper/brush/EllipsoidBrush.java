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
 * http://www.voxelwiki.com/minecraft/Voxelsniper#Ellipsoid_Brush
 *
 */
public class EllipsoidBrush extends PerformerBrush {

    private double xRad;
    private double yRad;
    private double zRad;
    private final boolean isTrue = false;

    /**
     *
     */
    public EllipsoidBrush() {
        this.setName("Ellipsoid");
    }

    private void execute(final SnipeData v, Block targetBlock) {
        this.currentPerformer.perform(targetBlock);
        double isTrueOffset = isTrue ? 0.5 : 0;
        int blockPositionX = targetBlock.getX();
        int blockPositionY = targetBlock.getY();
        int blockPositionZ = targetBlock.getZ();

        for (double x = 0; x <= xRad; x++) {

            final double xSquared = (x / (xRad + isTrueOffset)) * (x / (xRad + isTrueOffset));

            for (double z = 0; z <= zRad; z++) {

                final double zSquared = (z / (zRad + isTrueOffset)) * (z / (zRad + isTrueOffset));

                for (double y = 0; y <= yRad; y++) {

                    final double ySquared = (y / (yRad + isTrueOffset)) * (y / (yRad + isTrueOffset));

                    if (xSquared + ySquared + zSquared <= 1) {
                        this.currentPerformer.perform(this.clampY((int) (blockPositionX + x), (int) (blockPositionY + y), (int) (blockPositionZ + z)));
                        this.currentPerformer.perform(this.clampY((int) (blockPositionX + x), (int) (blockPositionY + y), (int) (blockPositionZ - z)));
                        this.currentPerformer.perform(this.clampY((int) (blockPositionX + x), (int) (blockPositionY - y), (int) (blockPositionZ + z)));
                        this.currentPerformer.perform(this.clampY((int) (blockPositionX + x), (int) (blockPositionY - y), (int) (blockPositionZ - z)));
                        this.currentPerformer.perform(this.clampY((int) (blockPositionX - x), (int) (blockPositionY + y), (int) (blockPositionZ + z)));
                        this.currentPerformer.perform(this.clampY((int) (blockPositionX - x), (int) (blockPositionY + y), (int) (blockPositionZ - z)));
                        this.currentPerformer.perform(this.clampY((int) (blockPositionX - x), (int) (blockPositionY - y), (int) (blockPositionZ + z)));
                        this.currentPerformer.perform(this.clampY((int) (blockPositionX - x), (int) (blockPositionY - y), (int) (blockPositionZ - z)));
                    }

                }
            }
        }

        v.owner().storeUndo(this.currentPerformer.getUndo());
    }

    @Override
    protected final void arrow(final SnipeData v) {
        this.execute(v, this.getTargetBlock());
    }

    @Override
    protected final void powder(final SnipeData v) {
        this.execute(v, this.getLastBlock());
    }

    @Override
    public final void info(final VoxelMessage vm) {
        vm.brushName(this.getName());
        vm.custom(Component.empty()
                .append(Component.text("X radius set to: ").color(NamedTextColor.AQUA).append(Component.text(this.xRad).color(NamedTextColor.DARK_AQUA)))
                .append(Component.newline())
                .append(Component.text("Y radius set to: ").color(NamedTextColor.AQUA).append(Component.text(this.yRad).color(NamedTextColor.DARK_AQUA)))
                .append(Component.newline())
                .append(Component.text("Z radius set to: ").color(NamedTextColor.AQUA).append(Component.text(this.zRad).color(NamedTextColor.DARK_AQUA)))
        );
    }

    @Override
    public final void parseParameters(final String triggerHandle, final String[] params, final com.thevoxelbox.voxelsniper.snipe.SnipeData v) {
        if (params[0].equalsIgnoreCase("info")) {
            v.getVoxelMessage().commandParameters("Ellipse Brush Parameters: ", null, "/b " + triggerHandle + " x [number]  -- Set X radius", "/b " + triggerHandle + " y [number]  -- Set Y radius", "/b " + triggerHandle + " z [number]  -- Set Z radius");
            return;
        }
        try {
            if (params[0].startsWith("x")) {
                this.xRad = Integer.parseInt(params[1]);
                v.sendMessage(Component.text("X radius set to: " + this.xRad).color(NamedTextColor.AQUA));
                return;
            }

            if (params[0].startsWith("y")) {
                this.yRad = Integer.parseInt(params[1]);
                v.sendMessage(Component.text("Y radius set to: " + this.yRad).color(NamedTextColor.AQUA));
                return;
            }

            if (params[0].startsWith("z")) {
                this.zRad = Integer.parseInt(params[1]);
                v.sendMessage(Component.text("Z radius set to: " + this.zRad).color(NamedTextColor.AQUA));
                return;
            }
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException ignored) {
        }

        v.getVoxelMessage().invalidUseParameter(triggerHandle);
        sendPerformerMessage(triggerHandle, v);
    }

    @Override
    public List<String> registerArguments() {
        List<String> arguments = new ArrayList<>();
        arguments.addAll(Lists.newArrayList("x", "y", "z"));

        arguments.addAll(super.registerArguments());
        return arguments;
    }

    @Override
    public HashMap<String, List<String>> registerArgumentValues() {
        HashMap<String, List<String>> argumentValues = new HashMap<>();
        
        argumentValues.put("x", Lists.newArrayList("[number]"));
        argumentValues.put("y", Lists.newArrayList("[number]"));
        argumentValues.put("z", Lists.newArrayList("[number]"));

        argumentValues.putAll(super.registerArgumentValues());
        return argumentValues;
    }

    @Override
    public String getPermissionNode() {
        return "voxelsniper.brush.ellipsoid";
    }
}
