package com.thevoxelbox.voxelsniper.brush;

import com.google.common.collect.Lists;
import com.thevoxelbox.voxelsniper.VoxelMessage;
import com.thevoxelbox.voxelsniper.snipe.SnipeData;
import com.thevoxelbox.voxelsniper.snipe.Undo;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

/**
 * http://www.voxelwiki.com/minecraft/Voxelsniper#Clean_Snow_Brush
 *
 * @author psanker
 */
public class CleanSnowBrush extends Brush {

    public static final double SMOOTH_SPHERE_VALUE = 0.5;
    public static final int VOXEL_SPHERE_VALUE = 0;

    private boolean smoothSphere = false;

    /**
     *
     */
    public CleanSnowBrush() {
        this.setName("Clean Snow");
    }

    private void cleanSnow(final SnipeData v) {
        final int brushSize = v.getBrushSize();
        final double brushSizeSquared = Math.pow(brushSize + (this.smoothSphere ? SMOOTH_SPHERE_VALUE : VOXEL_SPHERE_VALUE), 2);
        final Undo undo = new Undo();

        for (int y = (brushSize + 1) * 2; y >= 0; y--) {
            final double ySquared = Math.pow(y - brushSize, 2);

            for (int x = (brushSize + 1) * 2; x >= 0; x--) {
                final double xSquared = Math.pow(x - brushSize, 2);

                for (int z = (brushSize + 1) * 2; z >= 0; z--) {
                    if ((xSquared + Math.pow(z - brushSize, 2) + ySquared) <= brushSizeSquared) {
                        if ((this.clampY(this.getTargetBlock().getX() + x - brushSize, this.getTargetBlock().getY() + z - brushSize, this.getTargetBlock().getZ() + y - brushSize).getType() == Material.SNOW) && ((this.clampY(this.getTargetBlock().getX() + x - brushSize, this.getTargetBlock().getY() + z - brushSize - 1, this.getTargetBlock().getZ() + y - brushSize).getType() == Material.SNOW) || (this.clampY(this.getTargetBlock().getX() + x - brushSize, this.getTargetBlock().getY() + z - brushSize - 1, this.getTargetBlock().getZ() + y - brushSize).getType().isAir()))) {
                            undo.put(this.clampY(this.getTargetBlock().getX() + x, this.getTargetBlock().getY() + z, this.getTargetBlock().getZ() + y));
                            this.setBlockMaterialAt(this.getTargetBlock().getZ() + y - brushSize, this.getTargetBlock().getX() + x - brushSize, this.getTargetBlock().getY() + z - brushSize, Material.AIR);
                        }

                    }
                }
            }
        }

        v.owner().storeUndo(undo);
    }

    @Override
    protected final void arrow(final SnipeData v) {
        this.cleanSnow(v);
    }

    @Override
    protected final void powder(final SnipeData v) {
        this.cleanSnow(v);
    }

    @Override
    public final void info(final VoxelMessage vm) {
        vm.brushName(this.getName());
        vm.size();
    }

    @Override
    public final void parseParameters(final String triggerHandle, final String[] params, final SnipeData v) {
        if (params[0].equalsIgnoreCase("info")) {
            v.getVoxelMessage().commandParameters("Clean Snow Brush Parameters:", null, "/b " + triggerHandle + " smooth -- Toggle using smooth sphere algorithm (default: false)");
            return;
        }

        if (params[0].equalsIgnoreCase("smooth")) {
            this.smoothSphere = !this.smoothSphere;
            v.sendMessage(Component.text("Smooth sphere algorithm: " + this.smoothSphere).color(NamedTextColor.AQUA));
            return;
        }

        v.sendMessage(
                Component.text("Invalid parameter! Use ").color(NamedTextColor.RED)
                        .append(Component.text("'/b " + triggerHandle + " info'").color(NamedTextColor.LIGHT_PURPLE))
                        .append(Component.text(" to display valid parameters.").color(NamedTextColor.RED))
        );
    }

    @Override
    public List<String> registerArguments() {
        return new ArrayList<>(Lists.newArrayList("smooth"));
    }

    @Override
    public String getPermissionNode() {
        return "voxelsniper.brush.cleansnow";
    }
}
