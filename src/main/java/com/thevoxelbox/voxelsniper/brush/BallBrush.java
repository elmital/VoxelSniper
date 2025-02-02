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
 * @author Piotr
 */
public class BallBrush extends PerformerBrush {

    public static final double SMOOTH_SPHERE_VALUE = 0.5;
    public static final int VOXEL_SPHERE_VALUE = 0;

    private boolean smoothSphere = false;

    /**
     *
     */
    public BallBrush() {
        this.setName("Ball");
    }

    private void ball(final SnipeData v, Block targetBlock) {
        final int brushSize = v.getBrushSize();
        final double brushSizeSquared = Math.pow(brushSize + (this.smoothSphere ? SMOOTH_SPHERE_VALUE : VOXEL_SPHERE_VALUE), 2);

        int blockPositionX = targetBlock.getX();
        int blockPositionY = targetBlock.getY();
        int blockPositionZ = targetBlock.getZ();
        this.currentPerformer.perform(targetBlock);

        for (int z = 1; z <= brushSize; z++) {
            final double zSquared = Math.pow(z, 2);

            this.currentPerformer.perform(this.clampY(blockPositionX + z, blockPositionY, blockPositionZ));
            this.currentPerformer.perform(this.clampY(blockPositionX - z, blockPositionY, blockPositionZ));
            this.currentPerformer.perform(this.clampY(blockPositionX, blockPositionY + z, blockPositionZ));
            this.currentPerformer.perform(this.clampY(blockPositionX, blockPositionY - z, blockPositionZ));
            this.currentPerformer.perform(this.clampY(blockPositionX, blockPositionY, blockPositionZ + z));
            this.currentPerformer.perform(this.clampY(blockPositionX, blockPositionY, blockPositionZ - z));

            for (int x = 1; x <= brushSize; x++) {
                final double xSquared = Math.pow(x, 2);

                if (zSquared + xSquared <= brushSizeSquared) {
                    this.currentPerformer.perform(this.clampY(blockPositionX + z, blockPositionY, blockPositionZ + x));
                    this.currentPerformer.perform(this.clampY(blockPositionX + z, blockPositionY, blockPositionZ - x));
                    this.currentPerformer.perform(this.clampY(blockPositionX - z, blockPositionY, blockPositionZ + x));
                    this.currentPerformer.perform(this.clampY(blockPositionX - z, blockPositionY, blockPositionZ - x));
                    this.currentPerformer.perform(this.clampY(blockPositionX + z, blockPositionY + x, blockPositionZ));
                    this.currentPerformer.perform(this.clampY(blockPositionX + z, blockPositionY - x, blockPositionZ));
                    this.currentPerformer.perform(this.clampY(blockPositionX - z, blockPositionY + x, blockPositionZ));
                    this.currentPerformer.perform(this.clampY(blockPositionX - z, blockPositionY - x, blockPositionZ));
                    this.currentPerformer.perform(this.clampY(blockPositionX, blockPositionY + z, blockPositionZ + x));
                    this.currentPerformer.perform(this.clampY(blockPositionX, blockPositionY + z, blockPositionZ - x));
                    this.currentPerformer.perform(this.clampY(blockPositionX, blockPositionY - z, blockPositionZ + x));
                    this.currentPerformer.perform(this.clampY(blockPositionX, blockPositionY - z, blockPositionZ - x));
                }

                for (int y = 1; y <= brushSize; y++) {
                    if ((xSquared + Math.pow(y, 2) + zSquared) <= brushSizeSquared) {
                        this.currentPerformer.perform(this.clampY(blockPositionX + x, blockPositionY + y, blockPositionZ + z));
                        this.currentPerformer.perform(this.clampY(blockPositionX + x, blockPositionY + y, blockPositionZ - z));
                        this.currentPerformer.perform(this.clampY(blockPositionX - x, blockPositionY + y, blockPositionZ + z));
                        this.currentPerformer.perform(this.clampY(blockPositionX - x, blockPositionY + y, blockPositionZ - z));
                        this.currentPerformer.perform(this.clampY(blockPositionX + x, blockPositionY - y, blockPositionZ + z));
                        this.currentPerformer.perform(this.clampY(blockPositionX + x, blockPositionY - y, blockPositionZ - z));
                        this.currentPerformer.perform(this.clampY(blockPositionX - x, blockPositionY - y, blockPositionZ + z));
                        this.currentPerformer.perform(this.clampY(blockPositionX - x, blockPositionY - y, blockPositionZ - z));
                    }
                }
            }
        }

        v.owner().storeUndo(this.currentPerformer.getUndo());
    }

    @Override
    protected final void arrow(final SnipeData v) {
        this.ball(v, this.getTargetBlock());
    }

    @Override
    protected final void powder(final SnipeData v) {
        this.ball(v, this.getLastBlock());
    }

    @Override
    public final void info(final VoxelMessage vm) {
        vm.brushName(this.getName());
        vm.size();
    }

    @Override
    public final void parseParameters(final String triggerHandle, final String[] params, final SnipeData v) {
        if (params[0].equalsIgnoreCase("info")) {
            v.getVoxelMessage().commandParameters("Ball Brush Parameters:", null, "/b " + triggerHandle + " smooth -- Toggle using smooth sphere algorithm (default: false)");
            return;
        }

        if (params[0].equalsIgnoreCase("smooth")) {
            this.smoothSphere = !this.smoothSphere;
            v.sendMessage(Component.text("Smooth sphere algorithm: " + this.smoothSphere).color(NamedTextColor.AQUA));
            return;
        }

        v.sendMessage(Component.text("Invalid parameter! Use ").color(NamedTextColor.RED).append(Component.text("'/b " + triggerHandle + " info'").color(NamedTextColor.LIGHT_PURPLE)).append(Component.text(" to display valid parameters.").color(NamedTextColor.RED)));
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
        return "voxelsniper.brush.ball";
    }
}
