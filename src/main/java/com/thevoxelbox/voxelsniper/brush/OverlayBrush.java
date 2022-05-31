package com.thevoxelbox.voxelsniper.brush;

import com.google.common.collect.Lists;
import com.thevoxelbox.voxelsniper.VoxelMessage;
import com.thevoxelbox.voxelsniper.brush.perform.PerformerBrush;
import com.thevoxelbox.voxelsniper.snipe.SnipeData;
import com.thevoxelbox.voxelsniper.util.VoxelList;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author Gavjenks
 */
public class OverlayBrush extends PerformerBrush {

    private static final int DEFAULT_DEPTH = 3;
    private int depth = DEFAULT_DEPTH;

    private boolean allBlocks = false;
    private boolean useVoxelList = false;

    public OverlayBrush() {
        this.setName("Overlay (Topsoil Filling)");
    }

    private void overlay(final SnipeData v) {
        final int brushSize = v.getBrushSize();
        final double brushSizeSquared = Math.pow(brushSize + 0.5, 2);

        for (int z = brushSize; z >= -brushSize; z--) {
            for (int x = brushSize; x >= -brushSize; x--) {
                // check if column is valid
                // column is valid if it has no solid block right above the clicked layer
                final Material material = this.getBlockMaterialAt(this.getTargetBlock().getX() + x, this.getTargetBlock().getY() + 1, this.getTargetBlock().getZ() + z);
                if (isIgnoredBlock(material)) {
                    if ((Math.pow(x, 2) + Math.pow(z, 2)) <= brushSizeSquared) {
                        for (int y = this.getTargetBlock().getY(); y > 0; y--) { //TODO potentially broken with new world height
                            // check for surface
                            final Material layerBlock = this.getBlockMaterialAt(this.getTargetBlock().getX() + x, y, this.getTargetBlock().getZ() + z);
                            if (!isIgnoredBlock(layerBlock)) {
                                for (int currentDepth = y; y - currentDepth < depth; currentDepth--) {
                                    final Material currentBlock = this.getBlockMaterialAt(this.getTargetBlock().getX() + x, currentDepth, this.getTargetBlock().getZ() + z);
                                    if (isOverrideableMaterial(v.getVoxelList(), currentBlock)) {
                                        this.currentPerformer.perform(this.clampY(this.getTargetBlock().getX() + x, currentDepth, this.getTargetBlock().getZ() + z));
                                    }
                                }
                                break;
                            }
                        }
                    }
                }
            }
        }

        v.owner().storeUndo(this.currentPerformer.getUndo());
    }

    private void overlayTwo(final SnipeData v) {
        final int brushSize = v.getBrushSize();
        final double brushSizeSquared = Math.pow(brushSize + 0.5, 2);
        final int[][] memory = new int[brushSize * 2 + 1][brushSize * 2 + 1];

        for (int z = brushSize; z >= -brushSize; z--) {
            for (int x = brushSize; x >= -brushSize; x--) {
                boolean surfaceFound = false;
                for (int y = this.getTargetBlock().getY(); y > 0 && !surfaceFound; y--) { // start scanning from the height you clicked at //TODO potentially broken with new world height
                    if (memory[x + brushSize][z + brushSize] != 1) { // if haven't already found the surface in this column
                        if ((Math.pow(x, 2) + Math.pow(z, 2)) <= brushSizeSquared) { // if inside of the column...
                            if (!this.getBlockMaterialAt(this.getTargetBlock().getX() + x, y - 1, this.getTargetBlock().getZ() + z).isAir()) { // if not a floating block (like one of Notch's world pools)
                                if (this.getBlockMaterialAt(this.getTargetBlock().getX() + x, y + 1, this.getTargetBlock().getZ() + z).isAir()) { // must start at surface... this prevents it filling stuff in if
                                    // you click in a wall, and it starts out below surface.
                                    final Material currentBlock = this.getBlockMaterialAt(this.getTargetBlock().getX() + x, y, this.getTargetBlock().getZ() + z);
                                    if (this.isOverrideableMaterial(v.getVoxelList(), currentBlock)) {
                                        for (int d = 1; (d < this.depth + 1); d++) {
                                            this.currentPerformer.perform(this.clampY(this.getTargetBlock().getX() + x, y + d, this.getTargetBlock().getZ() + z)); // fills down as many layers as you specify
                                            // in parameters
                                            memory[x + brushSize][z + brushSize] = 1; // stop it from checking any other blocks in this vertical 1x1 column.
                                        }
                                        surfaceFound = true;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        v.owner().storeUndo(this.currentPerformer.getUndo());
    }

    @SuppressWarnings("deprecation")
    private boolean isIgnoredBlock(Material material) {
        return material == Material.WATER || material.isTransparent() || material == Material.CACTUS;
    }

    private boolean isOverrideableMaterial(VoxelList list, Material material) {
        if (this.useVoxelList) {
            return list.contains(material);
        }

        if (allBlocks && !(material.isAir())) {
            return true;
        }

        return switch (material) {
            case STONE, ANDESITE, DIORITE, GRANITE, GRASS_BLOCK, DIRT, COARSE_DIRT, PODZOL, SAND, RED_SAND, GRAVEL, SANDSTONE, MOSSY_COBBLESTONE, CLAY, SNOW, OBSIDIAN -> true;
            default -> false;
        };
    }

    @Override
    protected final void arrow(final SnipeData v) {
        if (this.useVoxelList && v.getVoxelList().isEmpty()) {
            v.sendMessage(Component.text("Overlay mode is set to custom defined blocks, but the VoxelList is empty. ").color(NamedTextColor.DARK_AQUA)
                    .append(Component.text("Please use /vv list to see how to populate the list.").color(NamedTextColor.GOLD)));
            return;
        }
        this.overlay(v);
    }

    @Override
    protected final void powder(final SnipeData v) {
        this.overlayTwo(v);
    }

    @Override
    public final void info(final VoxelMessage vm) {
        vm.brushName(this.getName());
        vm.size();
        vm.custom(Component.text("Overlaying on " + (this.allBlocks ? "all" : (this.useVoxelList ? "custom defined" : "natural")) + " blocks").color(NamedTextColor.GOLD));
    }

    @Override
    public final void parseParameters(final String triggerHandle, final String[] params, final SnipeData v) {
        if (params[0].equalsIgnoreCase("info")) {
            v.getVoxelMessage().commandParameters("Overlay Brush Parameters:"
                    , null
                    , "/b " + triggerHandle + " depth [number]  -- Depth of blocks to overlay from surface"
                    , "/b " + triggerHandle + " mode  -- Change target blocks to natural, custom defined or all blocks."
            );
            return;
        }

        if (params[0].startsWith("depth")) {
            try {
                this.depth = Integer.parseInt(params[1]);

                if (this.depth < 1) {
                    this.depth = 1;
                }

                v.sendMessage(Component.text("Overlay depth set to " + this.depth).color(NamedTextColor.AQUA));
                return;
            } catch (NumberFormatException ignored) {
            }
        }

        if (params[0].startsWith("mode")) {
            if (!this.allBlocks && !this.useVoxelList) {
                this.allBlocks = true;
            } else if (this.allBlocks && !this.useVoxelList) {
                this.allBlocks = false;
                this.useVoxelList = true;
            } else if (!this.allBlocks) {
                this.useVoxelList = false;
            }
            v.sendMessage(Component.text("Will overlay on " + (this.allBlocks ? "all" : (this.useVoxelList ? "custom defined" : "natural")) + " blocks, " + this.depth + " blocks deep.").color(NamedTextColor.BLUE));
            return;
        }

        v.getVoxelMessage().invalidUseParameter(triggerHandle);
        sendPerformerMessage(triggerHandle, v);
    }

    @Override
    public List<String> registerArguments() {
        List<String> arguments = new ArrayList<>();
        arguments.addAll(Lists.newArrayList("depth", "mode"));

        arguments.addAll(super.registerArguments());
        return arguments;
    }

    @Override
    public HashMap<String, List<String>> registerArgumentValues() {
        HashMap<String, List<String>> argumentValues = new HashMap<>();

        argumentValues.put("depth", Lists.newArrayList("[number]"));

        argumentValues.putAll(super.registerArgumentValues());
        return argumentValues;
    }

    @Override
    public String getPermissionNode() {
        return "voxelsniper.brush.overlay";
    }
}
