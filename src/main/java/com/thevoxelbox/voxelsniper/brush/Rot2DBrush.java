package com.thevoxelbox.voxelsniper.brush;

import com.google.common.collect.Lists;
import com.thevoxelbox.voxelsniper.VoxelMessage;
import com.thevoxelbox.voxelsniper.snipe.SnipeData;
import com.thevoxelbox.voxelsniper.util.BlockWrapper;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Piotr
 */
public class Rot2DBrush extends Brush {

    private int bSize;
    private int brushSize;
    private BlockWrapper[][][] snap;
    private double se;

    /**
     *
     */
    public Rot2DBrush() {
        this.setName("2D Rotation");
    }

    private void getMatrix() {
        this.brushSize = (this.bSize * 2) + 1;

        this.snap = new BlockWrapper[this.brushSize][this.brushSize][this.brushSize];

        final double brushSizeSquared = Math.pow(this.bSize + 0.5, 2);
        int sx = this.getTargetBlock().getX() - this.bSize;
        int sy;
        int sz;

        for (int x = 0; x < this.snap.length; x++) {
            sz = this.getTargetBlock().getZ() - this.bSize;
            final double xSquared = Math.pow(x - this.bSize, 2);
            for (int y = 0; y < this.snap.length; y++) {
                sy = this.getTargetBlock().getY() - this.bSize;
                if (xSquared + Math.pow(y - this.bSize, 2) <= brushSizeSquared) {
                    for (int z = 0; z < this.snap.length; z++) {
                        final Block block = this.clampY(sx, sy, sz); // why is this not sx + x, sy + y sz + z?
                        this.snap[x][z][y] = new BlockWrapper(block);
                        block.setType(Material.AIR);
                        sy++;
                    }
                }
                sz++;
            }
            sx++;
        }
    }

    private void rotate(final SnipeData v) {
        final double brushSiyeSquared = Math.pow(this.bSize + 0.5, 2);
        final double cos = Math.cos(this.se);
        final double sin = Math.sin(this.se);
        final boolean[][] doNotFill = new boolean[this.snap.length][this.snap.length];
        // I put y in the inside loop, since it doesn't have any power functions, should be much faster.
        // Also, new array keeps track of which x and z coords are being assigned in the rotated space so that we can
        // do a targeted filling of only those columns later that were left out.

        for (int x = 0; x < this.snap.length; x++) {
            final int xx = x - this.bSize;
            final double xSquared = Math.pow(xx, 2);

            for (int y = 0; y < this.snap.length; y++) {
                final int zz = y - this.bSize;

                if (xSquared + Math.pow(zz, 2) <= brushSiyeSquared) {
                    final double newX = (xx * cos) - (zz * sin);
                    final double newZ = (xx * sin) + (zz * cos);

                    doNotFill[(int) newX + this.bSize][(int) newZ + this.bSize] = true;

                    for (int currentY = 0; currentY < this.snap.length; currentY++) {
                        final int yy = currentY - this.bSize;
                        final BlockWrapper block = this.snap[x][currentY][y];

                        if (block.getMaterial().isAir()) {
                            continue;
                        }
                        this.setBlockMaterialAndDataAt(this.getTargetBlock().getX() + (int) newX, this.getTargetBlock().getY() + yy, this.getTargetBlock().getZ() + (int) newZ, block.getBlockData());
                    }
                }
            }
        }
        for (int x = 0; x < this.snap.length; x++) {
            final double xSquared = Math.pow(x - this.bSize, 2);
            final int fx = x + this.getTargetBlock().getX() - this.bSize;

            for (int z = 0; z < this.snap.length; z++) {
                if (xSquared + Math.pow(z - this.bSize, 2) <= brushSiyeSquared) {
                    final int fz = z + this.getTargetBlock().getZ() - this.bSize;

                    if (!doNotFill[x][z]) {
                        // smart fill stuff

                        for (int y = 0; y < this.snap.length; y++) {
                            final int fy = y + this.getTargetBlock().getY() - this.bSize;

                            final BlockData a = this.getBlockDataAt(fx + 1, fy, fz);
                            final BlockData d = this.getBlockDataAt(fx - 1, fy, fz);
                            final BlockData c = this.getBlockDataAt(fx, fy, fz + 1);
                            final BlockData b = this.getBlockDataAt(fx, fy, fz - 1);

                            BlockData winner;

                            if (a.getMaterial() == b.getMaterial() || a.getMaterial() == c.getMaterial() || a.getMaterial() == d.getMaterial()) { // I figure that since we are already narrowing it down to ONLY the holes left behind, it
                                // should
                                // be fine to do all 5 checks needed to be legit about it.
                                winner = a;
                            } else if (b == d || c == d) {
                                winner = d;
                            } else {
                                winner = b; // blockPositionY making this default, it will also automatically cover situations where B = C;
                            }

                            this.setBlockMaterialAndDataAt(fx, fy, fz, winner);
                        }
                    }
                }
            }
        }
    }

    @Override
    protected final void arrow(final SnipeData v) {
        this.bSize = v.getBrushSize();

        this.getMatrix();
        this.rotate(v);
    }

    @Override
    protected final void powder(final SnipeData v) {
        this.bSize = v.getBrushSize();

        this.getMatrix();
        this.rotate(v);
    }

    @Override
    public final void info(final VoxelMessage vm) {
        vm.brushName(this.getName());
    }

    @Override
    public final void parseParameters(final String triggerHandle, final String[] params, final SnipeData v) {
        if (params[0].equalsIgnoreCase("info")) {
            v.getVoxelMessage().commandParameters("2D Rotation Brush Parameters:", null, "/b " + triggerHandle + " [number]  -- Set angle in degrees");
            return;
        }

        try {
            this.se = Math.toRadians(Double.parseDouble(params[0]));
            v.sendMessage(Component.text("Angle set to " + this.se).color(NamedTextColor.GREEN));
            return;
        } catch (NumberFormatException ignored) {
        }

        v.getVoxelMessage().invalidUseParameter(triggerHandle);
    }

    @Override
    public List<String> registerArguments() {
        return new ArrayList<>(Lists.newArrayList("[number]"));
    }

    @Override
    public String getPermissionNode() {
        return "voxelsniper.brush.rot2d";
    }
}
