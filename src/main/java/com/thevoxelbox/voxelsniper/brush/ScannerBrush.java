package com.thevoxelbox.voxelsniper.brush;

import com.google.common.collect.Lists;
import com.thevoxelbox.voxelsniper.VoxelMessage;
import com.thevoxelbox.voxelsniper.snipe.SnipeData;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author DivineRage
 */
public class ScannerBrush extends Brush {

    private static final int DEPTH_MIN = 1;
    private static final int DEPTH_DEFAULT = 24;
    private static final int DEPTH_MAX = 64;

    private int depth = DEPTH_DEFAULT;
    private List<Material> checkFor = List.of(Material.AIR, Material.CAVE_AIR, Material.VOID_AIR);

    /**
     *
     */
    public ScannerBrush() {
        this.setName("Scanner");
    }

    private int clamp(final int value, final int min, final int max) {
        if (value < min) {
            return min;
        } else return Math.min(value, max);
    }

    private void scan(final SnipeData v, final BlockFace bf) {
        if (bf == null) {
            return;
        }

        switch (bf) {
            case NORTH -> {
                // Scan south
                for (int i = 1; i < this.depth + 1; i++) {
                    var mat = this.clampY(this.getTargetBlock().getX() + i, this.getTargetBlock().getY(), this.getTargetBlock().getZ()).getType();
                    if (this.checkFor.contains(mat)) {
                        v.sendMessage(Component.text(mat + " found after " + i + " blocks.").color(NamedTextColor.GREEN));
                        return;
                    }
                }
                v.sendMessage(Component.text("Nope.").color(NamedTextColor.GRAY));
            }
            case SOUTH -> {
                // Scan north
                for (int i = 1; i < this.depth + 1; i++) {
                    var mat = this.clampY(this.getTargetBlock().getX() - i, this.getTargetBlock().getY(), this.getTargetBlock().getZ()).getType();
                    if (this.checkFor.contains(mat)) {
                        v.sendMessage(Component.text(mat + " found after " + i + " blocks.").color(NamedTextColor.GREEN));
                        return;
                    }
                }
                v.sendMessage(Component.text("Nope.").color(NamedTextColor.GRAY));
            }
            case EAST -> {
                // Scan west
                for (int i = 1; i < this.depth + 1; i++) {
                    var mat = this.clampY(this.getTargetBlock().getX(), this.getTargetBlock().getY(), this.getTargetBlock().getZ() + i).getType();
                    if (this.checkFor.contains(mat)) {
                        v.sendMessage(Component.text(mat + " found after " + i + " blocks.").color(NamedTextColor.GREEN));
                        return;
                    }
                }
                v.sendMessage(Component.text("Nope.").color(NamedTextColor.GRAY));
            }
            case WEST -> {
                // Scan east
                for (int i = 1; i < this.depth + 1; i++) {
                    var mat = this.clampY(this.getTargetBlock().getX(), this.getTargetBlock().getY(), this.getTargetBlock().getZ() - i).getType();
                    if (checkFor.contains(mat)) {
                        v.sendMessage(Component.text(mat + " found after " + i + " blocks.").color(NamedTextColor.GREEN));
                        return;
                    }
                }
                v.sendMessage(Component.text("Nope.").color(NamedTextColor.GRAY));
            }
            case UP -> {
                // Scan down
                for (int i = 1; i < this.depth + 1; i++) {
                    if ((this.getTargetBlock().getY() - i) <= 0) {
                        break;
                    }
                    var mat = this.clampY(this.getTargetBlock().getX(), this.getTargetBlock().getY() - i, this.getTargetBlock().getZ()).getType();
                    if (this.checkFor.contains(mat)) {
                        v.sendMessage(Component.text(mat + " found after " + i + " blocks.").color(NamedTextColor.GREEN));
                        return;
                    }
                }
                v.sendMessage(Component.text("Nope.").color(NamedTextColor.GRAY));
            }
            case DOWN -> {
                // Scan up
                for (int i = this.getWorld().getMinHeight() + 1; i < this.depth + 1; i++) {
                    if ((this.getTargetBlock().getY() + i) >= v.getWorld().getMaxHeight()) {
                        break;
                    }
                    var mat = this.clampY(this.getTargetBlock().getX(), this.getTargetBlock().getY() + i, this.getTargetBlock().getZ()).getType();
                    if (this.checkFor.contains(mat)) {
                        v.sendMessage(Component.text(mat + " found after " + i + " blocks.").color(NamedTextColor.GREEN));
                        return;
                    }
                }
                v.sendMessage(Component.text("Nope.").color(NamedTextColor.GRAY));
            }
            default -> {
            }
        }
    }

    @Override
    protected final void arrow(final SnipeData v) {
        this.checkFor = List.of(v.getVoxelMaterial());
        this.scan(v, this.getTargetBlock().getFace(this.getLastBlock()));
    }

    @Override
    public final void info(final VoxelMessage vm) {
        vm.brushName(this.getName());
        vm.custom(Component.text("Scanner depth set to " + this.depth).color(NamedTextColor.GREEN).append(Component.newline())
                .append(Component.text("Scanner scans for " + this.checkFor + " (change with /v #)")));
    }

    @Override
    public final void parseParameters(final String triggerHandle, final String[] params, final SnipeData v) {
        if (params[0].equalsIgnoreCase("info")) {
            v.getVoxelMessage().commandParameters("Scanner brush Parameters:"
                    , null
                    , "/b " + triggerHandle + " depth [number] -- will set the search depth to #. Clamps to 1 - 64."
            );
            return;
        }

        try {
            if (params[0].startsWith("depth")) {
                this.depth = this.clamp(Integer.parseInt(params[1]), DEPTH_MIN, DEPTH_MAX);
                v.sendMessage(Component.text("Scanner depth set to " + this.depth).color(NamedTextColor.AQUA));
                return;
            }
        } catch (NumberFormatException ignored) {
        }

        v.getVoxelMessage().invalidUseParameter(triggerHandle);
    }

    @Override
    public List<String> registerArguments() {
        return new ArrayList<>(Lists.newArrayList("depth"));
    }

    @Override
    public HashMap<String, List<String>> registerArgumentValues() {
        // Number variables
        HashMap<String, List<String>> argumentValues = new HashMap<>();
        
        argumentValues.put("depth", Lists.newArrayList("[number]"));
        
        return argumentValues;
    }

    @Override
    public String getPermissionNode() {
        return "voxelsniper.brush.scanner";
    }
}
