package com.thevoxelbox.voxelsniper.brush;

import com.google.common.collect.Lists;
import com.thevoxelbox.voxelsniper.VoxelMessage;
import com.thevoxelbox.voxelsniper.brush.perform.PerformerBrush;
import com.thevoxelbox.voxelsniper.snipe.SnipeData;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * http://www.voxelwiki.com/minecraft/Voxelsniper#The_Blob_Brush
 *
 * @author Giltwist
 */
public class BlobBrush extends PerformerBrush {

    private static final int GROW_PERCENT_DEFAULT = 1000;
    private static final int GROW_PERCENT_MIN = 1;
    private static final int GROW_PERCENT_MAX = 9999;

    private final Random randomGenerator = new Random();
    private int growPercent = GROW_PERCENT_DEFAULT; // chance block on recursion pass is made active

    public BlobBrush() {
        this.setName("Blob");
    }

    private void checkValidGrowPercent(final SnipeData v) {
        if (this.growPercent < GROW_PERCENT_MIN || this.growPercent > GROW_PERCENT_MAX) {
            this.growPercent = GROW_PERCENT_DEFAULT;
            v.sendMessage(Component.text("Growth percent set to: 10%").color(NamedTextColor.BLUE));
        }
    }

    private void digBlob(final SnipeData v) {
        final int brushSize = v.getBrushSize();
        final int brushSizeDoubled = 2 * brushSize;
        final int[][][] splat = new int[brushSizeDoubled + 1][brushSizeDoubled + 1][brushSizeDoubled + 1];
        final int[][][] tempSplat = new int[brushSizeDoubled + 1][brushSizeDoubled + 1][brushSizeDoubled + 1];

        this.checkValidGrowPercent(v);

        // Seed the array
        for (int x = brushSizeDoubled; x >= 0; x--) {
            for (int y = brushSizeDoubled; y >= 0; y--) {
                for (int z = brushSizeDoubled; z >= 0; z--) {
                    if ((x == 0 || y == 0 | z == 0 || x == brushSizeDoubled || y == brushSizeDoubled || z == brushSizeDoubled) && this.randomGenerator.nextInt(GROW_PERCENT_MAX + 1) <= this.growPercent) {
                        splat[x][y][z] = 0;
                    } else {
                        splat[x][y][z] = 1;
                    }
                }
            }
        }

        // Grow the seed
        for (int r = 0; r < brushSize; r++) {
            for (int x = brushSizeDoubled; x >= 0; x--) {
                for (int y = brushSizeDoubled; y >= 0; y--) {
                    for (int z = brushSizeDoubled; z >= 0; z--) {
                        tempSplat[x][y][z] = splat[x][y][z];
                        double growCheck = 0;
                        if (splat[x][y][z] == 1) {
                            if (x != 0 && splat[x - 1][y][z] == 0) {
                                growCheck++;
                            }
                            if (y != 0 && splat[x][y - 1][z] == 0) {
                                growCheck++;
                            }
                            if (z != 0 && splat[x][y][z - 1] == 0) {
                                growCheck++;
                            }
                            if (x != 2 * brushSize && splat[x + 1][y][z] == 0) {
                                growCheck++;
                            }
                            if (y != 2 * brushSize && splat[x][y + 1][z] == 0) {
                                growCheck++;
                            }
                            if (z != 2 * brushSize && splat[x][y][z + 1] == 0) {
                                growCheck++;
                            }
                        }

                        if (growCheck >= 1 && this.randomGenerator.nextInt(GROW_PERCENT_MAX + 1) <= this.growPercent) {
                            tempSplat[x][y][z] = 0; // prevent bleed into splat
                        }
                    }
                }
            }

            // shouldn't this just be splat = tempsplat;? -Gavjenks
            // integrate tempsplat back into splat at end of iteration
            for (int x = brushSizeDoubled; x >= 0; x--) {
                for (int y = brushSizeDoubled; y >= 0; y--) {
                    for (int z = brushSizeDoubled; z >= 0; z--) {
                        splat[x][y][z] = tempSplat[x][y][z];
                    }
                }
            }
        }

        final double rSquared = Math.pow(brushSize + 1, 2);

        // Make the changes        
        for (int x = brushSizeDoubled; x >= 0; x--) {
            final double xSquared = Math.pow(x - brushSize - 1, 2);

            for (int y = brushSizeDoubled; y >= 0; y--) {
                final double ySquared = Math.pow(y - brushSize - 1, 2);

                for (int z = brushSizeDoubled; z >= 0; z--) {
                    if (splat[x][y][z] == 1 && xSquared + ySquared + Math.pow(z - brushSize - 1, 2) <= rSquared) {
                        this.currentPerformer.perform(this.clampY(this.getTargetBlock().getX() - brushSize + x, this.getTargetBlock().getY() - brushSize + z, this.getTargetBlock().getZ() - brushSize + y));
                    }
                }
            }
        }

        v.owner().storeUndo(this.currentPerformer.getUndo());
    }

    private void growBlob(final SnipeData v) {
        final int brushSize = v.getBrushSize();
        final int brushSizeDoubled = 2 * brushSize;
        final int[][][] splat = new int[brushSizeDoubled + 1][brushSizeDoubled + 1][brushSizeDoubled + 1];
        final int[][][] tempSplat = new int[brushSizeDoubled + 1][brushSizeDoubled + 1][brushSizeDoubled + 1];

        this.checkValidGrowPercent(v);

        // Seed the array
        splat[brushSize][brushSize][brushSize] = 1;

        // Grow the seed
        for (int r = 0; r < brushSize; r++) {

            for (int x = brushSizeDoubled; x >= 0; x--) {
                for (int y = brushSizeDoubled; y >= 0; y--) {
                    for (int z = brushSizeDoubled; z >= 0; z--) {
                        tempSplat[x][y][z] = splat[x][y][z];
                        int growCheck = 0;
                        if (splat[x][y][z] == 0) {
                            if (x != 0 && splat[x - 1][y][z] == 1) {
                                growCheck++;
                            }
                            if (y != 0 && splat[x][y - 1][z] == 1) {
                                growCheck++;
                            }
                            if (z != 0 && splat[x][y][z - 1] == 1) {
                                growCheck++;
                            }
                            if (x != 2 * brushSize && splat[x + 1][y][z] == 1) {
                                growCheck++;
                            }
                            if (y != 2 * brushSize && splat[x][y + 1][z] == 1) {
                                growCheck++;
                            }
                            if (z != 2 * brushSize && splat[x][y][z + 1] == 1) {
                                growCheck++;
                            }
                        }

                        if (growCheck >= 1 && this.randomGenerator.nextInt(GROW_PERCENT_MAX + 1) <= this.growPercent) {
                            // prevent bleed into splat
                            tempSplat[x][y][z] = 1;
                        }
                    }
                }
            }

            // integrate tempsplat back into splat at end of iteration
            for (int x = brushSizeDoubled; x >= 0; x--) {
                for (int y = brushSizeDoubled; y >= 0; y--) {
                    for (int z = brushSizeDoubled; z >= 0; z--) {
                        splat[x][y][z] = tempSplat[x][y][z];
                    }
                }
            }
        }

        final double rSquared = Math.pow(brushSize + 1, 2);

        // Make the changes
        for (int x = brushSizeDoubled; x >= 0; x--) {
            final double xSquared = Math.pow(x - brushSize - 1, 2);

            for (int y = brushSizeDoubled; y >= 0; y--) {
                final double ySquared = Math.pow(y - brushSize - 1, 2);

                for (int z = brushSizeDoubled; z >= 0; z--) {
                    if (splat[x][y][z] == 1 && xSquared + ySquared + Math.pow(z - brushSize - 1, 2) <= rSquared) {
                        this.currentPerformer.perform(this.clampY(this.getTargetBlock().getX() - brushSize + x, this.getTargetBlock().getY() - brushSize + z, this.getTargetBlock().getZ() - brushSize + y));
                    }
                }
            }
        }

        v.owner().storeUndo(this.currentPerformer.getUndo());
    }

    @Override
    protected final void arrow(final SnipeData v) {
        this.growBlob(v);
    }

    @Override
    protected final void powder(final SnipeData v) {
        this.digBlob(v);
    }

    @Override
    public final void info(final VoxelMessage vm) {
        this.checkValidGrowPercent(null);

        vm.brushName(this.getName());
        vm.size();
        vm.custom(Component.text("Growth percent set to: " + this.growPercent / 100 + "%").color(NamedTextColor.BLUE));
    }

    @Override
    public final void parseParameters(final String triggerHandle, final String[] params, final SnipeData v) {
        if (params[0].equalsIgnoreCase("info")) {
            v.getVoxelMessage().commandParameters("Blob Brush Parameters:", null
                    , "/b " + triggerHandle + " growth [number] -- Set growth percentage (between " + String.format("%.2f", ((float) GROW_PERCENT_MIN / 100)) + " to " + String.format("%.2f", ((float) GROW_PERCENT_MAX / 100)) + ").  Default is " + String.format("%.2f", ((float) GROW_PERCENT_DEFAULT / 100))
            );
            return;
        }

        if (params[0].startsWith("growth")) {
            try {
                if (params.length == 1) {
                    this.growPercent = GROW_PERCENT_DEFAULT;
                    v.sendMessage(Component.text("Growth percent set to default value: " + String.format("%.2f", ((float) GROW_PERCENT_DEFAULT / 100)) + "%").color(NamedTextColor.AQUA));
                    return;
                }

                float growthValue = Float.parseFloat(params[1]);
                if ((int) (growthValue * 100) >= GROW_PERCENT_MIN && (int) (growthValue * 100) <= GROW_PERCENT_MAX) {
                    v.sendMessage(Component.text("Growth percent set to: " + String.format("%.2f", growthValue) + "%").color(NamedTextColor.AQUA));
                    this.growPercent = (int) growthValue * 100;
                } else {
                    v.sendMessage(Component.text("Growth percent must be a number between " + String.format("%.2f", ((float) GROW_PERCENT_MIN / 100)) + " and " + String.format("%.2f", ((float) GROW_PERCENT_MAX / 100)) + "!").color(NamedTextColor.RED));
                }
                return;
            } catch (NumberFormatException ignored) {
            }
        }

        v.sendMessage(
                Component.empty()
                        .append(Component.text("Invalid parameter! Use ").color(NamedTextColor.RED))
                        .append(Component.newline())
                        .append(Component.text("'/b " + triggerHandle + " info'").color(NamedTextColor.LIGHT_PURPLE))
                        .append(Component.newline())
                        .append(Component.text(" to display valid parameters.").color(NamedTextColor.RED))
                        .append(Component.newline())
        );
        sendPerformerMessage(triggerHandle, v);
    }

    @Override
    public List<String> registerArguments() {
        List<String> arguments = new ArrayList<>();
        arguments.addAll(Lists.newArrayList("growth"));

        arguments.addAll(super.registerArguments());
        return arguments;
    }

    @Override
    public HashMap<String, List<String>> registerArgumentValues() {
        HashMap<String, List<String>> argumentValues = new HashMap<>();

        
        argumentValues.put("growth", Lists.newArrayList("[number]"));

        argumentValues.putAll(super.registerArgumentValues());
        return argumentValues;
    }

    @Override
    public String getPermissionNode() {
        return "voxelsniper.brush.blob";
    }
}
