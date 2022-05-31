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
 * http://www.voxelwiki.com/minecraft/Voxelsniper#The_Drain_Brush
 *
 * @author Gavjenks
 * @author psanker
 */
public class DrainBrush extends Brush {

    private final double trueCircle = 0.5;
    private boolean disc = false;

    /**
     *
     */
    public DrainBrush() {
        this.setName("Drain");
    }

    private void drain(final SnipeData v) {
        final int brushSize = v.getBrushSize();
        final double brushSizeSquared = Math.pow(brushSize + this.trueCircle, 2);
        final Undo undo = new Undo();

        if (this.disc) {
            for (int x = brushSize; x >= 0; x--) {
                final double xSquared = Math.pow(x, 2);

                for (int y = brushSize; y >= 0; y--) {
                    if ((xSquared + Math.pow(y, 2)) <= brushSizeSquared) {
                        if (this.getBlockMaterialAt(this.getTargetBlock().getX() + x, this.getTargetBlock().getY(), this.getTargetBlock().getZ() + y) == Material.WATER || this.getBlockMaterialAt(this.getTargetBlock().getX() + x, this.getTargetBlock().getY(), this.getTargetBlock().getZ() + y) == Material.LAVA) {
                            undo.put(this.clampY(this.getTargetBlock().getX() + x, this.getTargetBlock().getY(), this.getTargetBlock().getZ() + y));
                            this.setBlockMaterialAt(this.getTargetBlock().getZ() + y, this.getTargetBlock().getX() + x, this.getTargetBlock().getY(), Material.AIR);
                        }

                        if (this.getBlockMaterialAt(this.getTargetBlock().getX() + x, this.getTargetBlock().getY(), this.getTargetBlock().getZ() - y) == Material.WATER || this.getBlockMaterialAt(this.getTargetBlock().getX() + x, this.getTargetBlock().getY(), this.getTargetBlock().getZ() - y) == Material.LAVA) {
                            undo.put(this.clampY(this.getTargetBlock().getX() + x, this.getTargetBlock().getY(), this.getTargetBlock().getZ() - y));
                            this.setBlockMaterialAt(this.getTargetBlock().getZ() - y, this.getTargetBlock().getX() + x, this.getTargetBlock().getY(), Material.AIR);
                        }

                        if (this.getBlockMaterialAt(this.getTargetBlock().getX() - x, this.getTargetBlock().getY(), this.getTargetBlock().getZ() + y) == Material.WATER || this.getBlockMaterialAt(this.getTargetBlock().getX() - x, this.getTargetBlock().getY(), this.getTargetBlock().getZ() + y) == Material.LAVA) {
                            undo.put(this.clampY(this.getTargetBlock().getX() - x, this.getTargetBlock().getY(), this.getTargetBlock().getZ() + y));
                            this.setBlockMaterialAt(this.getTargetBlock().getZ() + y, this.getTargetBlock().getX() - x, this.getTargetBlock().getY(), Material.AIR);
                        }

                        if (this.getBlockMaterialAt(this.getTargetBlock().getX() - x, this.getTargetBlock().getY(), this.getTargetBlock().getZ() - y) == Material.WATER || this.getBlockMaterialAt(this.getTargetBlock().getX() - x, this.getTargetBlock().getY(), this.getTargetBlock().getZ() - y) == Material.LAVA) {
                            undo.put(this.clampY(this.getTargetBlock().getX() - x, this.getTargetBlock().getY(), this.getTargetBlock().getZ() - y));
                            this.setBlockMaterialAt(this.getTargetBlock().getZ() - y, this.getTargetBlock().getX() - x, this.getTargetBlock().getY(), Material.AIR);
                        }
                    }
                }
            }
        } else {
            for (int y = (brushSize + 1) * 2; y >= 0; y--) {
                final double ySquared = Math.pow(y - brushSize, 2);

                for (int x = (brushSize + 1) * 2; x >= 0; x--) {
                    final double xSquared = Math.pow(x - brushSize, 2);

                    for (int z = (brushSize + 1) * 2; z >= 0; z--) {
                        if ((xSquared + Math.pow(z - brushSize, 2) + ySquared) <= brushSizeSquared) {
                            if (this.getBlockMaterialAt(this.getTargetBlock().getX() + x - brushSize, this.getTargetBlock().getY() + z - brushSize, this.getTargetBlock().getZ() + y - brushSize) == Material.WATER || this.getBlockMaterialAt(this.getTargetBlock().getX() + x - brushSize, this.getTargetBlock().getY() + z - brushSize, this.getTargetBlock().getZ() + y - brushSize) == Material.LAVA) {
                                undo.put(this.clampY(this.getTargetBlock().getX() + x, this.getTargetBlock().getY() + z, this.getTargetBlock().getZ() + y));
                                this.setBlockMaterialAt(this.getTargetBlock().getZ() + y - brushSize, this.getTargetBlock().getX() + x - brushSize, this.getTargetBlock().getY() + z - brushSize, Material.AIR);
                            }
                        }
                    }
                }
            }
        }

        v.owner().storeUndo(undo);
    }

    @Override
    protected final void arrow(final SnipeData v) {
        this.drain(v);
    }

    @Override
    protected final void powder(final SnipeData v) {
        this.drain(v);
    }

    @Override
    public final void info(final VoxelMessage vm) {
        vm.brushName(this.getName());
        vm.size();

        vm.custom(
                Component.text(this.trueCircle == 0.5 ? "True circle mode ON" : "True circle mode OFF").color(NamedTextColor.AQUA)
                        .append(Component.newline())
                        .append(Component.text(this.disc ? "Disc drain mode ON" : "Disc drain mode OFF"))
        );
    }

    @Override
    public final void parseParameters(final String triggerHandle, final String[] params, final SnipeData v) {
        if (params[0].equalsIgnoreCase("info")) {
            v.getVoxelMessage().commandParameters("Drain Brush Parameters:", null, "/b " + triggerHandle + " shape  -- Toggle between brush shapes (default: ball)");
            return;
        }
        if (params[0].startsWith("shape")) {
            this.disc = !this.disc;
            v.sendMessage(Component.text("Drain Brush Shape: " + (this.disc ? "Disc" : "Ball")).color(NamedTextColor.AQUA));
            return;
        }

        v.sendMessage(Component.text("Invalid parameter! Use ").color(NamedTextColor.RED)
                .append(Component.text("'/b " + triggerHandle + " info'").color(NamedTextColor.LIGHT_PURPLE))
                .append(Component.text(" to display valid parameters."))
        );
    }

    @Override
    public List<String> registerArguments() {
        return new ArrayList<>(Lists.newArrayList("shape"));
    }

    @Override
    public String getPermissionNode() {
        return "voxelsniper.brush.drain";
    }
}
