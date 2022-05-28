package com.thevoxelbox.voxelsniper.brush;

import com.google.common.collect.Lists;
import com.thevoxelbox.voxelsniper.VoxelMessage;
import com.thevoxelbox.voxelsniper.snipe.SnipeData;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.entity.LargeFireball;
import org.bukkit.entity.SmallFireball;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Gavjenks Heavily revamped from ruler brush blockPositionY
 * @author Giltwist
 * @author Monofraps (Merged Meteor brush)
 */
public class CometBrush extends Brush {

    private boolean useBigBalls = false;

    /**
     *
     */
    public CometBrush() {
        this.setName("Comet");
    }

    private void doFireball(final SnipeData v) {
        final Vector targetCoords = new Vector(this.getTargetBlock().getX() + .5 * this.getTargetBlock().getX() / Math.abs(this.getTargetBlock().getX()), this.getTargetBlock().getY() + .5, this.getTargetBlock().getZ() + .5 * this.getTargetBlock().getZ() / Math.abs(this.getTargetBlock().getZ()));
        final Location playerLocation = v.owner().getPlayer().getEyeLocation();
        final Vector slope = targetCoords.subtract(playerLocation.toVector());

        if (useBigBalls) {
            v.owner().getPlayer().launchProjectile(LargeFireball.class).setVelocity(slope.normalize());
        } else {
            v.owner().getPlayer().launchProjectile(SmallFireball.class).setVelocity(slope.normalize());
        }
    }

    @Override
    public final void parseParameters(final String triggerHandle, final String[] params, final SnipeData v) {
        if (params[0].equalsIgnoreCase("info")) {
            v.sendMessage(Component.text("Comet Brush Parameters:").color(NamedTextColor.GOLD)
                    .append(Component.newline())
                    .append(Component.text("/b " + triggerHandle + " [big|small]  -- Sets your ball size").color(NamedTextColor.AQUA)));
        }

        if (params[0].equalsIgnoreCase("big")) {
            useBigBalls = true;
            v.sendMessage(Component.text("Your balls are ").color(NamedTextColor.DARK_RED).append(Component.text("BIG")));
            return;
        }

        if (params[0].equalsIgnoreCase("small")) {
            useBigBalls = false;
            v.sendMessage(Component.text("Your balls are ").color(NamedTextColor.DARK_RED).append(Component.text("small")));
            return;
        }

        v.sendMessage(Component.text("Invalid parameter! Use ").color(NamedTextColor.RED).append(Component.text("'/b " + triggerHandle + " info'").color(NamedTextColor.LIGHT_PURPLE)).append(Component.text(" to display valid parameters.")));
    }

    @Override
    public List<String> registerArguments() {
        List<String> arguments = new ArrayList<>();
        
        arguments.addAll(Lists.newArrayList("big", "small"));

        return arguments;
    }

    @Override
    protected final void arrow(final SnipeData v) {
        this.doFireball(v);
    }

    @Override
    protected final void powder(final SnipeData v) {
        this.doFireball(v);
    }

    @Override
    public final void info(final VoxelMessage vm) {
        vm.brushName(this.getName());
        vm.voxel();
        vm.custom(Component.text("Your balls are ").append(Component.text(useBigBalls ? "BIG" : "small").color(NamedTextColor.DARK_RED)));
    }

    @Override
    public String getPermissionNode() {
        return "voxelsniper.brush.comet";
    }
}
