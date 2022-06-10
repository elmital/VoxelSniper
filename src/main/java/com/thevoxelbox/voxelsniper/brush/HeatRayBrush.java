package com.thevoxelbox.voxelsniper.brush;

import com.google.common.collect.Lists;
import com.thevoxelbox.voxelsniper.VoxelMessage;
import com.thevoxelbox.voxelsniper.snipe.SnipeData;
import com.thevoxelbox.voxelsniper.snipe.Undo;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;
import org.bukkit.util.noise.PerlinNoiseGenerator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * @author Gavjenks
 */
public class HeatRayBrush extends Brush {

    private static final double REQUIRED_OBSIDIAN_DENSITY = 0.6;
    private static final double REQUIRED_COBBLE_DENSITY = 0.5;
    private static final double REQUIRED_FIRE_DENSITY = -0.25;
    private static final double REQUIRED_AIR_DENSITY = 0;

    private int octaves = 5;
    private double amplitude = 0.3;
    private double frequency = 1;

    /**
     * Default Constructor.
     */
    public HeatRayBrush() {
        this.setName("Heat Ray");
    }

    /**
     * Heat Ray executer.
     *
     * @param v
     */
    public final void heatRay(final SnipeData v) {
        final PerlinNoiseGenerator generator = new PerlinNoiseGenerator(new Random());

        final Vector targetLocation = this.getTargetBlock().getLocation().toVector();
        final Location currentLocation = new Location(this.getTargetBlock().getWorld(), 0, 0, 0);
        final Undo undo = new Undo();
        Block currentBlock;

        for (int z = v.getBrushSize(); z >= -v.getBrushSize(); z--) {
            for (int x = v.getBrushSize(); x >= -v.getBrushSize(); x--) {
                for (int y = v.getBrushSize(); y >= -v.getBrushSize(); y--) {
                    currentLocation.setX(this.getTargetBlock().getX() + x);
                    currentLocation.setY(this.getTargetBlock().getY() + y);
                    currentLocation.setZ(this.getTargetBlock().getZ() + z);

                    if (currentLocation.toVector().isInSphere(targetLocation, v.getBrushSize())) {
                        currentBlock = currentLocation.getBlock();
                        if (currentBlock.getType() == Material.CHEST) {
                            continue;
                        }

                        if (currentBlock.isLiquid()) {
                            undo.put(currentBlock);
                            currentBlock.setType(Material.AIR);
                            continue;
                        }

                        if (currentBlock.isBurnable()) {
                            undo.put(currentBlock);
                            currentBlock.setType(Material.FIRE);
                            continue;
                        }

                        if (!currentBlock.getType().equals(Material.AIR)) {
                            final double airDensity = generator.noise(currentLocation.getX(), currentLocation.getY(), currentLocation.getZ(), this.octaves, this.frequency, this.amplitude);
                            final double fireDensity = generator.noise(currentLocation.getX(), currentLocation.getY(), currentLocation.getZ(), this.octaves, this.frequency, this.amplitude);
                            final double cobbleDensity = generator.noise(currentLocation.getX(), currentLocation.getY(), currentLocation.getZ(), this.octaves, this.frequency, this.amplitude);
                            final double obsidianDensity = generator.noise(currentLocation.getX(), currentLocation.getY(), currentLocation.getZ(), this.octaves, this.frequency, this.amplitude);

                            if (obsidianDensity >= HeatRayBrush.REQUIRED_OBSIDIAN_DENSITY) {
                                undo.put(currentBlock);
                                if (currentBlock.getType() != Material.OBSIDIAN) {
                                    currentBlock.setType(Material.OBSIDIAN);
                                }
                            } else if (cobbleDensity >= HeatRayBrush.REQUIRED_COBBLE_DENSITY) {
                                undo.put(currentBlock);
                                if (currentBlock.getType() != Material.COBBLESTONE) {
                                    currentBlock.setType(Material.COBBLESTONE);
                                }
                            } else if (fireDensity >= HeatRayBrush.REQUIRED_FIRE_DENSITY) {
                                undo.put(currentBlock);
                                if (currentBlock.getType() != Material.FIRE) {
                                    currentBlock.setType(Material.FIRE);
                                }
                            } else if (airDensity >= HeatRayBrush.REQUIRED_AIR_DENSITY) {
                                undo.put(currentBlock);
                                if (!currentBlock.getType().isAir()) {
                                    currentBlock.setType(Material.AIR);
                                }
                            }
                        }
                    }

                }
            }
        }

        v.owner().storeUndo(undo);
    }

    @Override
    protected final void arrow(final SnipeData v) {
        this.heatRay(v);
    }

    @Override
    protected final void powder(final SnipeData v) {
        this.heatRay(v);
    }

    @Override
    public final void info(final VoxelMessage vm) {
        vm.brushName(this.getName());
        vm.custom(Component.text("Octaves: " + this.octaves).color(NamedTextColor.GREEN)
                .append(Component.newline())
                .append(Component.text("Amplitude: " + this.amplitude))
                .append(Component.newline())
                .append(Component.text("Frequency: " + this.frequency))
        );
        vm.size();
    }

    @Override
    public final void parseParameters(final String triggerHandle, final String[] params, final SnipeData v) {

        if (params[0].equalsIgnoreCase("info")) {
            v.getVoxelMessage().commandParameters("Heat Ray brush Parameters:"
                    , null
                    , "/b " + triggerHandle + " octave [number]  -- Octaves for the noise generator."
                    , "/b " + triggerHandle + " amplitude [number]  -- Amplitude for the noise generator."
                    , "/b " + triggerHandle + " frequency [number]  -- Frequency for the noise generator."
                    , "/b " + triggerHandle + " default  -- Reset to default values."
            );
            return;
        }

        if (params[0].equalsIgnoreCase("default")) {
            this.octaves = 5;
            this.amplitude = 0.3;
            this.frequency = 1;
            v.sendMessage(Component.text("Values were set to default values.").color(NamedTextColor.GOLD));
            return;
        }

        try {
            if (params[0].equalsIgnoreCase("octave")) {
                this.octaves = Integer.parseInt(params[1]);
                v.getVoxelMessage().custom(Component.text("Octave: " + this.octaves).color(NamedTextColor.GREEN));
                return;
            }
            if (params[0].equalsIgnoreCase("amplitude")) {
                this.amplitude = Double.parseDouble(params[1]);
                v.getVoxelMessage().custom(Component.text("Amplitude: " + this.amplitude).color(NamedTextColor.GREEN));
                return;
            }

            if (params[0].equalsIgnoreCase("frequency")) {
                this.frequency = Double.parseDouble(params[1]);
                v.getVoxelMessage().custom(Component.text("Frequency: " + this.frequency).color(NamedTextColor.GREEN));
                return;
            }
        } catch (NumberFormatException ignored) {
        }

        v.getVoxelMessage().invalidUseParameter(triggerHandle);
    }

    @Override
    public List<String> registerArguments() {
        return new ArrayList<>(Lists.newArrayList("octave", "amplitude", "frequency", "default"));
    }

    @Override
    public HashMap<String, List<String>> registerArgumentValues() {
        HashMap<String, List<String>> argumentValues = new HashMap<>();
        
        argumentValues.put("octave", Lists.newArrayList("[number]"));
        argumentValues.put("amplitude", Lists.newArrayList("[number]"));
        argumentValues.put("frequency", Lists.newArrayList("[number]"));
        
        return argumentValues;
    }

    @Override
    public String getPermissionNode() {
        return "voxelsniper.brush.heatray";
    }
}
