package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.VoxelMessage;
import com.thevoxelbox.voxelsniper.snipe.SnipeData;
import com.thevoxelbox.voxelsniper.snipe.Undo;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.TreeType;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.MultipleFacing;
import org.bukkit.block.data.Orientable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * @author Mick
 */
public class TreeSnipeBrush extends Brush {

    private TreeType treeType = TreeType.TREE;

    /**
     *
     */
    public TreeSnipeBrush() {
        this.setName("Tree Snipe");
    }

    private void single(final SnipeData v, Block targetBlock) {
        Undo undo = new Undo();
        undo.put(targetBlock);

        if(treeType.equals(TreeType.CHORUS_PLANT)) {
            var tar = targetBlock.getState();

            if(tar.getType().equals(Material.CHORUS_FLOWER)) {
                var data = Material.CHORUS_PLANT.createBlockData();
                ((MultipleFacing) data).setFace(BlockFace.DOWN, true);
                tar.setBlockData(data);
                tar.update(true, false);
            } else if(!tar.getType().equals(Material.CHORUS_PLANT)) {
                tar.setType(Material.END_STONE);
                tar.update(true);
            }
            var rel  = targetBlock.getRelative(BlockFace.UP).getState();
            rel.setType(Material.CHORUS_PLANT);
            rel.update(true, false);
        } else if(treeType.equals(TreeType.WARPED_FUNGUS) || treeType.equals(TreeType.CRIMSON_FUNGUS)) {
            var tar = targetBlock.getState();
            tar.setType(treeType.equals(TreeType.WARPED_FUNGUS) ? Material.WARPED_NYLIUM : Material.CRIMSON_NYLIUM);
            tar.update(true);
        }
        var generated = this.getWorld().generateTree(targetBlock.getRelative(BlockFace.UP).getLocation(), new Random()
                , this.treeType, blockState -> {
                    undo.put(blockState.getBlock());
                    if(blockState.getLocation().equals(targetBlock.getRelative(BlockFace.UP).getLocation())) {
                        if(!blockState.getType().isFuel() && !blockState.getType().equals(Material.WARPED_HYPHAE) && !blockState.getType().equals(Material.WARPED_STEM) && !blockState.getType().equals(Material.CRIMSON_HYPHAE) && !blockState.getType().equals(Material.CRIMSON_STEM) && !blockState.getType().equals(Material.CHORUS_PLANT)) {
                            blockState.setType(blockState.getBlock().getRelative(BlockFace.UP).getType());
                            blockState.update(true, false);
                        }
                    }
                });

        if(generated)
            v.owner().storeUndo(undo);
        else
            v.getVoxelMessage().brushMessageError("Three can't be generated here");
    }

    private void printTreeType(final VoxelMessage vm) {
        var printout = Component.empty();

        boolean delimiterHelper = true;
        for (final TreeType treeType : TreeType.values()) {
            if (delimiterHelper) {
                delimiterHelper = false;
            } else {
                printout = printout.append(Component.text(", ").color(NamedTextColor.DARK_GRAY));
            }
            printout = printout.append(Component.text(treeType.name().toLowerCase()).color((treeType.equals(this.treeType)) ? NamedTextColor.GRAY : NamedTextColor.DARK_GRAY));
        }

        vm.custom(printout);
    }

    @Override
    protected final void arrow(final SnipeData v) {
        this.single(v, getTargetBlock().getRelative(BlockFace.UP));
    }

    @Override
    protected final void powder(final SnipeData v) {
        this.single(v, getTargetBlock());
    }

    @Override
    public final void info(final VoxelMessage vm) {
        vm.brushName(this.getName());
        this.printTreeType(vm);
    }

    @Override
    public final void parseParameters(final String triggerHandle, final String[] params, final SnipeData v) {
        if (params[0].equalsIgnoreCase("info")) {
            v.getVoxelMessage().commandParameters("Tree Snipe Brush Parameters:", null, "/b " + triggerHandle + " [treeType]  -- Change tree type");
            return;
        }

        try {
            this.treeType = TreeType.valueOf(params[0].toUpperCase());
            this.printTreeType(v.getVoxelMessage());
        } catch (Exception e) {
            v.getVoxelMessage().invalidUseParameter(triggerHandle);
        }
    }

    @Override
    public List<String> registerArguments() {
        return new ArrayList<>(Arrays.stream(TreeType.values()).map(Enum::name).toList());
    }

    @Override
    public String getPermissionNode() {
        return "voxelsniper.brush.treesnipe";
    }
}
