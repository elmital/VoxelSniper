package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.VoxelMessage;
import com.thevoxelbox.voxelsniper.snipe.SnipeData;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 */
public class BiomeBrush extends Brush {

    private Biome selectedBiome = Biome.PLAINS;

    public BiomeBrush() {
        this.setName("Biome");
    }

    private void biome(final SnipeData v) {
        final int brushSize = v.getBrushSize();
        final double brushSizeSquared = Math.pow(brushSize, 2);

        for (int x = -brushSize; x <= brushSize; x++) {
            final double xSquared = Math.pow(x, 2);

            for (int z = -brushSize; z <= brushSize; z++) {
                if ((xSquared + Math.pow(z, 2)) <= brushSizeSquared) {
                    this.getWorld().setBiome(this.getTargetBlock().getX() + x, this.getTargetBlock().getZ() + z, this.selectedBiome);
                }
            }
        }

        final Block block1 = this.getWorld().getBlockAt(this.getTargetBlock().getX() - brushSize, 0, this.getTargetBlock().getZ() - brushSize);
        final Block block2 = this.getWorld().getBlockAt(this.getTargetBlock().getX() + brushSize, 0, this.getTargetBlock().getZ() + brushSize);

        final int lowChunkX = (block1.getX() <= block2.getX()) ? block1.getChunk().getX() : block2.getChunk().getX();
        final int lowChunkZ = (block1.getZ() <= block2.getZ()) ? block1.getChunk().getZ() : block2.getChunk().getZ();
        final int highChunkX = (block1.getX() >= block2.getX()) ? block1.getChunk().getX() : block2.getChunk().getX();
        final int highChunkZ = (block1.getZ() >= block2.getZ()) ? block1.getChunk().getZ() : block2.getChunk().getZ();

        for (int x = lowChunkX; x <= highChunkX; x++) {
            for (int z = lowChunkZ; z <= highChunkZ; z++) {
                this.getWorld().refreshChunk(x, z);
            }
        }
    }

    @Override
    protected final void arrow(final SnipeData v) {
        this.biome(v);
    }

    @Override
    protected final void powder(final SnipeData v) {
        this.biome(v);
    }

    @Override
    public final void info(final VoxelMessage vm) {
        vm.brushName(this.getName());
        vm.size();
        vm.custom(Component.text("Currently selected biome type: ").color(NamedTextColor.GOLD).append(Component.text(this.selectedBiome.name()).color(NamedTextColor.DARK_GREEN)));
    }

    @Override
    public final void parseParameters(final String triggerHandle, final String[] params, final SnipeData v) {
        if (params[0].equalsIgnoreCase("info")) {
            v.getVoxelMessage().commandParameters("Biome Brush Parameters:", null,"/b " + triggerHandle + " [biomeType] -- Change brush to the specified biome");
            return;
        }

        try {
            this.selectedBiome = Biome.valueOf(params[0].toUpperCase());
            v.sendMessage(Component.text("Currently selected biome type: ").color(NamedTextColor.GOLD).append(Component.text(this.selectedBiome.name()).color(NamedTextColor.DARK_GREEN)));
        } catch (IllegalArgumentException e) {
            v.sendMessage(Component.text("That biome does not exist.").color(NamedTextColor.RED));
        }
    }

    @Override
    public List<String> registerArguments() {
        List<String> arguments = new ArrayList<>();
        
        arguments.addAll(Arrays.stream(Biome.values()).map(e -> e.name()).collect(Collectors.toList()));
        
        return arguments;
    }

    @Override
    public String getPermissionNode() {
        return "voxelsniper.brush.biome";
    }
}
