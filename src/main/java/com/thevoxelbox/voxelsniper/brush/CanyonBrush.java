package com.thevoxelbox.voxelsniper.brush;

import com.google.common.collect.Lists;
import com.thevoxelbox.voxelsniper.VoxelMessage;
import com.thevoxelbox.voxelsniper.snipe.SnipeData;
import com.thevoxelbox.voxelsniper.snipe.Undo;
import com.thevoxelbox.voxelsniper.util.BlockHelper;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * http://www.voxelwiki.com/minecraft/Voxelsniper#The_CANYONATOR
 *
 * @author Voxel
 */
public class CanyonBrush extends Brush {

    private final int SHIFT_LEVEL_MIN = BlockHelper.MINIMUM_WORLD_HEIGHT + 10;
    private final int SHIFT_LEVEL_MAX = 60;
    private int yLevel = SHIFT_LEVEL_MIN;

    /**
     *
     */
    public CanyonBrush() {
        this.setName("Canyon");
    }

    /**
     * @param chunk
     * @param undo
     */
    @SuppressWarnings("deprecation")
    protected final void canyon(final Chunk chunk, final Undo undo) {
        for (int x = 0; x < CHUNK_SIZE; x++) {
            for (int z = 0; z < CHUNK_SIZE; z++) {
                int currentYLevel = this.yLevel;

                for (int y = 63; y < this.getWorld().getMaxHeight(); y++) {
                    final Block block = chunk.getBlock(x, y, z);
                    final Block currentYLevelBlock = chunk.getBlock(x, currentYLevel, z);

                    undo.put(block);
                    undo.put(currentYLevelBlock);

                    currentYLevelBlock.setType(block.getType(), false);
                    block.setType(Material.AIR);

                    currentYLevel++;
                }

                final Block block = chunk.getBlock(x, getWorld().getMinHeight(), z);
                undo.put(block);
                block.setType(Material.BEDROCK);

                for (int y = getWorld().getMinHeight() + 1; y < SHIFT_LEVEL_MIN; y++) {
                    final Block currentBlock = chunk.getBlock(x, y, z);
                    undo.put(currentBlock);
                    currentBlock.setType(Material.STONE);
                }
            }
        }
    }

    @Override
    protected void arrow(final SnipeData v) {
        final Undo undo = new Undo();

        canyon(getTargetBlock().getChunk(), undo);

        v.owner().storeUndo(undo);
    }

    @Override
    protected void powder(final SnipeData v) {
        final Undo undo = new Undo();

        Chunk targetChunk = getTargetBlock().getChunk();
        for (int x = targetChunk.getX() - 1; x <= targetChunk.getX() + 1; x++) {
            for (int z = targetChunk.getX() - 1; z <= targetChunk.getX() + 1; z++) {
                canyon(getWorld().getChunkAt(x, z), undo);
            }
        }

        v.owner().storeUndo(undo);
    }

    @Override
    public void info(final VoxelMessage vm) {
        vm.brushName(this.getName());
        vm.custom(Component.text("Shift Level set to " + this.yLevel).color(NamedTextColor.GREEN));
    }

    @Override
    public final void parseParameters(String triggerHandle, final String[] params, final SnipeData v) {
        if (params[0].equalsIgnoreCase("info")) {
            v.getVoxelMessage().commandParameters("Blob Parameters:", null, "/b " + triggerHandle + " y [number] -- Set the y-coordinate where the land will be shifted to");
            return;
        }

        if (params[0].startsWith("y")) {
            try {
                int yLevel = Integer.parseInt(params[1]);

                if (yLevel < SHIFT_LEVEL_MIN) {
                    yLevel = SHIFT_LEVEL_MIN;
                } else if (yLevel > SHIFT_LEVEL_MAX) {
                    yLevel = SHIFT_LEVEL_MAX;
                }

                setYLevel(yLevel);

                v.sendMessage(Component.text("Land will be shifted to y-coordinate of " + getYLevel()).color(NamedTextColor.GREEN));
            } catch (NumberFormatException e) {
                v.sendMessage(Component.text("Invalid input, please enter a valid number!").color(NamedTextColor.RED));
            }
            return;
        }

        v.sendMessage(
                Component.empty()
                        .append(Component.text("Invalid parameter! Use ").color(NamedTextColor.RED))
                        .append(Component.text("'/b " + triggerHandle + " info'").color(NamedTextColor.LIGHT_PURPLE))
                        .append(Component.text(" to display valid parameters.").color(NamedTextColor.RED))
        );
    }

    @Override
    public List<String> registerArguments() {
        List<String> arguments = new ArrayList<>();
        
        arguments.addAll(Lists.newArrayList("y"));

        return arguments;
    }

    @Override
    public HashMap<String, List<String>> registerArgumentValues() {
        HashMap<String, List<String>> argumentValues = new HashMap<>();
        
        argumentValues.put("y", Lists.newArrayList("[number]"));

        return argumentValues;
    }

    protected final int getYLevel() {
        return yLevel;
    }

    protected final void setYLevel(int yLevel) {
        this.yLevel = yLevel;
    }

    @Override
    public String getPermissionNode() {
        return "voxelsniper.brush.canyon";
    }
}
