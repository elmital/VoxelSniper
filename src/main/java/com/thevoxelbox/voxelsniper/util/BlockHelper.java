package com.thevoxelbox.voxelsniper.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Art;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Painting;
import org.bukkit.entity.Player;


/**
 * @author Voxel
 */
public class BlockHelper {

    public static final int MAXIMUM_WORLD_HEIGHT = 355;
    public static final int MINIMUM_WORLD_HEIGHT = -64;
    private static final double DEFAULT_PLAYER_VIEW_HEIGHT = 1.65;
    private static final double DEFAULT_LOCATION_VIEW_HEIGHT = 0;
    private static final double DEFAULT_STEP = 0.2;
    private static final int DEFAULT_RANGE = 414;

    private Location playerLoc;
    private double rotX, rotY, viewHeight, rotXSin, rotXCos, rotYSin, rotYCos;
    private double length, hLength, step;
    private double range;
    private double playerX, playerY, playerZ;
    private double xOffset, yOffset, zOffset;
    private int lastX, lastY, lastZ;
    private int targetX, targetY, targetZ;
    private World world;

    /**
     * Constructor requiring location, uses default values.
     *
     * @param location
     */
    public BlockHelper(final Location location) {
        this.init(location, BlockHelper.DEFAULT_RANGE, BlockHelper.DEFAULT_STEP, BlockHelper.DEFAULT_LOCATION_VIEW_HEIGHT);
    }

    /**
     * Constructor requiring location, max range, and a stepping value.
     *
     * @param location
     * @param range
     * @param step
     */
    public BlockHelper(final Location location, final int range, final double step) {
        this.world = location.getWorld();
        this.init(location, range, step, BlockHelper.DEFAULT_LOCATION_VIEW_HEIGHT);
    }

    /**
     * Constructor requiring player, max range, and a stepping value.
     *
     * @param player
     * @param range
     * @param step
     */
    public BlockHelper(final Player player, final int range, final double step) {
        this.init(player.getLocation(), range, step, BlockHelper.DEFAULT_PLAYER_VIEW_HEIGHT);
    }

    /**
     * Constructor requiring player, uses default values.
     *
     * @param player
     * @param world
     */
    public BlockHelper(final Player player, final World world) {
        this.world = world;
        this.init(player.getLocation(), BlockHelper.DEFAULT_RANGE, BlockHelper.DEFAULT_STEP, BlockHelper.DEFAULT_PLAYER_VIEW_HEIGHT);
        // values
    }

    /**
     * @param player
     * @param world
     * @param range
     */
    public BlockHelper(final Player player, final World world, final double range) {
        this.world = world;
        this.init(player.getLocation(), range, BlockHelper.DEFAULT_STEP, BlockHelper.DEFAULT_PLAYER_VIEW_HEIGHT);
        this.fromOffWorld();
    }

    /**
     *
     */
    public final void fromOffWorld() {
        if (this.targetY > BlockHelper.MAXIMUM_WORLD_HEIGHT) {
            while (this.targetY > BlockHelper.MAXIMUM_WORLD_HEIGHT && this.length <= this.range) {
                this.lastX = this.targetX;
                this.lastY = this.targetY;
                this.lastZ = this.targetZ;

                do {
                    this.length += this.step;

                    this.hLength = (this.length * this.rotYCos);
                    this.yOffset = (this.length * this.rotYSin);
                    this.xOffset = (this.hLength * this.rotXCos);
                    this.zOffset = (this.hLength * this.rotXSin);

                    this.targetX = (int) Math.floor(this.xOffset + this.playerX);
                    this.targetY = (int) Math.floor(this.yOffset + this.playerY);
                    this.targetZ = (int) Math.floor(this.zOffset + this.playerZ);

                } while ((this.length <= this.range) && ((this.targetX == this.lastX) && (this.targetY == this.lastY) && (this.targetZ == this.lastZ)));
            }
        } else if (this.targetY < MINIMUM_WORLD_HEIGHT) {
            while (this.targetY < MINIMUM_WORLD_HEIGHT && this.length <= this.range) {
                this.lastX = this.targetX;
                this.lastY = this.targetY;
                this.lastZ = this.targetZ;

                do {
                    this.length += this.step;

                    this.hLength = (this.length * this.rotYCos);
                    this.yOffset = (this.length * this.rotYSin);
                    this.xOffset = (this.hLength * this.rotXCos);
                    this.zOffset = (this.hLength * this.rotXSin);

                    this.targetX = (int) Math.floor(this.xOffset + this.playerX);
                    this.targetY = (int) Math.floor(this.yOffset + this.playerY);
                    this.targetZ = (int) Math.floor(this.zOffset + this.playerZ);

                } while ((this.length <= this.range) && ((this.targetX == this.lastX) && (this.targetY == this.lastY) && (this.targetZ == this.lastZ)));
            }
        }
    }

    /**
     * Returns the current block along the line of vision.
     *
     * @return Block
     */
    public final Block getCurBlock() {
        if (this.length > this.range || this.targetY > BlockHelper.MAXIMUM_WORLD_HEIGHT || this.targetY < BlockHelper.MINIMUM_WORLD_HEIGHT) {
            return null;
        } else {
            return this.world.getBlockAt(this.targetX, this.targetY, this.targetZ);
        }
    }

    /**
     * Returns the block attached to the face at the cursor, or null if out of range.
     *
     * @return Block
     */
    public final Block getFaceBlock() {
        while (this.getNextBlock() != null) {
            var c = this.getCurBlock();
            if(c != null && !c.getType().isAir())
                break;
        }
        if (this.getCurBlock() != null) {
            return this.getLastBlock();
        } else {
            return null;
        }
    }

    /**
     * Returns the previous block along the line of vision.
     *
     * @return Block
     */
    public final Block getLastBlock() {
        if (this.lastY > BlockHelper.MAXIMUM_WORLD_HEIGHT || this.lastY < BlockHelper.MINIMUM_WORLD_HEIGHT) {
            return null;
        }
        return this.world.getBlockAt(this.lastX, this.lastY, this.lastZ);
    }

    /**
     * Returns STEPS forward along line of vision and returns block.
     *
     * @return Block
     */
    public final Block getNextBlock() {
        this.lastX = this.targetX;
        this.lastY = this.targetY;
        this.lastZ = this.targetZ;

        do {
            this.length += this.step;

            this.hLength = (this.length * this.rotYCos);
            this.yOffset = (this.length * this.rotYSin);
            this.xOffset = (this.hLength * this.rotXCos);
            this.zOffset = (this.hLength * this.rotXSin);

            this.targetX = (int) Math.floor(this.xOffset + this.playerX);
            this.targetY = (int) Math.floor(this.yOffset + this.playerY);
            this.targetZ = (int) Math.floor(this.zOffset + this.playerZ);

        } while ((this.length <= this.range) && ((this.targetX == this.lastX) && (this.targetY == this.lastY) && (this.targetZ == this.lastZ)));

        if (this.length > this.range || this.targetY > BlockHelper.MAXIMUM_WORLD_HEIGHT || this.targetY < BlockHelper.MINIMUM_WORLD_HEIGHT) {
            return null;
        }

        return this.world.getBlockAt(this.targetX, this.targetY, this.targetZ);
    }

    /**
     * @return Block
     */
    public final Block getRangeBlock() {
        this.fromOffWorld();
        if (this.length > this.range) {
            return null;
        } else {
            return this.getRange();
        }
    }

    /**
     * Returns the block at the cursor, or null if out of range.
     *
     * @return Block
     */
    public final Block getTargetBlock() {
        this.fromOffWorld();
        while (this.getNextBlock() != null) {
            var c = this.getCurBlock();
            if(c != null && !c.getType().isAir())
                break;
        }
        return this.getCurBlock();
    }

    private Block getRange() {
        this.lastX = this.targetX;
        this.lastY = this.targetY;
        this.lastZ = this.targetZ;

        do {
            this.length += this.step;

            this.hLength = (this.length * this.rotYCos);
            this.yOffset = (this.length * this.rotYSin);
            this.xOffset = (this.hLength * this.rotXCos);
            this.zOffset = (this.hLength * this.rotXSin);

            this.targetX = (int) Math.floor(this.xOffset + this.playerX);
            this.targetY = (int) Math.floor(this.yOffset + this.playerY);
            this.targetZ = (int) Math.floor(this.zOffset + this.playerZ);

        } while ((this.length <= this.range) && ((this.targetX == this.lastX) && (this.targetY == this.lastY) && (this.targetZ == this.lastZ)));

        if (!this.world.getBlockAt(this.targetX, this.targetY, this.targetZ).getType().isAir()) {
            return this.world.getBlockAt(this.targetX, this.targetY, this.targetZ);
        }

        if (this.length > this.range || this.targetY > BlockHelper.MAXIMUM_WORLD_HEIGHT || this.targetY < BlockHelper.MINIMUM_WORLD_HEIGHT) {
            return this.world.getBlockAt(this.lastX, this.lastY, this.lastZ);
        } else {
            return this.getRange();
        }
    }

    private void init(final Location location, final double range, final double step, final double viewHeight) {
        this.playerLoc = location;
        this.viewHeight = viewHeight;
        this.playerX = this.playerLoc.getX();
        this.playerY = this.playerLoc.getY() + this.viewHeight;
        this.playerZ = this.playerLoc.getZ();
        this.range = range;
        this.step = step;
        this.length = 0;
        this.rotX = (this.playerLoc.getYaw() + 90) % 360;
        this.rotY = this.playerLoc.getPitch() * -1;
        this.rotYCos = Math.cos(Math.toRadians(this.rotY));
        this.rotYSin = Math.sin(Math.toRadians(this.rotY));
        this.rotXCos = Math.cos(Math.toRadians(this.rotX));
        this.rotXSin = Math.sin(Math.toRadians(this.rotX));

        this.targetX = (int) Math.floor(this.playerLoc.getX());
        this.targetY = (int) Math.floor(this.playerLoc.getY() + this.viewHeight);
        this.targetZ = (int) Math.floor(this.playerLoc.getZ());
        this.lastX = this.targetX;
        this.lastY = this.targetY;
        this.lastZ = this.targetZ;
    }
    
    
    /**
     * The paint method used to scroll or set a painting to a specific type.
     *
     * @param p The player executing the method
     * @param auto Scroll automatically? If false will use 'choice' to try and set the painting
     * @param back Scroll in reverse?
     * @param choice Chosen index to set the painting to
     */
    @SuppressWarnings(value = "deprecation")
    public static void paint(final Player p, final boolean auto, final boolean back, final int choice) {
        Location targetLocation = p.getTargetBlock(null, 4).getLocation();
        Chunk paintingChunk = p.getTargetBlock(null, 4).getLocation().getChunk();
        double bestDistanceMatch = 50.0;
        Painting bestMatch = null;
        for (Entity entity : paintingChunk.getEntities()) {
            if (entity.getType() == EntityType.PAINTING) {
                double distance = targetLocation.distanceSquared(entity.getLocation());
                if (distance <= 4 && distance < bestDistanceMatch) {
                    bestDistanceMatch = distance;
                    bestMatch = (Painting) entity;
                }
            }
        }
        if (bestMatch != null) {
            if (auto) {
                try {
                    final int i = bestMatch.getArt().getId() + (back ? -1 : 1);
                    Art art = Art.getById(i);
                    if (art == null) {
                        p.sendMessage(Component.text("This is the final painting, try scrolling to the other direction.").color(NamedTextColor.RED));
                        return;
                    }
                    bestMatch.setArt(art);
                    p.sendMessage(Component.text("Painting set to ID: " + (i)).color(NamedTextColor.GREEN));
                } catch (final Exception e) {
                    p.sendMessage(Component.text("Oops. Something went wrong.").color(NamedTextColor.RED));
                }
            } else {
                try {
                    Art art = Art.getById(choice);
                    if(art == null)
                        throw new Exception();
                    bestMatch.setArt(art);
                    p.sendMessage(Component.text("Painting set to ID: " + choice).color(NamedTextColor.GREEN));
                } catch (final Exception exception) {
                    p.sendMessage(Component.text("Your input was invalid somewhere.").color(NamedTextColor.RED));
                }
            }
        }
    }
}
