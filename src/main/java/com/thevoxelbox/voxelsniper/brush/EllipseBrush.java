package com.thevoxelbox.voxelsniper.brush;

import com.google.common.collect.Lists;
import com.thevoxelbox.voxelsniper.VoxelMessage;
import com.thevoxelbox.voxelsniper.brush.perform.PerformerBrush;
import com.thevoxelbox.voxelsniper.snipe.SnipeData;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * http://www.voxelwiki.com/minecraft/Voxelsniper#Ellipse_Brush
 *
 * @author psanker
 */
public class EllipseBrush extends PerformerBrush {

    private static final double TWO_PI = (2 * Math.PI);
    private static final int SCL_MIN = 1;
    private static final int SCL_MAX = 9999;
    private static final int SCL_DEFAULT = 10;
    private static final int STEPS_MIN = 1;
    private static final int STEPS_MAX = 2000;
    private static final int STEPS_DEFAULT = 200;
    private int xscl;
    private int yscl;
    private int steps;
    private double stepSize;
    private boolean fill;

    /**
     *
     */
    public EllipseBrush() {
        this.setName("Ellipse");
    }

    private void ellipse(final SnipeData v, Block targetBlock) {
        try {
            for (double steps = 0; (steps <= TWO_PI); steps += stepSize) {
                final int x = (int) Math.round(this.xscl * Math.cos(steps));
                final int y = (int) Math.round(this.yscl * Math.sin(steps));

                switch (getTargetBlock().getFace(this.getLastBlock())) {
                    case NORTH:
                    case SOUTH:
                        currentPerformer.perform(targetBlock.getRelative(0, x, y));
                        break;
                    case EAST:
                    case WEST:
                        currentPerformer.perform(targetBlock.getRelative(x, y, 0));
                        break;
                    case UP:
                    case DOWN:
                        currentPerformer.perform(targetBlock.getRelative(x, 0, y));
                    default:
                        break;
                }

                if (steps >= TWO_PI) {
                    break;
                }
            }
        } catch (final Exception exception) {
            v.sendMessage(Component.text("Invalid target.").color(NamedTextColor.RED));
        }

        v.owner().storeUndo(this.currentPerformer.getUndo());
    }

    private void ellipsefill(final SnipeData v, Block targetBlock) {
        int ix = this.xscl;
        int iy = this.yscl;

        currentPerformer.perform(targetBlock);

        try {
            if (ix >= iy) { // Need this unless you want weird holes
                for (iy = this.yscl; iy > 0; iy--) { //TODO potentially broken with new world height
                    for (double steps = 0; (steps <= TWO_PI); steps += stepSize) {
                        final int x = (int) Math.round(ix * Math.cos(steps));
                        final int y = (int) Math.round(iy * Math.sin(steps));

                        switch (getTargetBlock().getFace(this.getLastBlock())) {
                            case NORTH:
                            case SOUTH:
                                currentPerformer.perform(targetBlock.getRelative(0, x, y));
                                break;
                            case EAST:
                            case WEST:
                                currentPerformer.perform(targetBlock.getRelative(x, y, 0));
                                break;
                            case UP:
                            case DOWN:
                                currentPerformer.perform(targetBlock.getRelative(x, 0, y));
                            default:
                                break;
                        }

                        if (steps >= TWO_PI) {
                            break;
                        }
                    }
                    ix--;
                }
            } else {
                for (ix = this.xscl; ix > 0; ix--) { //TODO potentially broken with new world height
                    for (double steps = 0; (steps <= TWO_PI); steps += stepSize) {
                        final int x = (int) Math.round(ix * Math.cos(steps));
                        final int y = (int) Math.round(iy * Math.sin(steps));

                        switch (getTargetBlock().getFace(this.getLastBlock())) {
                            case NORTH:
                            case SOUTH:
                                currentPerformer.perform(targetBlock.getRelative(0, x, y));
                                break;
                            case EAST:
                            case WEST:
                                currentPerformer.perform(targetBlock.getRelative(x, y, 0));
                                break;
                            case UP:
                            case DOWN:
                                currentPerformer.perform(targetBlock.getRelative(x, 0, y));
                            default:
                                break;
                        }

                        if (steps >= TWO_PI) {
                            break;
                        }
                    }
                    iy--;
                }
            }
        } catch (final Exception exception) {
            v.sendMessage(Component.text("Invalid target.").color(NamedTextColor.RED));
        }

        v.owner().storeUndo(this.currentPerformer.getUndo());
    }

    private void execute(final SnipeData v, Block targetBlock) {
        this.stepSize = (TWO_PI / this.steps);

        if (this.fill) {
            this.ellipsefill(v, targetBlock);
        } else {
            this.ellipse(v, targetBlock);
        }
    }

    @Override
    protected final void arrow(final SnipeData v) {
        this.execute(v, this.getTargetBlock());
    }

    @Override
    protected final void powder(final SnipeData v) {
        this.execute(v, this.getLastBlock());
    }

    @Override
    public final void info(final VoxelMessage vm) {
        if (this.xscl < SCL_MIN || this.xscl > SCL_MAX) {
            this.xscl = SCL_DEFAULT;
        }

        if (this.yscl < SCL_MIN || this.yscl > SCL_MAX) {
            this.yscl = SCL_DEFAULT;
        }

        if (this.steps < STEPS_MIN || this.steps > STEPS_MAX) {
            this.steps = STEPS_DEFAULT;
        }

        vm.brushName(this.getName());
        vm.custom(Component.empty()
                .append(Component.text("X-size set to: ").color(NamedTextColor.AQUA).append(Component.text(this.xscl).color(NamedTextColor.DARK_AQUA).append(Component.newline())))
                .append(Component.text("Y-size set to: ").color(NamedTextColor.AQUA).append(Component.text(this.yscl).color(NamedTextColor.DARK_AQUA).append(Component.newline())))
                .append(Component.text("Render step number set to: ").color(NamedTextColor.AQUA).append(Component.text(this.steps).color(NamedTextColor.DARK_AQUA)))
        );
        if (this.fill) {
            vm.custom(Component.text("Fill mode is enabled").color(NamedTextColor.AQUA));
        } else {
            vm.custom(Component.text("Fill mode is disabled").color(NamedTextColor.AQUA));
        }
    }

    @Override
    public final void parseParameters(final String triggerHandle, final String[] params, final SnipeData v) {
        if (params[0].equalsIgnoreCase("info")) {
            v.getVoxelMessage().commandParameters("Ellipse Brush Parameters: "
                    , null
                    , "/b " + triggerHandle + " x [number]  -- Set X size modifier"
                    , "/b " + triggerHandle + " y [number]  -- Set Y size modifier"
                    , "/b " + triggerHandle + " t [number]  -- Set time steps"
                    , "/b " + triggerHandle + "fill  -- Toggles fill mode"
            );
            return;
        }

        if (params[0].equalsIgnoreCase("fill")) {
            this.fill = !this.fill;
            v.sendMessage(Component.text("Fill mode is now " + (this.fill ? "enabled" : "disabled")).color(NamedTextColor.AQUA));
            return;
        }

        try {
            if (params[0].startsWith("x")) {
                int xValue = Integer.parseInt(params[1]);

                if (xValue < SCL_MIN || xValue > SCL_MAX) {
                    v.sendMessage(Component.text("Invalid X scale, must be between " + SCL_MIN + " - " + SCL_MAX).color(NamedTextColor.RED));
                    return;
                }

                this.xscl = xValue;
                v.sendMessage(Component.text("X-scale modifier set to: " + this.xscl).color(NamedTextColor.AQUA));
                return;
            }

            if (params[0].startsWith("y")) {
                int yValue = Integer.parseInt(params[1]);

                if (yValue < SCL_MIN || yValue > SCL_MAX) {
                    v.sendMessage(Component.text("Invalid Y scale, must be between " + SCL_MIN + " - " + SCL_MAX).color(NamedTextColor.RED));
                    return;
                }

                this.yscl = yValue;
                v.sendMessage(Component.text("Y-scale modifier set to: " + this.yscl).color(NamedTextColor.AQUA));
                return;
            }

            if (params[0].startsWith("t")) {
                int stepValue = Integer.parseInt(params[1]);

                if (stepValue < STEPS_MIN || stepValue > STEPS_MAX) {
                    v.sendMessage(Component.text("Invalid step amount, must be between " + STEPS_MIN + " - " + STEPS_MAX).color(NamedTextColor.RED));
                    return;
                }

                this.steps = stepValue;
                v.sendMessage(Component.text("Render step number set to: " + this.steps).color(NamedTextColor.AQUA));
                return;
            }
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
        }

        v.sendMessage(Component.text("Invalid parameter! Use ").color(NamedTextColor.RED)
                .append(Component.text("'/b " + triggerHandle + " info'").color(NamedTextColor.LIGHT_PURPLE))
                .append(Component.text(" to display valid parameters."))
        );
        sendPerformerMessage(triggerHandle, v);
    }

    @Override
    public List<String> registerArguments() {
        List<String> arguments = new ArrayList<>();
        arguments.addAll(Lists.newArrayList("x", "y", "t", "fill"));

        arguments.addAll(super.registerArguments());
        return arguments;
    }

    @Override
    public HashMap<String, List<String>> registerArgumentValues() {
        HashMap<String, List<String>> argumentValues = new HashMap<>();
        
        argumentValues.put("x", Lists.newArrayList("[number]"));
        argumentValues.put("y", Lists.newArrayList("[number]"));
        argumentValues.put("t", Lists.newArrayList("[number]"));

        argumentValues.putAll(super.registerArgumentValues());
        return argumentValues;
    }

    @Override
    public String getPermissionNode() {
        return "voxelsniper.brush.ellipse";
    }
}
