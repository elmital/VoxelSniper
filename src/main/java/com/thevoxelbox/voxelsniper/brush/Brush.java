package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.VoxelCommandManager;
import com.thevoxelbox.voxelsniper.VoxelMessage;
import com.thevoxelbox.voxelsniper.brush.perform.PerformerBrush;
import com.thevoxelbox.voxelsniper.snipe.SnipeAction;
import com.thevoxelbox.voxelsniper.snipe.SnipeData;
import com.thevoxelbox.voxelsniper.util.BlockHelper;
import com.thevoxelbox.voxelsniper.util.BlockWrapper;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Abstract implementation of the {@link IBrush} interface.
 */
public abstract class Brush implements IBrush {

    protected static final String BRUSH_ARGUMENT_PREFIX = VoxelCommandManager.BRUSH_SUBCOMMAND_PREFIX;

    protected static final int CHUNK_SIZE = 16;
    /**
     * Targeted Block.
     */
    private Block targetBlock;
    /**
     * Last Block before targeted Block.
     */
    private Block lastBlock;
    /**
     * Brush name.
     */
    private String name = "Undefined";

    /**
     * @param x
     * @param y
     * @param z
     * @return {@link Block}
     */
    public final Block clampY(final int x, final int y, final int z) {
        int clampedY = y;
        if (clampedY < this.getWorld().getMinHeight()) {
            clampedY = this.getWorld().getMinHeight();
        } else if (clampedY > this.getWorld().getMaxHeight()) {
            clampedY = this.getWorld().getMaxHeight();
        }

        return this.getWorld().getBlockAt(x, clampedY, z);
    }

    private boolean preparePerform(final SnipeData v, final Block clickedBlock, final BlockFace clickedFace) {
        if (this.getTarget(v, clickedBlock, clickedFace)) {
            if (this instanceof PerformerBrush) {
                ((PerformerBrush) this).initP(v);
            }
            return true;
        }

        return false;
    }

    @Override
    public boolean perform(SnipeAction action, SnipeData data, Block targetBlock, Block lastBlock) {
        this.setTargetBlock(targetBlock);
        this.setLastBlock(lastBlock);
        switch (action) {
            case ARROW:
                this.arrow(data);
                return true;
            case GUNPOWDER:
                this.powder(data);
                return true;
            default:
                return false;
        }
    }

    /**
     * The arrow action. Executed when a player RightClicks with an Arrow
     *
     * @param v Sniper caller
     */
    protected void arrow(final SnipeData v) {
    }

    /**
     * The powder action. Executed when a player RightClicks with Gunpowder
     *
     * @param v Sniper caller
     */
    protected void powder(final SnipeData v) {
    }

    @Override
    public abstract void info(VoxelMessage vm);

    @Override
    public void parseParameters(String triggerHandle, final String[] params, final SnipeData v) {
        v.sendMessage(Component.text("This brush does not accept additional parameters.").color(NamedTextColor.RED));
    }

    // TODO: make abstract
    @Override
    public List<String> registerArguments() {
        // Return empty list if not overridden; i.e. no arguments to add.
        return new ArrayList<>();
    }

    @Override
    public HashMap<String, List<String>> registerArgumentValues() {
        // Do nothing because not all brushes have arguments that have values
        return new HashMap<>();
    }

    /**
     * Overridable getTarget method.
     *
     * @param v
     * @param clickedBlock
     * @param clickedFace
     * @return boolean
     */
    protected final boolean getTarget(final SnipeData v, final Block clickedBlock, final BlockFace clickedFace) {
        if (clickedBlock != null) {
            this.setTargetBlock(clickedBlock);
            this.setLastBlock(clickedBlock.getRelative(clickedFace));
            if (this.getLastBlock() == null) {
                v.sendMessage(Component.text("Snipe target block must be visible.").color(NamedTextColor.RED));
                return false;
            }
            if (v.owner().getSnipeData(v.owner().getCurrentToolId()).isLightningEnabled()) {
                this.getWorld().strikeLightning(this.getTargetBlock().getLocation());
            }
            return true;
        } else {
            BlockHelper rangeBlockHelper;
            if (v.owner().getSnipeData(v.owner().getCurrentToolId()).isRanged()) {
                rangeBlockHelper = new BlockHelper(v.owner().getPlayer(), v.owner().getPlayer().getWorld(), v.owner().getSnipeData(v.owner().getCurrentToolId()).getRange());
                this.setTargetBlock(rangeBlockHelper.getRangeBlock());
            } else {
                rangeBlockHelper = new BlockHelper(v.owner().getPlayer(), v.owner().getPlayer().getWorld());
                this.setTargetBlock(rangeBlockHelper.getTargetBlock());
            }
            if (this.getTargetBlock() != null) {
                this.setLastBlock(rangeBlockHelper.getLastBlock());
                if (this.getLastBlock() == null) {
                    v.sendMessage(Component.text("Snipe target block must be visible.").color(NamedTextColor.RED));
                    return false;
                }
                if (v.owner().getSnipeData(v.owner().getCurrentToolId()).isLightningEnabled()) {
                    this.getWorld().strikeLightning(this.getTargetBlock().getLocation());
                }
                return true;
            } else {
                v.sendMessage(Component.text("Snipe target block must be visible.").color(NamedTextColor.RED));
                return false;
            }
        }
    }

    @Override
    public final String getName() {
        return this.name;
    }

    @Override
    public final void setName(final String name) {
        this.name = name;
    }

    @Override
    public String getBrushCategory() {
        return "General";
    }

    /**
     * @return the targetBlock
     */
    protected final Block getTargetBlock() {
        return this.targetBlock;
    }

    /**
     * @param targetBlock the targetBlock to set
     */
    protected final void setTargetBlock(final Block targetBlock) {
        this.targetBlock = targetBlock;
    }

    /**
     * @return the world
     */
    protected final World getWorld() {
        return targetBlock.getWorld();
    }

    /**
     * Looks up Type ID of Block at given coordinates in the world of the targeted Block.
     *
     * @param x X coordinate
     * @param y Y coordinate
     * @param z Z coordinate
     * @return Type ID of Block at given coordinates in the world of the targeted Block.
     */
    protected Material getBlockMaterialAt(int x, int y, int z) {
        return getWorld().getBlockAt(x, y, z).getBlockData().getMaterial();
    }

    /**
     * Looks up Block Data Value of Block at given coordinates in the world of the targeted Block.
     *
     * @param x X coordinate
     * @param y Y coordinate
     * @param z Z coordinate
     * @return Block Data Value of Block at given coordinates in the world of the targeted Block.
     */
    protected BlockData getBlockDataAt(int x, int y, int z) {
        return this.getWorld().getBlockAt(x, y, z).getBlockData();
    }

    /**
     * @return Block before target Block.
     */
    protected final Block getLastBlock() {
        return this.lastBlock;
    }

    /**
     * @param lastBlock Last Block before target Block.
     */
    protected final void setLastBlock(Block lastBlock) {
        this.lastBlock = lastBlock;
    }

    /**
     * Set block data with supplied data over BlockWrapper.
     *
     * @param blockWrapper Block data wrapper
     */
    @Deprecated
    protected final void setBlock(BlockWrapper blockWrapper) {
        this.getWorld().getBlockAt(blockWrapper.getX(), blockWrapper.getY(), blockWrapper.getZ()).setBlockData(blockWrapper.getBlockData());
    }

    /**
     * Sets the Material of the block at the passed coordinate. This function will automatically create use the default BlockData for that Material.
     *
     * @param z Z coordinate
     * @param x X coordinate
     * @param y Y coordinate
     * @param material the material to set this block to
     */
    protected final void setBlockMaterialAt(int z, int x, int y, Material material) {
        this.getWorld().getBlockAt(x, y, z).setBlockData(material.createBlockData());
    }

    /**
     * Sets the BlockData value of the block at the passed coordinate. Will use the exact BlockData that is passed into the function and NOT the default
     * BlockData of the Material.
     *
     * @param x X coordinate
     * @param y Y coordinate
     * @param z Z coordinate
     * @param blockData The blockData to set this block to
     */
    protected final void setBlockMaterialAndDataAt(int x, int y, int z, BlockData blockData) {
        this.getWorld().getBlockAt(x, y, z).setBlockData(blockData, true);
    }
}
