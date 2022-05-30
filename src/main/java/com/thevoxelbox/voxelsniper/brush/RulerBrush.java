package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.VoxelMessage;
import com.thevoxelbox.voxelsniper.snipe.SnipeData;
import com.thevoxelbox.voxelsniper.snipe.Undo;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.util.Vector;

/**
 * http://www.voxelwiki.com/minecraft/Voxelsniper#The_Ruler_Brush
 *
 * @author Gavjenks
 */
public class RulerBrush extends Brush {

    private boolean first = true;
    private Vector coords = new Vector(0, 0, 0);

    private int xOff = 0;
    private int yOff = 0;
    private int zOff = 0;

    /**
     *
     */
    public RulerBrush() {
        this.setName("Ruler");
    }

    @Override
    protected final void arrow(final SnipeData v) {
        final Material voxelMaterial = v.getVoxelMaterial();
        this.coords = this.getTargetBlock().getLocation().toVector();

        if (this.xOff == 0 && this.yOff == 0 && this.zOff == 0) {
            v.sendMessage(Component.text("First point selected.").color(NamedTextColor.DARK_PURPLE));
            this.first = !this.first;
        } else {
            final Undo undo = new Undo();

            undo.put(this.clampY(this.getTargetBlock().getX() + this.xOff, this.getTargetBlock().getY() + this.yOff, this.getTargetBlock().getZ() + this.zOff));
            this.setBlockMaterialAt(this.getTargetBlock().getZ() + this.zOff, this.getTargetBlock().getX() + this.xOff, this.getTargetBlock().getY() + this.yOff, voxelMaterial);
            v.owner().storeUndo(undo);
        }
    }

    @Override
    protected final void powder(final SnipeData v) {
        if (this.coords == null || this.coords.lengthSquared() == 0) {
            v.getVoxelMessage().brushMessageError("Warning: You did not select a first coordinate with the arrow. Comparing to point 0,0,0 instead.");
            return;
        }

        final double distance = (double) (Math.round(this.getTargetBlock().getLocation().toVector().subtract(this.coords).length() * 100) / 100);
        final double blockDistance = (double) (Math.round((Math.abs(Math.max(Math.max(Math.abs(this.getTargetBlock().getX() - coords.getX()), Math.abs(this.getTargetBlock().getY() - this.coords.getY())), Math.abs(this.getTargetBlock().getZ() - this.coords.getZ()))) + 1) * 100) / 100);
        v.sendMessage(Component.text("Format = (second coord - first coord)").color(NamedTextColor.BLUE)
                .append(Component.newline())
                .append(Component.text("X change: " + (this.getTargetBlock().getX() - this.coords.getX())).color(NamedTextColor.AQUA))
                .append(Component.newline())
                .append(Component.text("Y change: " + (this.getTargetBlock().getY() - this.coords.getY())).color(NamedTextColor.AQUA))
                .append(Component.newline())
                .append(Component.text("Z change: " + (this.getTargetBlock().getZ() - this.coords.getZ())).color(NamedTextColor.AQUA))
                .append(Component.newline())
                .append(Component.text("Euclidean distance = " + distance).color(NamedTextColor.AQUA))
                .append(Component.newline())
                .append(Component.text("Block distance = " + blockDistance).color(NamedTextColor.AQUA))
        );
    }

    @Override
    public final void info(final VoxelMessage vm) {
        vm.brushName(this.getName());
        vm.voxel();
    }

    @Override
    // TODO: Implement block placing
    public final void parseParameters(final String triggerHandle, final String[] params, final SnipeData v) {
        if (params[0].equalsIgnoreCase("info")) {
            v.getVoxelMessage().commandParameters(null
                    , Component.text("Instructions: Right click first point with the arrow. Right click with powder for distances from that block (can repeat without getting a new first block.)").color(NamedTextColor.BLUE)
            );
            return;
        }

        v.getVoxelMessage().invalidUseParameter(triggerHandle);
    }

    @Override
    public String getPermissionNode() {
        return "voxelsniper.brush.ruler";
    }
}
