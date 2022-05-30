package com.thevoxelbox.voxelsniper.brush;

import com.google.common.base.Objects;
import com.thevoxelbox.voxelsniper.VoxelMessage;
import com.thevoxelbox.voxelsniper.snipe.SnipeData;
import com.thevoxelbox.voxelsniper.snipe.Undo;
import com.thevoxelbox.voxelsniper.util.UndoDelegate;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.TreeType;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * http://www.voxelwiki.com/minecraft/Voxelsniper#The_Tree_Brush
 *
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

    @SuppressWarnings("deprecation")
    private void single(final SnipeData v, Block targetBlock) {
        UndoDelegate undoDelegate = new UndoDelegate(targetBlock.getWorld());
        Block blockBelow = targetBlock.getRelative(BlockFace.DOWN);
        BlockState currentState = blockBelow.getState();
        undoDelegate.setBlock(blockBelow);
        blockBelow.setType(Material.GRASS);
        this.getWorld().generateTree(targetBlock.getLocation(), this.treeType, undoDelegate);
        Undo undo = undoDelegate.getUndo();
        blockBelow.setBlockData(currentState.getBlockData().getMaterial().createBlockData(), true);
        undo.put(blockBelow);
        v.owner().storeUndo(undo);
    }

    private int getYOffset() {
        for (int i = getTargetBlock().getWorld().getMinHeight() + 1; i < (getTargetBlock().getWorld().getMaxHeight() - 1 - getTargetBlock().getY()); i++) {
            if (Objects.equal(getTargetBlock().getRelative(0, i + 1, 0).getType(), Material.AIR)) {
                return i;
            }
        }
        return 0;
    }

    private void printTreeType(final VoxelMessage vm) {
        var printout = Component.empty();

        boolean delimiterHelper = true;
        for (final TreeType treeType : TreeType.values()) {
            if (delimiterHelper) {
                delimiterHelper = false;
            } else {
                printout = printout.append(Component.text(", "));
            }
            printout = printout.append(Component.text(treeType.name().toLowerCase()).color((treeType.equals(this.treeType)) ? NamedTextColor.GRAY : NamedTextColor.DARK_GRAY));
        }

        vm.custom(printout);
    }

    @Override
    protected final void arrow(final SnipeData v) {
        Block targetBlock = getTargetBlock().getRelative(0, getYOffset(), 0);
        this.single(v, targetBlock);
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
