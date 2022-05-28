package com.thevoxelbox.voxelsniper.brush;

import com.google.common.collect.Lists;
import com.thevoxelbox.voxelsniper.VoxelMessage;
import com.thevoxelbox.voxelsniper.snipe.SnipeData;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author Monofraps
 */
@SuppressWarnings("deprecation")
public abstract class BlendBrushBase extends Brush {

    protected boolean excludeAir = true;
    protected boolean excludeWater = true;

    /**
     * @param v
     */
    protected abstract void blend(final SnipeData v);

    @Override
    protected final void arrow(final SnipeData v) {
        this.excludeAir = false;
        this.blend(v);
    }

    @Override
    protected final void powder(final SnipeData v) {
        this.excludeAir = true;
        this.blend(v);
    }

    @Override
    public final void info(final VoxelMessage vm) {
        vm.brushName(this.getName());
        vm.size();
        vm.voxel();
        vm.custom(Component.text("Water Mode: " + (this.excludeWater ? "exclude" : "include")).color(NamedTextColor.BLUE));
    }

    @Override
    public void parseParameters(final String triggerHandle, final String[] params, final SnipeData v) {
        if (params[0].equalsIgnoreCase("water")) {
            this.excludeWater = !this.excludeWater;
            v.sendMessage(Component.text("Water Mode: " + (this.excludeWater ? "exclude" : "include")).color(NamedTextColor.AQUA));
            return;
        }

        v.sendMessage(
                Component.empty()
                        .append(Component.text("Invalid parameter! Use ").color(NamedTextColor.RED))
                        .append(Component.newline())
                        .append(Component.text("'/b " + triggerHandle + " info'").color(NamedTextColor.LIGHT_PURPLE))
                        .append(Component.newline())
                        .append(Component.text(" to display valid parameters.").color(NamedTextColor.RED))
        );
    }

    @Override
    public List<String> registerArguments() {
        List<String> arguments = new ArrayList<>();
        
        arguments.addAll(Lists.newArrayList("water"));

        return arguments;
    }

    @Override
    public HashMap<String, List<String>> registerArgumentValues() {
        HashMap<String, List<String>> argumentValues = new HashMap<>();

        
        argumentValues.put("water", Lists.newArrayList("true", "false"));

        return argumentValues;
    }

    /**
     * @return
     */
    protected final boolean isExcludeAir() {
        return excludeAir;
    }

    /**
     * @param excludeAir
     */
    protected final void setExcludeAir(boolean excludeAir) {
        this.excludeAir = excludeAir;
    }

    /**
     * @return
     */
    protected final boolean isExcludeWater() {
        return excludeWater;
    }

    /**
     * @param excludeWater
     */
    protected final void setExcludeWater(boolean excludeWater) {
        this.excludeWater = excludeWater;
    }
}
