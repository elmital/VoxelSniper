package com.thevoxelbox.voxelsniper.brush;

import com.google.common.collect.Lists;
import com.thevoxelbox.voxelsniper.VoxelMessage;
import com.thevoxelbox.voxelsniper.snipe.SnipeData;
import com.thevoxelbox.voxelsniper.snipe.Undo;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * @author Ghost8700 @ Voxel
 */
public class GenerateTreeBrush extends Brush {
    private final List<Material> validWood = List.of(Material.OAK_LOG, Material.ACACIA_LOG, Material.SPRUCE_LOG, Material.JUNGLE_LOG, Material.DARK_OAK_LOG, Material.BIRCH_LOG, Material.OAK_WOOD
            , Material.ACACIA_WOOD, Material.SPRUCE_WOOD, Material.JUNGLE_WOOD, Material.DARK_OAK_WOOD, Material.BIRCH_WOOD, Material.CRIMSON_HYPHAE, Material.CRIMSON_STEM, Material.WARPED_HYPHAE, Material.WARPED_STEM, Material.MANGROVE_LOG, Material.MANGROVE_WOOD, Material.CHERRY_LOG, Material.CHERRY_WOOD);
    private final List<Material> validLeaves = List.of(Material.OAK_LEAVES, Material.ACACIA_LEAVES, Material.SPRUCE_LEAVES
            , Material.JUNGLE_LEAVES, Material.DARK_OAK_LEAVES, Material.BIRCH_LEAVES, Material.WARPED_WART_BLOCK, Material.NETHER_WART_BLOCK, Material.MANGROVE_LEAVES, Material.CHERRY_LEAVES);

    // Tree Variables.
    private final Random randGenerator = new Random();
    private final ArrayList<Block> branchBlocks = new ArrayList<>();
    private Undo undo;
    // If these default values are edited. Remember to change default values in the default preset.
    private Material leavesMaterial = Material.OAK_LEAVES;
    private Material woodMaterial = Material.OAK_LOG;
    private boolean rootFloat = false;
    private int startHeight = 0;
    private int rootLength = 9;
    private int maxRoots = 2;
    private int minRoots = 1;
    private int thickness = 1;
    private int slopeChance = 40;
    private final int twistChance = 5; // This is a hidden value not available through Parameters. Otherwise, messy.
    private int heightMinimum = 14;
    private int heightMaximum = 18;
    private int branchLength = 8;
    private int nodeMax = 4;
    private int nodeMin = 3;

    private int blockPositionX;
    private int blockPositionY;
    private int blockPositionZ;

    /**
     *
     */
    public GenerateTreeBrush() {
        this.setName("Generate Tree");
    }

    // Branch Creation based on direction chosen from the parameters passed.
    private void branchCreate(final int xDirection, final int zDirection) {

        // Sets branch origin.
        final int originX = blockPositionX;
        final int originY = blockPositionY;
        final int originZ = blockPositionZ;

        // Sets direction preference.
        final int xPreference = this.randGenerator.nextInt(60) + 20;
        final int zPreference = this.randGenerator.nextInt(60) + 20;

        // Iterates according to branch length.
        for (int r = 0; r < this.branchLength; r++) {

            // Alters direction according to preferences.
            if (this.randGenerator.nextInt(100) < xPreference) {
                blockPositionX = blockPositionX + xDirection;
            }
            if (this.randGenerator.nextInt(100) < zPreference) {
                blockPositionZ = blockPositionZ + zDirection;
            }

            // 50% chance to increase elevation every second block.
            if (Math.abs(r % 2) == 1) {
                blockPositionY = blockPositionY + this.randGenerator.nextInt(2);
            }

            // Add block to undo function.
            if (this.getBlockMaterialAt(blockPositionX, blockPositionY, blockPositionZ) != woodMaterial) {
                this.undo.put(this.clampY(blockPositionX, blockPositionY, blockPositionZ));
            }

            // Creates a branch block.
            this.clampY(blockPositionX, blockPositionY, blockPositionZ).setBlockData(woodMaterial.createBlockData(), false);
            this.branchBlocks.add(this.clampY(blockPositionX, blockPositionY, blockPositionZ));
        }

        // Resets the origin
        blockPositionX = originX;
        blockPositionY = originY;
        blockPositionZ = originZ;
    }

    private void leafNodeCreate() {
        // Generates the node size.
        final int nodeRadius = this.randGenerator.nextInt(this.nodeMax - this.nodeMin + 1) + this.nodeMin;
        final double bSquared = Math.pow(nodeRadius + 0.5, 2);

        // Lowers the current block in order to start at the bottom of the node.
        blockPositionY = blockPositionY - 2;

        for (int z = nodeRadius; z >= 0; z--) {
            final double zSquared = Math.pow(z, 2);

            for (int x = nodeRadius; x >= 0; x--) {
                final double xSquared = Math.pow(x, 2);

                for (int y = nodeRadius; y >= 0; y--) {
                    if ((xSquared + Math.pow(y, 2) + zSquared) <= bSquared) {
                        // Chance to skip creation of a block.
                        if (this.randGenerator.nextInt(100) >= 30) {
                            // If block is Air, create a leaf block.
                            if (this.getWorld().getBlockAt(blockPositionX + x, blockPositionY + y, blockPositionZ + z).getType().isAir()) {
                                // Adds block to undo function.
                                if (this.getBlockMaterialAt(blockPositionX + x, blockPositionY + y, blockPositionZ + z) != leavesMaterial) {
                                    this.undo.put(this.clampY(blockPositionX + x, blockPositionY + y, blockPositionZ + z));
                                }
                                // Creates block.
                                this.clampY(blockPositionX + x, blockPositionY + y, blockPositionZ + z).setBlockData(leavesMaterial.createBlockData(), false);
                            }
                        }
                        if (this.randGenerator.nextInt(100) >= 30) {
                            if (this.getWorld().getBlockAt(blockPositionX + x, blockPositionY + y, blockPositionZ - z).getType().isAir()) {
                                if (this.getBlockMaterialAt(blockPositionX + x, blockPositionY + y, blockPositionZ - z) != leavesMaterial) {
                                    this.undo.put(this.clampY(blockPositionX + x, blockPositionY + y, blockPositionZ - z));
                                }
                                this.clampY(blockPositionX + x, blockPositionY + y, blockPositionZ - z).setBlockData(leavesMaterial.createBlockData(), false);
                            }
                        }
                        if (this.randGenerator.nextInt(100) >= 30) {
                            if (this.getWorld().getBlockAt(blockPositionX - x, blockPositionY + y, blockPositionZ + z).getType().isAir()) {
                                if (this.getBlockMaterialAt(blockPositionX - x, blockPositionY + y, blockPositionZ + z) != leavesMaterial) {
                                    this.undo.put(this.clampY(blockPositionX - x, blockPositionY + y, blockPositionZ + z));
                                }
                                this.clampY(blockPositionX - x, blockPositionY + y, blockPositionZ + z).setBlockData(leavesMaterial.createBlockData(), false);
                            }
                        }
                        if (this.randGenerator.nextInt(100) >= 30) {
                            if (this.getWorld().getBlockAt(blockPositionX - x, blockPositionY + y, blockPositionZ - z).getType().isAir()) {
                                if (this.getBlockMaterialAt(blockPositionX - x, blockPositionY + y, blockPositionZ - z) != leavesMaterial) {
                                    this.undo.put(this.clampY(blockPositionX - x, blockPositionY + y, blockPositionZ - z));
                                }
                                this.clampY(blockPositionX - x, blockPositionY + y, blockPositionZ - z).setBlockData(leavesMaterial.createBlockData(), false);
                            }
                        }
                        if (this.randGenerator.nextInt(100) >= 30) {
                            if (this.getWorld().getBlockAt(blockPositionX + x, blockPositionY - y, blockPositionZ + z).getType().isAir()) {
                                if (this.getBlockMaterialAt(blockPositionX + x, blockPositionY - y, blockPositionZ + z) != leavesMaterial) {
                                    this.undo.put(this.clampY(blockPositionX + x, blockPositionY - y, blockPositionZ + z));
                                }
                                this.clampY(blockPositionX + x, blockPositionY - y, blockPositionZ + z).setBlockData(leavesMaterial.createBlockData(), false);
                            }
                        }
                        if (this.randGenerator.nextInt(100) >= 30) {
                            if (this.getWorld().getBlockAt(blockPositionX + x, blockPositionY - y, blockPositionZ - z).getType().isAir()) {
                                if (this.getBlockMaterialAt(blockPositionX + x, blockPositionY - y, blockPositionZ - z) != leavesMaterial) {
                                    this.undo.put(this.clampY(blockPositionX + x, blockPositionY - y, blockPositionZ - z));
                                }
                                this.clampY(blockPositionX + x, blockPositionY - y, blockPositionZ - z).setBlockData(leavesMaterial.createBlockData(), false);
                            }
                        }
                        if (this.randGenerator.nextInt(100) >= 30) {
                            if (this.getWorld().getBlockAt(blockPositionX - x, blockPositionY - y, blockPositionZ + z).getType().isAir()) {
                                if (this.getBlockMaterialAt(blockPositionX - x, blockPositionY - y, blockPositionZ + z) != leavesMaterial) {
                                    this.undo.put(this.clampY(blockPositionX - x, blockPositionY - y, blockPositionZ + z));
                                }
                                this.clampY(blockPositionX - x, blockPositionY - y, blockPositionZ + z).setBlockData(leavesMaterial.createBlockData(), false);
                            }
                        }
                        if (this.randGenerator.nextInt(100) >= 30) {
                            if (this.getWorld().getBlockAt(blockPositionX - x, blockPositionY - y, blockPositionZ - z).getType().isAir()) {
                                if (this.getBlockMaterialAt(blockPositionX - x, blockPositionY - y, blockPositionZ - z) != leavesMaterial) {
                                    this.undo.put(this.clampY(blockPositionX - x, blockPositionY - y, blockPositionZ - z));
                                }
                                this.clampY(blockPositionX - x, blockPositionY - y, blockPositionZ - z).setBlockData(leavesMaterial.createBlockData(), false);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Code Concerning Root Generation.
     *
     * @param xDirection
     * @param zDirection
     */
    private void rootCreate(final int xDirection, final int zDirection) {
        // Sets Origin.
        final int originX = blockPositionX;
        final int originY = blockPositionY;
        final int originZ = blockPositionZ;

        // Generates the number of roots to create.
        final int roots = this.randGenerator.nextInt(this.maxRoots - this.minRoots + 1) + this.minRoots;

        // A roots preference to move along the X and Y axis.
        // Loops for each root to be created.
        for (int i = 0; i < roots; i++) {
            // Pushes the root'world starting point out from the center of the tree.
            for (int t = 0; t < this.thickness - 1; t++) {
                blockPositionX = blockPositionX + xDirection;
                blockPositionZ = blockPositionZ + zDirection;
            }

            // Generate directional preference between 30% and 70%
            final int xPreference = this.randGenerator.nextInt(30) + 40;
            final int zPreference = this.randGenerator.nextInt(30) + 40;

            for (int j = 0; j < this.rootLength; j++) {
                // For the purposes of this algorithm, logs aren't considered solid.

                // If not solid then...
                // Save for undo function
                if (this.getBlockMaterialAt(blockPositionX, blockPositionY, blockPositionZ) != woodMaterial) {
                    this.undo.put(this.clampY(blockPositionX, blockPositionY, blockPositionZ));

                    // Place log block.
                    this.clampY(blockPositionX, blockPositionY, blockPositionZ).setBlockData(woodMaterial.createBlockData(), false);
                } else {
                    // If solid then...
                    // End loop
                    break;
                }

                // Checks is block below is solid
                if (this.clampY(blockPositionX, blockPositionY - 1, blockPositionZ).getType().isAir() || this.clampY(blockPositionX, blockPositionY - 1, blockPositionZ).getType() == Material.WATER || this.clampY(blockPositionX, blockPositionY - 1, blockPositionZ).getType() == Material.SNOW || this.clampY(blockPositionX, blockPositionY - 1, blockPositionZ).getType() == Material.OAK_LOG || this.clampY(blockPositionX, blockPositionY - 1, blockPositionZ).getType() == Material.ACACIA_LOG || this.clampY(blockPositionX, blockPositionY - 1, blockPositionZ).getType() == Material.BIRCH_LOG || this.clampY(blockPositionX, blockPositionY - 1, blockPositionZ).getType() == Material.DARK_OAK_LOG || this.clampY(blockPositionX, blockPositionY - 1, blockPositionZ).getType() == Material.JUNGLE_LOG || this.clampY(blockPositionX, blockPositionY - 1, blockPositionZ).getType() == Material.SPRUCE_LOG) {
                    // Mos down if solid.
                    blockPositionY = blockPositionY - 1;
                    if (this.rootFloat) {
                        if (this.randGenerator.nextInt(100) < xPreference) {
                            blockPositionX = blockPositionX + xDirection;
                        }
                        if (this.randGenerator.nextInt(100) < zPreference) {
                            blockPositionZ = blockPositionZ + zDirection;
                        }
                    }
                } else {
                    // If solid then move.
                    if (this.randGenerator.nextInt(100) < xPreference) {
                        blockPositionX = blockPositionX + xDirection;
                    }
                    if (this.randGenerator.nextInt(100) < zPreference) {
                        blockPositionZ = blockPositionZ + zDirection;
                    }
                    // Checks if new location is solid, if not then move down.
                    if (this.clampY(blockPositionX, blockPositionY - 1, blockPositionZ).getType().isAir() || this.clampY(blockPositionX, blockPositionY - 1, blockPositionZ).getType() == Material.WATER || this.clampY(blockPositionX, blockPositionY - 1, blockPositionZ).getType() == Material.SNOW || this.clampY(blockPositionX, blockPositionY - 1, blockPositionZ).getType() == Material.OAK_LOG || this.clampY(blockPositionX, blockPositionY - 1, blockPositionZ).getType() == Material.ACACIA_LOG || this.clampY(blockPositionX, blockPositionY - 1, blockPositionZ).getType() == Material.BIRCH_LOG || this.clampY(blockPositionX, blockPositionY - 1, blockPositionZ).getType() == Material.DARK_OAK_LOG || this.clampY(blockPositionX, blockPositionY - 1, blockPositionZ).getType() == Material.JUNGLE_LOG || this.clampY(blockPositionX, blockPositionY - 1, blockPositionZ).getType() == Material.SPRUCE_LOG) {
                        blockPositionY = blockPositionY - 1;
                    }
                }
            }

            // Reset origin.
            blockPositionX = originX;
            blockPositionY = originY;
            blockPositionZ = originZ;

        }
    }

    private void rootGen() {
        // Quadrant 1
        this.rootCreate(1, 1);

        // Quadrant 2
        this.rootCreate(-1, 1);

        // Quadrant 3
        this.rootCreate(1, -1);

        // Quadrant 4
        this.rootCreate(-1, -1);
    }

    private void trunkCreate() {
        // Creates true circle discs of the set size using the wood type selected.
        final double bSquared = Math.pow(this.thickness + 0.5, 2);

        for (int x = this.thickness; x >= 0; x--) {
            final double xSquared = Math.pow(x, 2);

            for (int z = this.thickness; z >= 0; z--) {
                if ((xSquared + Math.pow(z, 2)) <= bSquared) {
                    // If block is air, then create a block.
                    if (this.getWorld().getBlockAt(blockPositionX + x, blockPositionY, blockPositionZ + z).getType().isAir()) {
                        // Adds block to undo function.
                        if (this.getBlockMaterialAt(blockPositionX + x, blockPositionY, blockPositionZ + z) != woodMaterial) {
                            this.undo.put(this.clampY(blockPositionX + x, blockPositionY, blockPositionZ + z));
                        }
                        // Creates block.
                        this.clampY(blockPositionX + x, blockPositionY, blockPositionZ + z).setBlockData(woodMaterial.createBlockData(), false);
                    }
                    if (this.getWorld().getBlockAt(blockPositionX + x, blockPositionY, blockPositionZ - z).getType().isAir()) {
                        if (this.getBlockMaterialAt(blockPositionX + x, blockPositionY, blockPositionZ - z) != woodMaterial) {
                            this.undo.put(this.clampY(blockPositionX + x, blockPositionY, blockPositionZ - z));
                        }
                        this.clampY(blockPositionX + x, blockPositionY, blockPositionZ - z).setBlockData(woodMaterial.createBlockData(), false);
                    }
                    if (this.getWorld().getBlockAt(blockPositionX - x, blockPositionY, blockPositionZ + z).getType().isAir()) {
                        if (this.getBlockMaterialAt(blockPositionX - x, blockPositionY, blockPositionZ + z) != woodMaterial) {
                            this.undo.put(this.clampY(blockPositionX - x, blockPositionY, blockPositionZ + z));
                        }
                        this.clampY(blockPositionX - x, blockPositionY, blockPositionZ + z).setBlockData(woodMaterial.createBlockData(), false);
                    }
                    if (this.getWorld().getBlockAt(blockPositionX - x, blockPositionY, blockPositionZ - z).getType().isAir()) {
                        if (this.getBlockMaterialAt(blockPositionX - x, blockPositionY, blockPositionZ - z) != woodMaterial) {
                            this.undo.put(this.clampY(blockPositionX - x, blockPositionY, blockPositionZ - z));
                        }
                        this.clampY(blockPositionX - x, blockPositionY, blockPositionZ - z).setBlockData(woodMaterial.createBlockData(), false);
                    }
                }
            }
        }
    }

    /*
     * 
     * Code Concerning Trunk Generation
     */
    private void trunkGen() {
        // Sets Origin
        final int originX = blockPositionX;
        final int originY = blockPositionY;
        final int originZ = blockPositionZ;

        // ----------
        // Main Trunk
        // ----------
        // Sets diretional preferences.
        int xPreference = this.randGenerator.nextInt(this.slopeChance);
        int zPreference = this.randGenerator.nextInt(this.slopeChance);

        // Sets direction.
        int xDirection = 1;
        if (this.randGenerator.nextInt(100) < 50) {
            xDirection = -1;
        }

        int zDirection = 1;
        if (this.randGenerator.nextInt(100) < 50) {
            zDirection = -1;
        }

        // Generates a height for trunk.
        int height = this.randGenerator.nextInt(this.heightMaximum - this.heightMinimum + 1) + this.heightMinimum;

        for (int p = 0; p < height; p++) {
            if (p > 3) {
                if (this.randGenerator.nextInt(100) <= this.twistChance) {
                    xDirection *= -1;
                }
                if (this.randGenerator.nextInt(100) <= this.twistChance) {
                    zDirection *= -1;
                }
                if (this.randGenerator.nextInt(100) < xPreference) {
                    blockPositionX += xDirection;
                }
                if (this.randGenerator.nextInt(100) < zPreference) {
                    blockPositionZ += zDirection;
                }
            }

            // Creates trunk section
            this.trunkCreate();

            // Mos up for next section
            blockPositionY = blockPositionY + 1;
        }

        // Generates branchs at top of trunk for each quadrant.
        this.branchCreate(1, 1);
        this.branchCreate(-1, 1);
        this.branchCreate(1, -1);
        this.branchCreate(-1, -1);

        // Reset Origin for next trunk.
        blockPositionX = originX;
        blockPositionY = originY + 4;
        blockPositionZ = originZ;

        // ---------------
        // Secondary Trunk
        // ---------------
        // Sets diretional preferences.
        xPreference = this.randGenerator.nextInt(this.slopeChance);
        zPreference = this.randGenerator.nextInt(this.slopeChance);

        // Sets direction.
        xDirection = 1;
        if (this.randGenerator.nextInt(100) < 50) {
            xDirection = -1;
        }

        zDirection = 1;
        if (this.randGenerator.nextInt(100) < 50) {
            zDirection = -1;
        }

        // Generates a height for trunk.
        height = this.randGenerator.nextInt(this.heightMaximum - this.heightMinimum + 1) + this.heightMinimum;

        if (height > 4) {
            for (int p = 0; p < height; p++) {
                if (this.randGenerator.nextInt(100) <= this.twistChance) {
                    xDirection *= -1;
                }
                if (this.randGenerator.nextInt(100) <= this.twistChance) {
                    zDirection *= -1;
                }
                if (this.randGenerator.nextInt(100) < xPreference) {
                    blockPositionX = blockPositionX + xDirection;
                }
                if (this.randGenerator.nextInt(100) < zPreference) {
                    blockPositionZ = blockPositionZ + zDirection;
                }

                // Creates a trunk section
                this.trunkCreate();

                // Mos up for next section
                blockPositionY = blockPositionY + 1;
            }

            // Generate branches at top of trunk for each quadrant.
            this.branchCreate(1, 1);
            this.branchCreate(-1, 1);
            this.branchCreate(1, -1);
            this.branchCreate(-1, -1);
        }
    }

    @Override
    protected final void arrow(final SnipeData v) {
        this.undo = new Undo();

        this.branchBlocks.clear();

        // Sets the location variables.
        blockPositionX = this.getTargetBlock().getX();
        blockPositionY = this.getTargetBlock().getY() + this.startHeight;
        blockPositionZ = this.getTargetBlock().getZ();

        // Generates the roots.
        this.rootGen();

        // Generates the trunk, which also generates branches.
        this.trunkGen();

        // Each branch block was saved in an array. This is now fed through an array.
        // This array takes each branch block and constructs a leaf node around it.
        for (final Block block : this.branchBlocks) {
            blockPositionX = block.getX();
            blockPositionY = block.getY();
            blockPositionZ = block.getZ();
            this.leafNodeCreate();
        }

        // Ends the undo function and mos on.
        v.owner().storeUndo(this.undo);
    }

    // The Powder currently does nothing extra.
    @Override
    protected final void powder(final SnipeData v) {
        this.arrow(v);
    }

    @Override
    public final void info(final VoxelMessage vm) {
        vm.brushName(this.getName());
    }

    @Override
    public final void parseParameters(final String triggerHandle, final String[] params, final SnipeData v) {

        if (params[0].equalsIgnoreCase("info")) {
            if (params.length == 1 || params[1].equals("1")) {
                v.getVoxelMessage().commandParameters("Generate Tree Brush Parameters:         [1/2]"
                        , null
                        , "/b " + triggerHandle + "leaves [material]  -- leaves type"
                        , "/b " + triggerHandle + "wood [material]  -- wood type"
                        , "/b " + triggerHandle + "thickness [number]  -- tree thickness"
                        , "/b " + triggerHandle + "startHeight [number] -- starting height"
                        , "/b " + triggerHandle + "slope [0 - 100]  -- trunk slope chance"
                        , "/b " + triggerHandle + "branchLength [number]  -- branch length"
                        , "/b " + triggerHandle + "rootLength [number]  -- root length"
                        , "/b " + triggerHandle + "rootFloat [true/false]  -- root float "
                        , "/b " + triggerHandle + "info 2  -- next page"
                );
            } else if (params[1].equals("2")) {
                v.getVoxelMessage().commandParameters("Generate Tree Brush Parameters:         [2/2]"
                        , null
                        , "/b " + triggerHandle + "rootMin [number]  -- minimum roots"
                        , "/b " + triggerHandle + "rootMax [number]  -- maximum roots"
                        , "/b " + triggerHandle + "heightMin [number]  -- minimum height"
                        , "/b " + triggerHandle + "heightMax [number]  -- maximum height"
                        , "/b " + triggerHandle + "leavesMin [number]  -- minimum leaf node size"
                        , "/b " + triggerHandle + "leavesMax [number]  -- maximum leaf node size"
                        , "/b " + triggerHandle + "default  -- restore default params"
                );
            }
            return;
        }
        try {
            if (params[0].equalsIgnoreCase("leaves")) {
                Material material = Material.valueOf(params[1]);

                if (validLeaves.contains(material)) {
                    this.leavesMaterial = material;
                    v.sendMessage(Component.text("Leaves material set to " + this.leavesMaterial.name()).color(NamedTextColor.BLUE));
                } else {
                    throw new IllegalArgumentException();
                }
                return;
            }

            if (params[0].equalsIgnoreCase("wood")) {
                Material material = Material.valueOf(params[1]);

                if (validWood.contains(material)) {
                    this.woodMaterial = material;
                    v.sendMessage(Component.text("Wood log material set to " + this.leavesMaterial.name()).color(NamedTextColor.BLUE));
                } else {
                    throw new IllegalArgumentException();
                }
                return;
            }
        } catch (IllegalArgumentException e) {
            v.getVoxelMessage().brushMessageError("Not a valid material type.");
            return;
        }

        try {
            if (params[0].equalsIgnoreCase("thickness")) { // Tree Thickness
                this.thickness = Integer.parseInt(params[1]);
                v.sendMessage(Component.text("Thickness set to " + this.thickness).color(NamedTextColor.BLUE));
                return;
            }

            if (params[0].equalsIgnoreCase("startHeight")) { // Starting Height
                this.startHeight = Integer.parseInt(params[1]);
                v.sendMessage(Component.text("Starting height set to " + this.startHeight).color(NamedTextColor.BLUE));
                return;
            }

            if (params[0].equalsIgnoreCase("slope")) { // Trunk Slope Chance
                this.slopeChance = Integer.parseInt(params[1]);
                v.sendMessage(Component.text("Trunk slope set to " + this.slopeChance + "% chance").color(NamedTextColor.BLUE));
                return;
            }

            if (params[0].equalsIgnoreCase("branchLength")) { // Branch Length
                this.branchLength = Integer.parseInt(params[1]);
                v.sendMessage(Component.text("Branch length set to " + this.branchLength).color(NamedTextColor.BLUE));
                return;
            }

            if (params[0].equalsIgnoreCase("rootLength")) { // Root Length
                this.rootLength = Integer.parseInt(params[1]);
                v.sendMessage(Component.text("Root length set to " + this.rootLength).color(NamedTextColor.BLUE));
                return;
            }

            if (params[0].equalsIgnoreCase("rootFloat")) { // Root Float
                this.rootFloat = Boolean.parseBoolean(params[1]);
                v.sendMessage(Component.text("Floating roots set to " + this.rootFloat).color(NamedTextColor.BLUE));
                return;
            }

            if (params[0].equalsIgnoreCase("rootMin")) { // Minimum Roots
                this.minRoots = Integer.parseInt(params[1]);
                if (this.minRoots > this.maxRoots) {
                    this.minRoots = this.maxRoots;
                    v.getVoxelMessage().brushMessageError("Minimum roots can't exceed maximum roots, has  been set to " + this.minRoots + " instead!");
                } else {
                    v.sendMessage(Component.text("Minimum roots set to " + this.minRoots).color(NamedTextColor.BLUE));
                }
                return;
            }

            if (params[0].equalsIgnoreCase("rootMax")) { // Maximum Roots
                this.maxRoots = Integer.parseInt(params[1]);
                if (this.minRoots > this.maxRoots) {
                    this.maxRoots = this.minRoots;
                    v.getVoxelMessage().brushMessageError("Maximum roots can't be lower than minimum roots, has been set to " + this.minRoots + " instead!");
                } else {
                    v.sendMessage(Component.text("Maximum roots set to " + this.maxRoots).color(NamedTextColor.BLUE));
                }
                return;
            }

            if (params[0].equalsIgnoreCase("heightMin")) { // Height Minimum
                this.heightMinimum = Integer.parseInt(params[1]);
                if (this.heightMinimum > this.heightMaximum) {
                    this.heightMinimum = this.heightMaximum;
                    v.getVoxelMessage().brushMessageError("Minimum height exceed than maximum height, has been set to " + this.heightMinimum + " instead!");
                } else {
                    v.sendMessage(Component.text("Minimum height set to " + this.heightMinimum).color(NamedTextColor.BLUE));
                }
                return;
            }

            if (params[0].equalsIgnoreCase("heightMax")) { // Height Maximum
                this.heightMaximum = Integer.parseInt(params[1]);
                if (this.heightMinimum > this.heightMaximum) {
                    this.heightMaximum = this.heightMinimum;
                    v.getVoxelMessage().brushMessageError("Maximum height can't be lower than minimum height, has been set to " + this.heightMaximum + " instead!");
                } else {
                    v.sendMessage(Component.text("Maximum height set to " + this.heightMaximum).color(NamedTextColor.BLUE));
                }
                return;
            }

            if (params[0].equalsIgnoreCase("leavesMax")) { // Leaf Node Max Size
                this.nodeMax = Integer.parseInt(params[1]);
                v.sendMessage(Component.text("Leaf thickness set to " + this.nodeMax).color(NamedTextColor.BLUE));
                return;
            }

            if (params[0].equalsIgnoreCase("leavesMin")) { // Leaf Node Min Size
                this.nodeMin = Integer.parseInt(params[1]);
                v.sendMessage(Component.text("Leaf thickness set to " + this.nodeMin).color(NamedTextColor.BLUE));
                return;
            }
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException ignored) {

        }

        if (params[0].equalsIgnoreCase("default")) { // Default settings.
            this.leavesMaterial = Material.OAK_LEAVES;
            this.woodMaterial = Material.OAK_LOG;
            this.rootFloat = false;
            this.startHeight = 0;
            this.rootLength = 9;
            this.maxRoots = 2;
            this.minRoots = 1;
            this.thickness = 1;
            this.slopeChance = 40;
            this.heightMinimum = 14;
            this.heightMaximum = 18;
            this.branchLength = 8;
            this.nodeMax = 4;
            this.nodeMin = 3;
            v.sendMessage(Component.text("Brush reset to default parameters.").color(NamedTextColor.GOLD));
            return;
        }

        v.getVoxelMessage().invalidUseParameter(triggerHandle);
    }

    @Override
    public List<String> registerArguments() {
        return new ArrayList<>(Lists.newArrayList("leaves", "wood", "thickness", "startHeight", "branchLength", "slope", "rootLength",
                "rootFloat", "info", "rootMin", "rootMax", "heightMin", "heightMax", "leavesMin", "leavesMax", "default"));
    }

    @Override
    public HashMap<String, List<String>> registerArgumentValues() {
        HashMap<String, List<String>> argumentValues = new HashMap<>();
        
        // Number variables
        argumentValues.put("thickness", Lists.newArrayList("[number]"));
        argumentValues.put("startHeight", Lists.newArrayList("[number]"));
        argumentValues.put("slope", Lists.newArrayList("[number]"));
        argumentValues.put("branchLength", Lists.newArrayList("[number]"));
        argumentValues.put("rootLength", Lists.newArrayList("[number]"));
        argumentValues.put("rootMin", Lists.newArrayList("[number]"));
        argumentValues.put("rootMax", Lists.newArrayList("[number]"));
        argumentValues.put("heightMin", Lists.newArrayList("[number]"));
        argumentValues.put("heightMax", Lists.newArrayList("[number]"));
        argumentValues.put("leavesMin", Lists.newArrayList("[number]"));
        argumentValues.put("leavesMax", Lists.newArrayList("[number]"));

        // Info variables
        argumentValues.put("info", Lists.newArrayList("1", "2"));

        // True/false variable
        argumentValues.put("rootFloat", Lists.newArrayList("true", "false"));

        // Wood material variables
        argumentValues.put("wood", validWood.stream().map(Material::name).toList());

        // Leaves material variables
        argumentValues.put("leaves", validLeaves.stream().map(Material::name).toList());

        return argumentValues;
    }

    @Override
    public String getPermissionNode() {
        return "voxelsniper.brush.generatetree";
    }
}
