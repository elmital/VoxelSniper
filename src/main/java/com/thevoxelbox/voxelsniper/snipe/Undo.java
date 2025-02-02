package com.thevoxelbox.voxelsniper.snipe;

import com.google.common.collect.Sets;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.BrewingStand;
import org.bukkit.block.Chest;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.block.Dispenser;
import org.bukkit.block.Furnace;
import org.bukkit.block.Sign;
import org.bukkit.block.data.type.NoteBlock;
import org.bukkit.util.Vector;

import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Holds {@link BlockState}s that can be later on used to reset those block locations back to the recorded states.
 */
public class Undo {

    private static final Set<Material> FALLING_MATERIALS = EnumSet.of(
            Material.WATER,
            Material.LAVA);
    private static final Set<Material> FALLOFF_MATERIALS = EnumSet.of(
            Material.ACACIA_SAPLING,
            Material.SPRUCE_SAPLING,
            Material.BIRCH_SAPLING,
            Material.DARK_OAK_SAPLING,
            Material.JUNGLE_SAPLING,
            Material.OAK_SAPLING,
            Material.MANGROVE_PROPAGULE,
            Material.BAMBOO,
            Material.WHITE_BED,
            Material.ORANGE_BED,
            Material.MAGENTA_BED,
            Material.LIGHT_BLUE_BED,
            Material.YELLOW_BED,
            Material.LIME_BED,
            Material.PINK_BED,
            Material.GRAY_BED,
            Material.LIGHT_GRAY_BED,
            Material.CYAN_BED,
            Material.PURPLE_BED,
            Material.BLUE_BED,
            Material.BROWN_BED,
            Material.GREEN_BED,
            Material.RED_BED,
            Material.BLACK_BED,
            Material.POWERED_RAIL,
            Material.DETECTOR_RAIL,
            Material.TALL_GRASS,
            Material.DEAD_BUSH,
            // TODO Material.PISTON_EXTENSION,
            Material.DANDELION,
            Material.POPPY,
            Material.BLUE_ORCHID,
            Material.ALLIUM,
            Material.AZURE_BLUET,
            Material.RED_TULIP,
            Material.ORANGE_TULIP,
            Material.WHITE_TULIP,
            Material.PINK_TULIP,
            Material.OXEYE_DAISY,
            Material.AZALEA,
            Material.FLOWERING_AZALEA,
            Material.TORCHFLOWER,
            Material.TORCHFLOWER_CROP,
            Material.PITCHER_PLANT,
            Material.PITCHER_POD,
            Material.BROWN_MUSHROOM,
            Material.RED_MUSHROOM,
            Material.CRIMSON_FUNGUS,
            Material.WARPED_FUNGUS,
            Material.TORCH,
            Material.SOUL_TORCH,
            Material.FIRE,
            Material.WHEAT,
            Material.ACACIA_SIGN,
            Material.SPRUCE_SIGN,
            Material.BIRCH_SIGN,
            Material.DARK_OAK_SIGN,
            Material.JUNGLE_SIGN,
            Material.OAK_SIGN,
            Material.CRIMSON_SIGN,
            Material.WARPED_SIGN,
            Material.MANGROVE_SIGN,
            Material.BAMBOO_SIGN,
            Material.CHERRY_SIGN,
            Material.ACACIA_WALL_SIGN,
            Material.SPRUCE_WALL_SIGN,
            Material.BIRCH_WALL_SIGN,
            Material.DARK_OAK_WALL_SIGN,
            Material.JUNGLE_WALL_SIGN,
            Material.OAK_WALL_SIGN,
            Material.CRIMSON_WALL_SIGN,
            Material.WARPED_WALL_SIGN,
            Material.MANGROVE_WALL_SIGN,
            Material.BAMBOO_WALL_SIGN,
            Material.CHERRY_WALL_SIGN,
            Material.ACACIA_HANGING_SIGN,
            Material.SPRUCE_HANGING_SIGN,
            Material.BIRCH_HANGING_SIGN,
            Material.DARK_OAK_HANGING_SIGN,
            Material.JUNGLE_HANGING_SIGN,
            Material.OAK_HANGING_SIGN,
            Material.CRIMSON_HANGING_SIGN,
            Material.WARPED_HANGING_SIGN,
            Material.MANGROVE_HANGING_SIGN,
            Material.BAMBOO_HANGING_SIGN,
            Material.ACACIA_WALL_HANGING_SIGN,
            Material.SPRUCE_WALL_HANGING_SIGN,
            Material.BIRCH_WALL_HANGING_SIGN,
            Material.DARK_OAK_WALL_HANGING_SIGN,
            Material.JUNGLE_WALL_HANGING_SIGN,
            Material.OAK_WALL_HANGING_SIGN,
            Material.WARPED_WALL_HANGING_SIGN,
            Material.CRIMSON_WALL_HANGING_SIGN,
            Material.MANGROVE_WALL_HANGING_SIGN,
            Material.BAMBOO_WALL_HANGING_SIGN,
            Material.CHERRY_WALL_HANGING_SIGN,
            Material.ACACIA_DOOR,
            Material.SPRUCE_DOOR,
            Material.BIRCH_DOOR,
            Material.DARK_OAK_DOOR,
            Material.JUNGLE_DOOR,
            Material.OAK_DOOR,
            Material.CRIMSON_DOOR,
            Material.WARPED_DOOR,
            Material.MANGROVE_DOOR,
            Material.BAMBOO_DOOR,
            Material.CHERRY_DOOR,
            Material.LADDER,
            Material.RAIL,
            Material.LEVER,
            Material.STONE_PRESSURE_PLATE,
            Material.IRON_DOOR,
            Material.ACACIA_PRESSURE_PLATE,
            Material.SPRUCE_PRESSURE_PLATE,
            Material.BIRCH_PRESSURE_PLATE,
            Material.DARK_OAK_PRESSURE_PLATE,
            Material.JUNGLE_PRESSURE_PLATE,
            Material.OAK_PRESSURE_PLATE,
            Material.CRIMSON_PRESSURE_PLATE,
            Material.WARPED_PRESSURE_PLATE,
            Material.MANGROVE_PRESSURE_PLATE,
            Material.BAMBOO_PRESSURE_PLATE,
            Material.REDSTONE_TORCH,
            Material.REDSTONE_WALL_TORCH,
            Material.REDSTONE_WIRE,
            Material.STONE_BUTTON,
            Material.SNOW,
            Material.CACTUS,
            Material.SUGAR_CANE,
            Material.CAKE,
            Material.REPEATER,
            Material.COMPARATOR,
            Material.CALIBRATED_SCULK_SENSOR,
            Material.SCULK_SENSOR,
            Material.ACACIA_TRAPDOOR,
            Material.SPRUCE_TRAPDOOR,
            Material.BIRCH_TRAPDOOR,
            Material.DARK_OAK_TRAPDOOR,
            Material.JUNGLE_TRAPDOOR,
            Material.OAK_TRAPDOOR,
            Material.IRON_TRAPDOOR,
            Material.CRIMSON_TRAPDOOR,
            Material.WARPED_TRAPDOOR,
            Material.MANGROVE_TRAPDOOR,
            Material.PUMPKIN_STEM,
            Material.MELON_STEM,
            Material.VINE,
            Material.CAVE_VINES,
            Material.CAVE_VINES_PLANT,
            Material.TWISTING_VINES,
            Material.TWISTING_VINES_PLANT,
            Material.WEEPING_VINES,
            Material.WEEPING_VINES_PLANT,
            Material.LILY_PAD,
            Material.BIG_DRIPLEAF,
            Material.BIG_DRIPLEAF_STEM,
            Material.SMALL_DRIPLEAF,
            Material.WARPED_ROOTS,
            Material.CRIMSON_ROOTS,
            Material.NETHER_SPROUTS,
            Material.NETHER_WART);
    private final Set<Vector> containing = Sets.newHashSet();
    private final List<BlockState> all;
    private final List<BlockState> falloff;
    private final List<BlockState> dropdown;

    /**
     * Default constructor of a Undo container.
     */
    public Undo() {
        all = new LinkedList<>();
        falloff = new LinkedList<>();
        dropdown = new LinkedList<>();
    }

    /**
     * Get the number of blocks in the collection.
     *
     * @return size of the Undo collection
     */
    public int getSize() {
        return containing.size();
    }

    /**
     * Adds a Block to the collection.
     *
     * @param block Block to be added
     */
    public void put(Block block) {
        Vector pos = block.getLocation().toVector();
        if (this.containing.contains(pos)) {
            return;
        }
        this.containing.add(pos);
        if (Undo.FALLING_MATERIALS.contains(block.getType())) {
            dropdown.add(block.getState());
        } else if (Undo.FALLOFF_MATERIALS.contains(block.getType())) {
            falloff.add(block.getState());
        } else {
            all.add(block.getState());
        }
    }

    /**
     * Set the blockstates of all recorded blocks back to the state when they were inserted.
     */
    public void undo() {

        for (BlockState blockState : all) {
            blockState.update(true, false);
            updateSpecialBlocks(blockState);
        }

        for (BlockState blockState : falloff) {
            blockState.update(true, false);
            updateSpecialBlocks(blockState);
        }

        for (BlockState blockState : dropdown) {
            blockState.update(true, false);
            updateSpecialBlocks(blockState);
        }
    }

    /**
     * @param blockState
     */
    private void updateSpecialBlocks(BlockState blockState) {
        BlockState currentState = blockState.getBlock().getState();
        if (blockState instanceof BrewingStand && currentState instanceof BrewingStand) {
            ((BrewingStand) currentState).getInventory().setContents(((BrewingStand) blockState).getInventory().getContents());
        } else if (blockState instanceof Chest && currentState instanceof Chest) {
            ((Chest) currentState).getInventory().setContents(((Chest) blockState).getInventory().getContents());
            ((Chest) currentState).getBlockInventory().setContents(((Chest) blockState).getBlockInventory().getContents());
            currentState.update();
        } else if (blockState instanceof CreatureSpawner && currentState instanceof CreatureSpawner) {
            ((CreatureSpawner) currentState).setSpawnedType(((CreatureSpawner) currentState).getSpawnedType());
            currentState.update();
        } else if (blockState instanceof Dispenser && currentState instanceof Dispenser) {
            ((Dispenser) currentState).getInventory().setContents(((Dispenser) blockState).getInventory().getContents());
            currentState.update();
        } else if (blockState instanceof Furnace && currentState instanceof Furnace) {
            ((Furnace) currentState).getInventory().setContents(((Furnace) blockState).getInventory().getContents());
            ((Furnace) currentState).setBurnTime(((Furnace) blockState).getBurnTime());
            ((Furnace) currentState).setCookTime(((Furnace) blockState).getCookTime());
            currentState.update();
        } else if (blockState instanceof NoteBlock && currentState instanceof NoteBlock) {
            ((NoteBlock) currentState).setNote(((NoteBlock) blockState).getNote());
            currentState.update();
        } else if (blockState instanceof Sign && currentState instanceof Sign) {
            int i = 0;
            for (Component text : ((Sign) blockState).lines()) {
                ((Sign) currentState).line(i++, text);
            }
            currentState.update();
        }
    }
}
