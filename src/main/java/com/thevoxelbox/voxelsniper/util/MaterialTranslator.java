package com.thevoxelbox.voxelsniper.util;

import java.util.HashMap;
import java.util.function.Function;

import org.bukkit.Material;
import org.bukkit.block.data.AnaloguePowerable;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Levelled;
import org.bukkit.block.data.Lightable;
import org.bukkit.block.data.type.*;

/**
 * Resolves legacy numerical item IDs to Materials.
 * 
 * This class SHOULD NOT be updated. It is meant for backwards compatibility and familiarity.
 * This will allow usage of the VoxelMaterial command such: /v 0 will set the Voxel to AIR.
 *
 * 
 * @author ervinnnc
 */

// TODO: Resolve data values for certain blocks 
// e.g. 59:7 will resolve to Wheat and data value of age=7.
public class MaterialTranslator {
    public static final HashMap<String, BlockData> legacyMaterialIds = new HashMap<>();
    
    public static BlockData resolveMaterial(String id) {
        if (legacyMaterialIds.containsKey(id)) {
            return legacyMaterialIds.get(id);
        }
        
        return null;
    } 
    
    static { 
        legacyMaterialIds.put("0", Material.AIR.createBlockData());
        legacyMaterialIds.put("1", Material.STONE.createBlockData());
        legacyMaterialIds.put("1:1", Material.GRANITE.createBlockData());
        legacyMaterialIds.put("1:2", Material.POLISHED_GRANITE.createBlockData());
        legacyMaterialIds.put("1:3", Material.DIORITE.createBlockData());
        legacyMaterialIds.put("1:4", Material.POLISHED_DIORITE.createBlockData());
        legacyMaterialIds.put("1:5", Material.ANDESITE.createBlockData());
        legacyMaterialIds.put("1:6", Material.POLISHED_ANDESITE.createBlockData());
        legacyMaterialIds.put("2", Material.GRASS_BLOCK.createBlockData());
        legacyMaterialIds.put("3", Material.DIRT.createBlockData());
        legacyMaterialIds.put("3:1", Material.COARSE_DIRT.createBlockData());
        legacyMaterialIds.put("3:2", Material.PODZOL.createBlockData());
        legacyMaterialIds.put("4", Material.COBBLESTONE.createBlockData());
        legacyMaterialIds.put("5", Material.OAK_PLANKS.createBlockData());
        legacyMaterialIds.put("5:1", Material.SPRUCE_PLANKS.createBlockData());
        legacyMaterialIds.put("5:2", Material.BIRCH_PLANKS.createBlockData());
        legacyMaterialIds.put("5:3", Material.JUNGLE_PLANKS.createBlockData());
        legacyMaterialIds.put("5:4", Material.ACACIA_PLANKS.createBlockData());
        legacyMaterialIds.put("5:5", Material.DARK_OAK_PLANKS.createBlockData());
        legacyMaterialIds.put("6", Material.OAK_SAPLING.createBlockData());
        legacyMaterialIds.put("6:1", Material.SPRUCE_SAPLING.createBlockData());
        legacyMaterialIds.put("6:2", Material.BIRCH_SAPLING.createBlockData());
        legacyMaterialIds.put("6:3", Material.JUNGLE_SAPLING.createBlockData());
        legacyMaterialIds.put("6:4", Material.ACACIA_SAPLING.createBlockData());
        legacyMaterialIds.put("6:5", Material.DARK_OAK_SAPLING.createBlockData());
        legacyMaterialIds.put("7", Material.BEDROCK.createBlockData());
        legacyMaterialIds.put("8", levelableFunction(Material.WATER, 15));
        legacyMaterialIds.put("9", Material.WATER.createBlockData());
        legacyMaterialIds.put("10", levelableFunction(Material.LAVA, 15));
        legacyMaterialIds.put("11", Material.LAVA.createBlockData());
        legacyMaterialIds.put("12", Material.SAND.createBlockData());
        legacyMaterialIds.put("12:1", Material.RED_SAND.createBlockData());
        legacyMaterialIds.put("13", Material.GRAVEL.createBlockData());
        legacyMaterialIds.put("14", Material.GOLD_ORE.createBlockData());
        legacyMaterialIds.put("15", Material.IRON_ORE.createBlockData());
        legacyMaterialIds.put("16", Material.COAL_ORE.createBlockData());
        legacyMaterialIds.put("17", Material.OAK_LOG.createBlockData());
        legacyMaterialIds.put("17:1", Material.SPRUCE_LOG.createBlockData());
        legacyMaterialIds.put("17:2", Material.BIRCH_LOG.createBlockData());
        legacyMaterialIds.put("17:3", Material.JUNGLE_LOG.createBlockData());
        legacyMaterialIds.put("18", Material.OAK_LEAVES.createBlockData());
        legacyMaterialIds.put("18:1", Material.SPRUCE_LEAVES.createBlockData());
        legacyMaterialIds.put("18:2", Material.BIRCH_LEAVES.createBlockData());
        legacyMaterialIds.put("18:3", Material.JUNGLE_LEAVES.createBlockData());
        legacyMaterialIds.put("19", Material.SPONGE.createBlockData());
        legacyMaterialIds.put("19:1", Material.WET_SPONGE.createBlockData());
        legacyMaterialIds.put("20", Material.GLASS.createBlockData());
        legacyMaterialIds.put("21", Material.LAPIS_ORE.createBlockData());
        legacyMaterialIds.put("22", Material.LAPIS_BLOCK.createBlockData());
        legacyMaterialIds.put("23", Material.DISPENSER.createBlockData());
        legacyMaterialIds.put("24", Material.SANDSTONE.createBlockData());
        legacyMaterialIds.put("24:1", Material.CHISELED_SANDSTONE.createBlockData());
        legacyMaterialIds.put("24:2", Material.SMOOTH_SANDSTONE.createBlockData());
        legacyMaterialIds.put("25", Material.NOTE_BLOCK.createBlockData());
        legacyMaterialIds.put("26", Material.RED_BED.createBlockData());
        legacyMaterialIds.put("27", Material.POWERED_RAIL.createBlockData());
        legacyMaterialIds.put("28", Material.DETECTOR_RAIL.createBlockData());
        legacyMaterialIds.put("29", Material.STICKY_PISTON.createBlockData());
        legacyMaterialIds.put("30", Material.COBWEB.createBlockData());
        legacyMaterialIds.put("31", Material.DEAD_BUSH.createBlockData());
        legacyMaterialIds.put("31:1", Material.GRASS.createBlockData());
        legacyMaterialIds.put("31:2", Material.FERN.createBlockData());
        legacyMaterialIds.put("32", Material.DEAD_BUSH.createBlockData());
        legacyMaterialIds.put("33", Material.PISTON.createBlockData());
        legacyMaterialIds.put("34", Material.PISTON_HEAD.createBlockData());
        legacyMaterialIds.put("35", Material.WHITE_WOOL.createBlockData());
        legacyMaterialIds.put("35:1", Material.ORANGE_WOOL.createBlockData());
        legacyMaterialIds.put("35:2", Material.MAGENTA_WOOL.createBlockData());
        legacyMaterialIds.put("35:3", Material.LIGHT_BLUE_WOOL.createBlockData());
        legacyMaterialIds.put("35:4", Material.YELLOW_WOOL.createBlockData());
        legacyMaterialIds.put("35:5", Material.LIME_WOOL.createBlockData());
        legacyMaterialIds.put("35:6", Material.PINK_WOOL.createBlockData());
        legacyMaterialIds.put("35:7", Material.GRAY_WOOL.createBlockData());
        legacyMaterialIds.put("35:8", Material.LIGHT_GRAY_WOOL.createBlockData());
        legacyMaterialIds.put("35:9", Material.CYAN_WOOL.createBlockData());
        legacyMaterialIds.put("35:10", Material.PURPLE_WOOL.createBlockData());
        legacyMaterialIds.put("35:11", Material.BLUE_WOOL.createBlockData());
        legacyMaterialIds.put("35:12", Material.BROWN_WOOL.createBlockData());
        legacyMaterialIds.put("35:13", Material.GREEN_WOOL.createBlockData());
        legacyMaterialIds.put("35:14", Material.RED_WOOL.createBlockData());
        legacyMaterialIds.put("35:15", Material.BLACK_WOOL.createBlockData());
        legacyMaterialIds.put("36", Material.AIR.createBlockData()); // nothing
        legacyMaterialIds.put("37", Material.DANDELION.createBlockData());
        legacyMaterialIds.put("38", Material.POPPY.createBlockData());
        legacyMaterialIds.put("38:1", Material.BLUE_ORCHID.createBlockData());
        legacyMaterialIds.put("38:2", Material.ALLIUM.createBlockData());
        legacyMaterialIds.put("38:3", Material.AZURE_BLUET.createBlockData());
        legacyMaterialIds.put("38:4", Material.RED_TULIP.createBlockData());
        legacyMaterialIds.put("38:5", Material.ORANGE_TULIP.createBlockData());
        legacyMaterialIds.put("38:6", Material.WHITE_TULIP.createBlockData());
        legacyMaterialIds.put("38:7", Material.PINK_TULIP.createBlockData());
        legacyMaterialIds.put("38:8", Material.OXEYE_DAISY.createBlockData());
        legacyMaterialIds.put("39", Material.BROWN_MUSHROOM.createBlockData());
        legacyMaterialIds.put("40", Material.RED_MUSHROOM.createBlockData());
        legacyMaterialIds.put("41", Material.GOLD_BLOCK.createBlockData());
        legacyMaterialIds.put("42", Material.IRON_BLOCK.createBlockData());
        legacyMaterialIds.put("43", slabFunction(Material.STONE_SLAB, Slab.Type.DOUBLE));
        legacyMaterialIds.put("43:1", slabFunction(Material.SANDSTONE_SLAB, Slab.Type.DOUBLE));
        legacyMaterialIds.put("43:2", slabFunction(Material.OAK_SLAB, Slab.Type.DOUBLE));
        legacyMaterialIds.put("43:3", slabFunction(Material.COBBLESTONE_SLAB, Slab.Type.DOUBLE));
        legacyMaterialIds.put("43:4", slabFunction(Material.BRICK_SLAB, Slab.Type.DOUBLE));
        legacyMaterialIds.put("43:5", slabFunction(Material.STONE_BRICK_SLAB, Slab.Type.DOUBLE));
        legacyMaterialIds.put("43:6", slabFunction(Material.NETHER_BRICK_SLAB, Slab.Type.DOUBLE));
        legacyMaterialIds.put("43:7", slabFunction(Material.QUARTZ_SLAB, Slab.Type.DOUBLE));
        legacyMaterialIds.put("44", Material.STONE_SLAB.createBlockData());
        legacyMaterialIds.put("44:1", Material.SANDSTONE_SLAB.createBlockData());
        legacyMaterialIds.put("44:2", Material.OAK_SLAB.createBlockData());
        legacyMaterialIds.put("44:3", Material.COBBLESTONE_SLAB.createBlockData());
        legacyMaterialIds.put("44:4", Material.BRICK_SLAB.createBlockData());
        legacyMaterialIds.put("44:5", Material.STONE_BRICK_SLAB.createBlockData());
        legacyMaterialIds.put("44:6", Material.NETHER_BRICK_SLAB.createBlockData());
        legacyMaterialIds.put("44:7", Material.QUARTZ_SLAB.createBlockData());
        legacyMaterialIds.put("45", Material.BRICKS.createBlockData());
        legacyMaterialIds.put("46", Material.TNT.createBlockData());
        legacyMaterialIds.put("47", Material.BOOKSHELF.createBlockData());
        legacyMaterialIds.put("48", Material.MOSSY_COBBLESTONE.createBlockData());
        legacyMaterialIds.put("49", Material.OBSIDIAN.createBlockData());
        legacyMaterialIds.put("50", Material.TORCH.createBlockData());
        legacyMaterialIds.put("51", Material.FIRE.createBlockData());
        legacyMaterialIds.put("52", Material.SPAWNER.createBlockData());
        legacyMaterialIds.put("53", Material.OAK_STAIRS.createBlockData());
        legacyMaterialIds.put("54", Material.CHEST.createBlockData());
        legacyMaterialIds.put("55", Material.REDSTONE_WIRE.createBlockData());
        legacyMaterialIds.put("56", Material.DIAMOND_ORE.createBlockData());
        legacyMaterialIds.put("57", Material.DIAMOND_BLOCK.createBlockData());
        legacyMaterialIds.put("58", Material.CRAFTING_TABLE.createBlockData());
        legacyMaterialIds.put("59", Material.WHEAT.createBlockData());
        legacyMaterialIds.put("60", Material.FARMLAND.createBlockData());
        legacyMaterialIds.put("61", Material.FURNACE.createBlockData());
        legacyMaterialIds.put("62", new Function<Material, BlockData>() {
            /**
             * Applies this function to the given argument.
             *
             * @param material the function argument
             * @return the function result
             */
            @Override
            public BlockData apply(Material material) {
                var d = (Furnace) material.createBlockData();
                d.setLit(true);
                return d;
            }
        }.apply(Material.FURNACE));
        legacyMaterialIds.put("63", Material.OAK_SIGN.createBlockData());
        legacyMaterialIds.put("64", Material.OAK_DOOR.createBlockData());
        legacyMaterialIds.put("65", Material.LADDER.createBlockData());
        legacyMaterialIds.put("66", Material.RAIL.createBlockData());
        legacyMaterialIds.put("67", Material.COBBLESTONE_STAIRS.createBlockData());
        legacyMaterialIds.put("68", Material.OAK_WALL_SIGN.createBlockData());
        legacyMaterialIds.put("69", Material.LEVER.createBlockData());
        legacyMaterialIds.put("70", Material.STONE_PRESSURE_PLATE.createBlockData());
        legacyMaterialIds.put("71", Material.IRON_DOOR.createBlockData());
        legacyMaterialIds.put("72", Material.OAK_PRESSURE_PLATE.createBlockData());
        legacyMaterialIds.put("73", Material.REDSTONE_ORE.createBlockData());
        legacyMaterialIds.put("74", lightableFunction(Material.REDSTONE_ORE, true));
        legacyMaterialIds.put("75", Material.REDSTONE_TORCH.createBlockData());
        legacyMaterialIds.put("76", lightableFunction(Material.REDSTONE_TORCH, true));
        legacyMaterialIds.put("77", Material.STONE_BUTTON.createBlockData());
        legacyMaterialIds.put("78", Material.SNOW.createBlockData());
        legacyMaterialIds.put("79", Material.ICE.createBlockData());
        legacyMaterialIds.put("80", Material.SNOW_BLOCK.createBlockData());
        legacyMaterialIds.put("81", Material.CACTUS.createBlockData());
        legacyMaterialIds.put("82", Material.CLAY.createBlockData());
        legacyMaterialIds.put("83", Material.SUGAR_CANE.createBlockData());
        legacyMaterialIds.put("84", Material.JUKEBOX.createBlockData());
        legacyMaterialIds.put("85", Material.OAK_FENCE.createBlockData());
        legacyMaterialIds.put("86", Material.PUMPKIN.createBlockData());
        legacyMaterialIds.put("87", Material.NETHERRACK.createBlockData());
        legacyMaterialIds.put("88", Material.SOUL_SAND.createBlockData());
        legacyMaterialIds.put("89", Material.GLOWSTONE.createBlockData());
        legacyMaterialIds.put("90", Material.NETHER_PORTAL.createBlockData());
        legacyMaterialIds.put("91", Material.JACK_O_LANTERN.createBlockData());
        legacyMaterialIds.put("92", Material.CAKE.createBlockData());
        legacyMaterialIds.put("93", Material.REPEATER.createBlockData());
        legacyMaterialIds.put("94", new Function<Material, BlockData>() {
            /**
             * Applies this function to the given argument.
             *
             * @param material the function argument
             * @return the function result
             */
            @Override
            public BlockData apply(Material material) {
                var d = (Repeater) material.createBlockData();
                d.setPowered(true);
                return d;
            }
        }.apply(Material.REPEATER));
        legacyMaterialIds.put("95", Material.WHITE_STAINED_GLASS.createBlockData());
        legacyMaterialIds.put("95:1", Material.ORANGE_STAINED_GLASS.createBlockData());
        legacyMaterialIds.put("95:2", Material.MAGENTA_STAINED_GLASS.createBlockData());
        legacyMaterialIds.put("95:3", Material.LIGHT_BLUE_STAINED_GLASS.createBlockData());
        legacyMaterialIds.put("95:4", Material.YELLOW_STAINED_GLASS.createBlockData());
        legacyMaterialIds.put("95:5", Material.LIME_STAINED_GLASS.createBlockData());
        legacyMaterialIds.put("95:6", Material.PINK_STAINED_GLASS.createBlockData());
        legacyMaterialIds.put("95:7", Material.GRAY_STAINED_GLASS.createBlockData());
        legacyMaterialIds.put("95:8", Material.LIGHT_GRAY_STAINED_GLASS.createBlockData());
        legacyMaterialIds.put("95:9", Material.CYAN_STAINED_GLASS.createBlockData());
        legacyMaterialIds.put("95:10", Material.PURPLE_STAINED_GLASS.createBlockData());
        legacyMaterialIds.put("95:11", Material.BLUE_STAINED_GLASS.createBlockData());
        legacyMaterialIds.put("95:12", Material.BROWN_STAINED_GLASS.createBlockData());
        legacyMaterialIds.put("95:13", Material.GREEN_STAINED_GLASS.createBlockData());
        legacyMaterialIds.put("95:14", Material.RED_STAINED_GLASS.createBlockData());
        legacyMaterialIds.put("95:15", Material.BLACK_STAINED_GLASS.createBlockData());
        legacyMaterialIds.put("96", Material.OAK_TRAPDOOR.createBlockData());
        legacyMaterialIds.put("97", Material.INFESTED_STONE.createBlockData());
        legacyMaterialIds.put("97:1", Material.INFESTED_COBBLESTONE.createBlockData());
        legacyMaterialIds.put("97:2", Material.INFESTED_STONE_BRICKS.createBlockData());
        legacyMaterialIds.put("97:3", Material.INFESTED_MOSSY_STONE_BRICKS.createBlockData());
        legacyMaterialIds.put("97:4", Material.INFESTED_CRACKED_STONE_BRICKS.createBlockData());
        legacyMaterialIds.put("97:5", Material.INFESTED_CHISELED_STONE_BRICKS.createBlockData());
        legacyMaterialIds.put("98", Material.STONE_BRICKS.createBlockData());
        legacyMaterialIds.put("98:1", Material.MOSSY_STONE_BRICKS.createBlockData());
        legacyMaterialIds.put("98:2", Material.CRACKED_STONE_BRICKS.createBlockData());
        legacyMaterialIds.put("98:3", Material.CHISELED_STONE_BRICKS.createBlockData());
        legacyMaterialIds.put("99", Material.BROWN_MUSHROOM_BLOCK.createBlockData());
        legacyMaterialIds.put("100", Material.RED_MUSHROOM_BLOCK.createBlockData());
        legacyMaterialIds.put("101", Material.IRON_BARS.createBlockData());
        legacyMaterialIds.put("102", Material.GLASS_PANE.createBlockData());
        legacyMaterialIds.put("103", Material.MELON.createBlockData());
        legacyMaterialIds.put("104", Material.PUMPKIN_STEM.createBlockData());
        legacyMaterialIds.put("105", Material.MELON_STEM.createBlockData());
        legacyMaterialIds.put("106", Material.VINE.createBlockData());
        legacyMaterialIds.put("107", Material.OAK_FENCE_GATE.createBlockData());
        legacyMaterialIds.put("108", Material.BRICK_STAIRS.createBlockData());
        legacyMaterialIds.put("109", Material.STONE_BRICK_STAIRS.createBlockData());
        legacyMaterialIds.put("110", Material.MYCELIUM.createBlockData());
        legacyMaterialIds.put("111", Material.LILY_PAD.createBlockData());
        legacyMaterialIds.put("112", Material.NETHER_BRICKS.createBlockData());
        legacyMaterialIds.put("113", Material.NETHER_BRICK_FENCE.createBlockData());
        legacyMaterialIds.put("114", Material.NETHER_BRICK_STAIRS.createBlockData());
        legacyMaterialIds.put("115", Material.NETHER_WART.createBlockData());
        legacyMaterialIds.put("116", Material.ENCHANTING_TABLE.createBlockData());
        legacyMaterialIds.put("117", Material.BREWING_STAND.createBlockData());
        legacyMaterialIds.put("118", Material.CAULDRON.createBlockData());
        legacyMaterialIds.put("119", Material.END_PORTAL.createBlockData());
        legacyMaterialIds.put("120", Material.END_PORTAL_FRAME.createBlockData());
        legacyMaterialIds.put("121", Material.END_STONE.createBlockData());
        legacyMaterialIds.put("122", Material.DRAGON_EGG.createBlockData());
        legacyMaterialIds.put("123", Material.REDSTONE_LAMP.createBlockData());
        legacyMaterialIds.put("124", lightableFunction(Material.REDSTONE_LAMP, true));
        legacyMaterialIds.put("125", slabFunction(Material.OAK_PLANKS, Slab.Type.DOUBLE));
        legacyMaterialIds.put("125:1", slabFunction(Material.SPRUCE_PLANKS, Slab.Type.DOUBLE));
        legacyMaterialIds.put("125:2", slabFunction(Material.BIRCH_PLANKS, Slab.Type.DOUBLE));
        legacyMaterialIds.put("125:3", slabFunction(Material.JUNGLE_PLANKS, Slab.Type.DOUBLE));
        legacyMaterialIds.put("125:4", slabFunction(Material.ACACIA_PLANKS, Slab.Type.DOUBLE));
        legacyMaterialIds.put("125:5", slabFunction(Material.DARK_OAK_PLANKS, Slab.Type.DOUBLE));
        legacyMaterialIds.put("126", Material.OAK_SLAB.createBlockData());
        legacyMaterialIds.put("126:1", Material.SPRUCE_SLAB.createBlockData());
        legacyMaterialIds.put("126:2", Material.BIRCH_SLAB.createBlockData());
        legacyMaterialIds.put("126:3", Material.JUNGLE_SLAB.createBlockData());
        legacyMaterialIds.put("126:4", Material.ACACIA_SLAB.createBlockData());
        legacyMaterialIds.put("126:5", Material.DARK_OAK_SLAB.createBlockData());
        legacyMaterialIds.put("127", Material.COCOA.createBlockData());
        legacyMaterialIds.put("128", Material.SANDSTONE_STAIRS.createBlockData());
        legacyMaterialIds.put("129", Material.EMERALD_ORE.createBlockData());
        legacyMaterialIds.put("130", Material.ENDER_CHEST.createBlockData());
        legacyMaterialIds.put("131", Material.TRIPWIRE_HOOK.createBlockData());
        legacyMaterialIds.put("132", Material.TRIPWIRE.createBlockData());
        legacyMaterialIds.put("133", Material.EMERALD_BLOCK.createBlockData());
        legacyMaterialIds.put("134", Material.SPRUCE_STAIRS.createBlockData());
        legacyMaterialIds.put("135", Material.BIRCH_STAIRS.createBlockData());
        legacyMaterialIds.put("136", Material.JUNGLE_STAIRS.createBlockData());
        legacyMaterialIds.put("137", Material.COMMAND_BLOCK.createBlockData());
        legacyMaterialIds.put("138", Material.BEACON.createBlockData());
        legacyMaterialIds.put("139", Material.COBBLESTONE_WALL.createBlockData());
        legacyMaterialIds.put("139:1", Material.MOSSY_COBBLESTONE_WALL.createBlockData());
        legacyMaterialIds.put("140", Material.FLOWER_POT.createBlockData());
        legacyMaterialIds.put("141", Material.CARROTS.createBlockData());
        legacyMaterialIds.put("142", Material.POTATOES.createBlockData());
        legacyMaterialIds.put("143", Material.OAK_BUTTON.createBlockData());
        legacyMaterialIds.put("144", Material.SKELETON_SKULL.createBlockData());
        legacyMaterialIds.put("145", Material.ANVIL.createBlockData());
        legacyMaterialIds.put("146", Material.TRAPPED_CHEST.createBlockData());
        legacyMaterialIds.put("147", Material.LIGHT_WEIGHTED_PRESSURE_PLATE.createBlockData());
        legacyMaterialIds.put("148", Material.HEAVY_WEIGHTED_PRESSURE_PLATE.createBlockData());
        legacyMaterialIds.put("149", Material.COMPARATOR.createBlockData());
        legacyMaterialIds.put("150", new Function<Material, BlockData>() {
            /**
             * Applies this function to the given argument.
             *
             * @param material the function argument
             * @return the function result
             */
            @Override
            public BlockData apply(Material material) {
                var d = (Comparator) material.createBlockData();
                d.setPowered(true);
                return d;
            }
        }.apply(Material.COMPARATOR));
        legacyMaterialIds.put("151", Material.DAYLIGHT_DETECTOR.createBlockData());
        legacyMaterialIds.put("152", Material.REDSTONE_BLOCK.createBlockData());
        legacyMaterialIds.put("153", Material.NETHER_QUARTZ_ORE.createBlockData());
        legacyMaterialIds.put("154", Material.HOPPER.createBlockData());
        legacyMaterialIds.put("155", Material.QUARTZ_BLOCK.createBlockData());
        legacyMaterialIds.put("155:1", Material.CHISELED_QUARTZ_BLOCK.createBlockData());
        legacyMaterialIds.put("155:2", Material.QUARTZ_PILLAR.createBlockData());
        legacyMaterialIds.put("156", Material.QUARTZ_STAIRS.createBlockData());
        legacyMaterialIds.put("157", Material.ACTIVATOR_RAIL.createBlockData());
        legacyMaterialIds.put("158", Material.DROPPER.createBlockData());
        legacyMaterialIds.put("159", Material.WHITE_TERRACOTTA.createBlockData());
        legacyMaterialIds.put("159:1", Material.ORANGE_TERRACOTTA.createBlockData());
        legacyMaterialIds.put("159:2", Material.MAGENTA_TERRACOTTA.createBlockData());
        legacyMaterialIds.put("159:3", Material.LIGHT_BLUE_TERRACOTTA.createBlockData());
        legacyMaterialIds.put("159:4", Material.YELLOW_TERRACOTTA.createBlockData());
        legacyMaterialIds.put("159:5", Material.LIME_TERRACOTTA.createBlockData());
        legacyMaterialIds.put("159:6", Material.PINK_TERRACOTTA.createBlockData());
        legacyMaterialIds.put("159:7", Material.GRAY_TERRACOTTA.createBlockData());
        legacyMaterialIds.put("159:8", Material.LIGHT_GRAY_TERRACOTTA.createBlockData());
        legacyMaterialIds.put("159:9", Material.CYAN_TERRACOTTA.createBlockData());
        legacyMaterialIds.put("159:10", Material.PURPLE_TERRACOTTA.createBlockData());
        legacyMaterialIds.put("159:11", Material.BLUE_TERRACOTTA.createBlockData());
        legacyMaterialIds.put("159:12", Material.BROWN_TERRACOTTA.createBlockData());
        legacyMaterialIds.put("159:13", Material.GREEN_TERRACOTTA.createBlockData());
        legacyMaterialIds.put("159:14", Material.RED_TERRACOTTA.createBlockData());
        legacyMaterialIds.put("159:15", Material.BLACK_TERRACOTTA.createBlockData());
        legacyMaterialIds.put("160", Material.WHITE_STAINED_GLASS_PANE.createBlockData());
        legacyMaterialIds.put("160:1", Material.ORANGE_STAINED_GLASS_PANE.createBlockData());
        legacyMaterialIds.put("160:2", Material.MAGENTA_STAINED_GLASS_PANE.createBlockData());
        legacyMaterialIds.put("160:3", Material.LIGHT_BLUE_STAINED_GLASS_PANE.createBlockData());
        legacyMaterialIds.put("160:4", Material.YELLOW_STAINED_GLASS_PANE.createBlockData());
        legacyMaterialIds.put("160:5", Material.LIME_STAINED_GLASS_PANE.createBlockData());
        legacyMaterialIds.put("160:6", Material.PINK_STAINED_GLASS_PANE.createBlockData());
        legacyMaterialIds.put("160:7", Material.GRAY_STAINED_GLASS_PANE.createBlockData());
        legacyMaterialIds.put("160:8", Material.LIGHT_GRAY_STAINED_GLASS_PANE.createBlockData());
        legacyMaterialIds.put("160:9", Material.CYAN_STAINED_GLASS_PANE.createBlockData());
        legacyMaterialIds.put("160:10", Material.PURPLE_STAINED_GLASS_PANE.createBlockData());
        legacyMaterialIds.put("160:11", Material.BLUE_STAINED_GLASS_PANE.createBlockData());
        legacyMaterialIds.put("160:12", Material.BROWN_STAINED_GLASS_PANE.createBlockData());
        legacyMaterialIds.put("160:13", Material.GREEN_STAINED_GLASS_PANE.createBlockData());
        legacyMaterialIds.put("160:14", Material.RED_STAINED_GLASS_PANE.createBlockData());
        legacyMaterialIds.put("160:15", Material.BLACK_STAINED_GLASS_PANE.createBlockData());
        legacyMaterialIds.put("161", Material.ACACIA_LEAVES.createBlockData());
        legacyMaterialIds.put("161:1", Material.DARK_OAK_LEAVES.createBlockData());
        legacyMaterialIds.put("162", Material.ACACIA_WOOD.createBlockData());
        legacyMaterialIds.put("162:1", Material.DARK_OAK_WOOD.createBlockData());
        legacyMaterialIds.put("163", Material.ACACIA_STAIRS.createBlockData());
        legacyMaterialIds.put("164", Material.DARK_OAK_STAIRS.createBlockData());
        legacyMaterialIds.put("165", Material.SLIME_BLOCK.createBlockData());
        legacyMaterialIds.put("166", Material.BARRIER.createBlockData());
        legacyMaterialIds.put("167", Material.IRON_TRAPDOOR.createBlockData());
        legacyMaterialIds.put("168", Material.PRISMARINE.createBlockData());
        legacyMaterialIds.put("168:1", Material.PRISMARINE_BRICKS.createBlockData());
        legacyMaterialIds.put("168:2", Material.DARK_PRISMARINE.createBlockData());
        legacyMaterialIds.put("169", Material.SEA_LANTERN.createBlockData());
        legacyMaterialIds.put("170", Material.HAY_BLOCK.createBlockData());
        legacyMaterialIds.put("171", Material.WHITE_CARPET.createBlockData());
        legacyMaterialIds.put("171:1", Material.ORANGE_CARPET.createBlockData());
        legacyMaterialIds.put("171:2", Material.MAGENTA_CARPET.createBlockData());
        legacyMaterialIds.put("171:3", Material.LIGHT_BLUE_CARPET.createBlockData());
        legacyMaterialIds.put("171:4", Material.YELLOW_CARPET.createBlockData());
        legacyMaterialIds.put("171:5", Material.LIME_CARPET.createBlockData());
        legacyMaterialIds.put("171:6", Material.PINK_CARPET.createBlockData());
        legacyMaterialIds.put("171:7", Material.GRAY_CARPET.createBlockData());
        legacyMaterialIds.put("171:8", Material.LIGHT_GRAY_CARPET.createBlockData());
        legacyMaterialIds.put("171:9", Material.CYAN_CARPET.createBlockData());
        legacyMaterialIds.put("171:10", Material.PURPLE_CARPET.createBlockData());
        legacyMaterialIds.put("171:11", Material.BLUE_CARPET.createBlockData());
        legacyMaterialIds.put("171:12", Material.BROWN_CARPET.createBlockData());
        legacyMaterialIds.put("171:13", Material.GREEN_CARPET.createBlockData());
        legacyMaterialIds.put("171:14", Material.RED_CARPET.createBlockData());
        legacyMaterialIds.put("171:15", Material.BLACK_CARPET.createBlockData());
        legacyMaterialIds.put("172", Material.TERRACOTTA.createBlockData());
        legacyMaterialIds.put("173", Material.COAL_BLOCK.createBlockData());
        legacyMaterialIds.put("174", Material.PACKED_ICE.createBlockData());
        legacyMaterialIds.put("175", Material.SUNFLOWER.createBlockData());
        legacyMaterialIds.put("175:1", Material.LILAC.createBlockData());
        legacyMaterialIds.put("175:2", Material.TALL_GRASS.createBlockData());
        legacyMaterialIds.put("175:3", Material.LARGE_FERN.createBlockData());
        legacyMaterialIds.put("175:4", Material.ROSE_BUSH.createBlockData());
        legacyMaterialIds.put("175:5", Material.PEONY.createBlockData());
        legacyMaterialIds.put("176", Material.WHITE_BANNER.createBlockData());
        legacyMaterialIds.put("177", Material.WHITE_WALL_BANNER.createBlockData());
        legacyMaterialIds.put("178", new Function<Material, BlockData>() {
            /**
             * Applies this function to the given argument.
             *
             * @param material the function argument
             * @return the function result
             */
            @Override
            public BlockData apply(Material material) {
                var d = (DaylightDetector) material.createBlockData();
                d.setInverted(true);
                return d;
            }
        }.apply(Material.DAYLIGHT_DETECTOR));
        legacyMaterialIds.put("179", Material.RED_SANDSTONE.createBlockData());
        legacyMaterialIds.put("179:1", Material.CHISELED_RED_SANDSTONE.createBlockData());
        legacyMaterialIds.put("179:2", Material.SMOOTH_RED_SANDSTONE.createBlockData());
        legacyMaterialIds.put("180", Material.RED_SANDSTONE_STAIRS.createBlockData());
        legacyMaterialIds.put("181", slabFunction(Material.RED_SANDSTONE_SLAB, Slab.Type.DOUBLE));
        legacyMaterialIds.put("182", Material.RED_SANDSTONE_SLAB.createBlockData());
        legacyMaterialIds.put("183", Material.SPRUCE_FENCE_GATE.createBlockData());
        legacyMaterialIds.put("184", Material.BIRCH_FENCE_GATE.createBlockData());
        legacyMaterialIds.put("185", Material.JUNGLE_FENCE_GATE.createBlockData());
        legacyMaterialIds.put("186", Material.DARK_OAK_FENCE_GATE.createBlockData());
        legacyMaterialIds.put("187", Material.ACACIA_FENCE_GATE.createBlockData());
        legacyMaterialIds.put("188", Material.SPRUCE_FENCE.createBlockData());
        legacyMaterialIds.put("189", Material.BIRCH_FENCE.createBlockData());
        legacyMaterialIds.put("190", Material.JUNGLE_FENCE.createBlockData());
        legacyMaterialIds.put("191", Material.DARK_OAK_FENCE.createBlockData());
        legacyMaterialIds.put("192", Material.ACACIA_FENCE.createBlockData());
        legacyMaterialIds.put("193", Material.SPRUCE_DOOR.createBlockData());
        legacyMaterialIds.put("194", Material.BIRCH_DOOR.createBlockData());
        legacyMaterialIds.put("195", Material.JUNGLE_DOOR.createBlockData());
        legacyMaterialIds.put("196", Material.ACACIA_DOOR.createBlockData());
        legacyMaterialIds.put("197", Material.DARK_OAK_DOOR.createBlockData());
        legacyMaterialIds.put("198", Material.END_ROD.createBlockData());
        legacyMaterialIds.put("199", Material.CHORUS_PLANT.createBlockData());
        legacyMaterialIds.put("200", Material.CHORUS_FLOWER.createBlockData());
        legacyMaterialIds.put("201", Material.PURPUR_BLOCK.createBlockData());
        legacyMaterialIds.put("202", Material.PURPUR_PILLAR.createBlockData());
        legacyMaterialIds.put("203", Material.PURPUR_STAIRS.createBlockData());
        legacyMaterialIds.put("204", slabFunction(Material.PURPUR_SLAB, Slab.Type.DOUBLE));
        legacyMaterialIds.put("205", Material.PURPUR_SLAB.createBlockData());
        legacyMaterialIds.put("206", Material.END_STONE_BRICKS.createBlockData());
        legacyMaterialIds.put("207", Material.BEETROOTS.createBlockData());
        legacyMaterialIds.put("208", Material.DIRT_PATH.createBlockData());
        legacyMaterialIds.put("209", Material.END_GATEWAY.createBlockData());
        legacyMaterialIds.put("210", Material.REPEATING_COMMAND_BLOCK.createBlockData());
        legacyMaterialIds.put("211", Material.CHAIN_COMMAND_BLOCK.createBlockData());
        legacyMaterialIds.put("212", Material.FROSTED_ICE.createBlockData());
        legacyMaterialIds.put("213", Material.MAGMA_BLOCK.createBlockData());
        legacyMaterialIds.put("214", Material.NETHER_WART_BLOCK.createBlockData());
        legacyMaterialIds.put("215", Material.RED_NETHER_BRICKS.createBlockData());
        legacyMaterialIds.put("216", Material.BONE_BLOCK.createBlockData());
        legacyMaterialIds.put("217", Material.STRUCTURE_VOID.createBlockData());
        legacyMaterialIds.put("218", Material.OBSERVER.createBlockData());
        legacyMaterialIds.put("219", Material.WHITE_SHULKER_BOX.createBlockData());
        legacyMaterialIds.put("220", Material.ORANGE_SHULKER_BOX.createBlockData());
        legacyMaterialIds.put("221", Material.MAGENTA_SHULKER_BOX.createBlockData());
        legacyMaterialIds.put("222", Material.LIGHT_BLUE_SHULKER_BOX.createBlockData());
        legacyMaterialIds.put("223", Material.YELLOW_SHULKER_BOX.createBlockData());
        legacyMaterialIds.put("224", Material.LIME_SHULKER_BOX.createBlockData());
        legacyMaterialIds.put("225", Material.PINK_SHULKER_BOX.createBlockData());
        legacyMaterialIds.put("226", Material.GRAY_SHULKER_BOX.createBlockData());
        legacyMaterialIds.put("227", Material.LIGHT_GRAY_SHULKER_BOX.createBlockData());
        legacyMaterialIds.put("228", Material.CYAN_SHULKER_BOX.createBlockData());
        legacyMaterialIds.put("229", Material.PURPLE_SHULKER_BOX.createBlockData());
        legacyMaterialIds.put("230", Material.BLUE_SHULKER_BOX.createBlockData());
        legacyMaterialIds.put("231", Material.BROWN_SHULKER_BOX.createBlockData());
        legacyMaterialIds.put("232", Material.GREEN_SHULKER_BOX.createBlockData());
        legacyMaterialIds.put("233", Material.RED_SHULKER_BOX.createBlockData());
        legacyMaterialIds.put("234", Material.BLACK_SHULKER_BOX.createBlockData());
        legacyMaterialIds.put("235", Material.WHITE_GLAZED_TERRACOTTA.createBlockData());
        legacyMaterialIds.put("236", Material.ORANGE_GLAZED_TERRACOTTA.createBlockData());
        legacyMaterialIds.put("237", Material.MAGENTA_GLAZED_TERRACOTTA.createBlockData());
        legacyMaterialIds.put("238", Material.LIGHT_BLUE_GLAZED_TERRACOTTA.createBlockData());
        legacyMaterialIds.put("239", Material.YELLOW_GLAZED_TERRACOTTA.createBlockData());
        legacyMaterialIds.put("240", Material.LIME_GLAZED_TERRACOTTA.createBlockData());
        legacyMaterialIds.put("241", Material.PINK_GLAZED_TERRACOTTA.createBlockData());
        legacyMaterialIds.put("242", Material.GRAY_GLAZED_TERRACOTTA.createBlockData());
        legacyMaterialIds.put("243", Material.LIGHT_GRAY_GLAZED_TERRACOTTA.createBlockData());
        legacyMaterialIds.put("244", Material.CYAN_GLAZED_TERRACOTTA.createBlockData());
        legacyMaterialIds.put("245", Material.PURPLE_GLAZED_TERRACOTTA.createBlockData());
        legacyMaterialIds.put("246", Material.BLUE_GLAZED_TERRACOTTA.createBlockData());
        legacyMaterialIds.put("247", Material.BROWN_GLAZED_TERRACOTTA.createBlockData());
        legacyMaterialIds.put("248", Material.GREEN_GLAZED_TERRACOTTA.createBlockData());
        legacyMaterialIds.put("249", Material.RED_GLAZED_TERRACOTTA.createBlockData());
        legacyMaterialIds.put("250", Material.BLACK_GLAZED_TERRACOTTA.createBlockData());
        legacyMaterialIds.put("251", Material.WHITE_CONCRETE.createBlockData());
        legacyMaterialIds.put("251:1", Material.ORANGE_CONCRETE.createBlockData());
        legacyMaterialIds.put("251:2", Material.MAGENTA_CONCRETE.createBlockData());
        legacyMaterialIds.put("251:3", Material.LIGHT_BLUE_CONCRETE.createBlockData());
        legacyMaterialIds.put("251:4", Material.YELLOW_CONCRETE.createBlockData());
        legacyMaterialIds.put("251:5", Material.LIME_CONCRETE.createBlockData());
        legacyMaterialIds.put("251:6", Material.PINK_CONCRETE.createBlockData());
        legacyMaterialIds.put("251:7", Material.GRAY_CONCRETE.createBlockData());
        legacyMaterialIds.put("251:8", Material.LIGHT_GRAY_CONCRETE.createBlockData());
        legacyMaterialIds.put("251:9", Material.CYAN_CONCRETE.createBlockData());
        legacyMaterialIds.put("251:10", Material.PURPLE_CONCRETE.createBlockData());
        legacyMaterialIds.put("251:11", Material.BLUE_CONCRETE.createBlockData());
        legacyMaterialIds.put("251:12", Material.BROWN_CONCRETE.createBlockData());
        legacyMaterialIds.put("251:13", Material.GREEN_CONCRETE.createBlockData());
        legacyMaterialIds.put("251:14", Material.RED_CONCRETE.createBlockData());
        legacyMaterialIds.put("251:15", Material.BLACK_CONCRETE.createBlockData());
        legacyMaterialIds.put("252", Material.WHITE_CONCRETE_POWDER.createBlockData());
        legacyMaterialIds.put("252:1", Material.ORANGE_CONCRETE_POWDER.createBlockData());
        legacyMaterialIds.put("252:2", Material.MAGENTA_CONCRETE_POWDER.createBlockData());
        legacyMaterialIds.put("252:3", Material.LIGHT_BLUE_CONCRETE_POWDER.createBlockData());
        legacyMaterialIds.put("252:4", Material.YELLOW_CONCRETE_POWDER.createBlockData());
        legacyMaterialIds.put("252:5", Material.LIME_CONCRETE_POWDER.createBlockData());
        legacyMaterialIds.put("252:6", Material.PINK_CONCRETE_POWDER.createBlockData());
        legacyMaterialIds.put("252:7", Material.GRAY_CONCRETE_POWDER.createBlockData());
        legacyMaterialIds.put("252:8", Material.LIGHT_GRAY_CONCRETE_POWDER.createBlockData());
        legacyMaterialIds.put("252:9", Material.CYAN_CONCRETE_POWDER.createBlockData());
        legacyMaterialIds.put("252:10", Material.PURPLE_CONCRETE_POWDER.createBlockData());
        legacyMaterialIds.put("252:11", Material.BLUE_CONCRETE_POWDER.createBlockData());
        legacyMaterialIds.put("252:12", Material.BROWN_CONCRETE_POWDER.createBlockData());
        legacyMaterialIds.put("252:13", Material.GREEN_CONCRETE_POWDER.createBlockData());
        legacyMaterialIds.put("252:14", Material.RED_CONCRETE_POWDER.createBlockData());
        legacyMaterialIds.put("252:15", Material.BLACK_CONCRETE_POWDER.createBlockData());
        legacyMaterialIds.put("253", Material.AIR.createBlockData()); // nothing
        legacyMaterialIds.put("254", Material.AIR.createBlockData()); // nothing
        legacyMaterialIds.put("255", Material.STRUCTURE_BLOCK.createBlockData());
    }

    private static BlockData slabFunction(Material material, Slab.Type type) {
        return new Function<Material, BlockData>() {
            /**
             * Applies this function to the given argument.
             *
             * @param material the function argument
             * @return the function result
             */
            @Override
            public BlockData apply(Material material) {
                var d = material.createBlockData();

                if(d instanceof Slab slab) {
                    slab.setType(type);
                    return d;
                }
                return null;
            }
        }.apply(material);
    }

    private static BlockData analoguePowerableFunction(Material material, int power) {
        return new Function<Material, BlockData>() {
            /**
             * Applies this function to the given argument.
             *
             * @param material the function argument
             * @return the function result
             */
            @Override
            public BlockData apply(Material material) {
                var d = material.createBlockData();

                if(d instanceof AnaloguePowerable analogue) {
                    analogue.setPower(power);
                    return d;
                }
                return null;
            }
        }.apply(material);
    }

    private static BlockData lightableFunction(Material material, boolean lit) {
        return new Function<Material, BlockData>() {
            /**
             * Applies this function to the given argument.
             *
             * @param material the function argument
             * @return the function result
             */
            @Override
            public BlockData apply(Material material) {
                var d = material.createBlockData();

                if(d instanceof Lightable lightable) {
                    lightable.setLit(lit);
                    return d;
                }
                return null;
            }
        }.apply(material);
    }

    private static BlockData levelableFunction(Material material, int level) {
        return new Function<Material, BlockData>() {
            /**
             * Applies this function to the given argument.
             *
             * @param material the function argument
             * @return the function result
             */
            @Override
            public BlockData apply(Material material) {
                var d = material.createBlockData();

                if(d instanceof Levelled levelled) {
                    levelled.setLevel(level);
                    return d;
                }
                return null;
            }
        }.apply(material);
    }
}
