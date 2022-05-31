package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.VoxelMessage;
import com.thevoxelbox.voxelsniper.snipe.SnipeData;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

/**
 * http://www.voxelwiki.com/minecraft/Voxelsniper#The_Volt-Meter_Brush
 *
 * @author Gavjenks
 */
public class VoltMeterBrush extends Brush {

    /**
     *
     */
    public VoltMeterBrush() {
        this.setName("VoltMeter");
    }

    private void data(final SnipeData v) {
        final Block block = this.clampY(this.getTargetBlock().getX(), this.getTargetBlock().getY(), this.getTargetBlock().getZ());
        final var data = block.getBlockData();
        v.sendMessage(Component.text("Blocks until repeater needed: " + data).color(NamedTextColor.AQUA));
    }

    private void volt(final SnipeData v) {
        final Block block = this.clampY(this.getTargetBlock().getX(), this.getTargetBlock().getY(), this.getTargetBlock().getZ());
        final boolean indirect = block.isBlockIndirectlyPowered();
        final boolean direct = block.isBlockPowered();
        v.sendMessage(Component.text("Direct Power? " + direct + " Indirect Power? " + indirect).color(NamedTextColor.AQUA)
                .append(Component.newline())
                .append(Component.text("Top Direct? " + block.isBlockFacePowered(BlockFace.UP) + " Top Indirect? " + block.isBlockFaceIndirectlyPowered(BlockFace.UP)).color(NamedTextColor.BLUE))
                .append(Component.newline())
                .append(Component.text("Bottom Direct? " + block.isBlockFacePowered(BlockFace.DOWN) + " Bottom Indirect? " + block.isBlockFaceIndirectlyPowered(BlockFace.DOWN)).color(NamedTextColor.BLUE))
                .append(Component.newline())
                .append(Component.text("East Direct? " + block.isBlockFacePowered(BlockFace.EAST) + " East Indirect? " + block.isBlockFaceIndirectlyPowered(BlockFace.EAST)).color(NamedTextColor.BLUE))
                .append(Component.newline())
                .append(Component.text("West Direct? " + block.isBlockFacePowered(BlockFace.WEST) + " West Indirect? " + block.isBlockFaceIndirectlyPowered(BlockFace.WEST)).color(NamedTextColor.BLUE))
                .append(Component.newline())
                .append(Component.text("North Direct? " + block.isBlockFacePowered(BlockFace.NORTH) + " North Indirect? " + block.isBlockFaceIndirectlyPowered(BlockFace.NORTH)).color(NamedTextColor.BLUE))
                .append(Component.newline())
                .append(Component.text("South Direct? " + block.isBlockFacePowered(BlockFace.SOUTH) + " South Indirect? " + block.isBlockFaceIndirectlyPowered(BlockFace.SOUTH)).color(NamedTextColor.BLUE))
        );
    }

    @Override
    protected final void arrow(final SnipeData v) {
        this.volt(v);
    }

    @Override
    protected final void powder(final SnipeData v) {
        this.data(v);
    }

    @Override
    public final void info(final VoxelMessage vm) {
        vm.brushName(this.getName());
        vm.brushMessage("Right click with arrow to see if blocks/faces are powered. Powder measures wire current.");
    }

    @Override
    public String getPermissionNode() {
        return "voxelsniper.brush.voltmeter";
    }
}
