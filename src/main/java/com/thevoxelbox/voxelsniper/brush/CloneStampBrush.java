package com.thevoxelbox.voxelsniper.brush;

import com.google.common.collect.Lists;
import com.thevoxelbox.voxelsniper.VoxelMessage;
import com.thevoxelbox.voxelsniper.snipe.SnipeData;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.ArrayList;
import java.util.List;

/**
 * The CloneStamp class is used to create a collection of blocks in a cylinder shape according to the selection the player has set.
 *
 * @author Voxel
 */
public class CloneStampBrush extends StampBrush {

    /**
     *
     */
    public CloneStampBrush() {
        this.setName("Clone");
    }

    /**
     * The clone method is used to grab a snapshot of the selected area dictated blockPositionY targetBlock.x y z v.brushSize v.voxelHeight and v.cCen.
     * <p/>
     * x y z -- initial center of the selection v.brushSize -- the radius of the cylinder v.voxelHeight -- the heigth of the cylinder c.cCen -- the offset on
     * the Y axis of the selection ( bottom of the cylinder ) as blockPositionY: Bottom_Y = targetBlock.y + v.cCen;
     *
     * @param v the caller
     */
    private void clone(final SnipeData v) {
        final int brushSize = v.getBrushSize();
        this.clone.clear();
        this.fall.clear();
        this.drop.clear();
        this.solid.clear();
        this.sorted = false;

        int yStartingPoint = this.getTargetBlock().getY() + v.getcCen();
        int yEndPoint = this.getTargetBlock().getY() + v.getVoxelHeight() + v.getcCen();

        if (yStartingPoint < this.getWorld().getMinHeight()) {
            yStartingPoint = this.getWorld().getMinHeight();
            v.sendMessage(Component.text("Warning: off-world start position.").color(NamedTextColor.DARK_PURPLE));
        } else if (yStartingPoint > this.getWorld().getMaxHeight() - 1) {
            yStartingPoint = this.getWorld().getMaxHeight() - 1;
            v.sendMessage(Component.text("Warning: off-world start position.").color(NamedTextColor.DARK_PURPLE));
        }

        if (yEndPoint < this.getWorld().getMinHeight()) {
            yEndPoint = this.getWorld().getMinHeight();
            v.sendMessage(Component.text("Warning: off-world start position.").color(NamedTextColor.DARK_PURPLE));
        } else if (yEndPoint > this.getWorld().getMaxHeight() - 1) {
            yEndPoint = this.getWorld().getMaxHeight() - 1;
            v.sendMessage(Component.text("Warning: off-world start position.").color(NamedTextColor.DARK_PURPLE));
        }

        final double bSquared = Math.pow(brushSize, 2);

        for (int z = yStartingPoint; z < yEndPoint; z++) {
            this.clone.add(new BlockWrapper(this.clampY(this.getTargetBlock().getX(), z, this.getTargetBlock().getZ()), 0, z - yStartingPoint, 0));
            for (int y = 1; y <= brushSize; y++) {
                this.clone.add(new BlockWrapper(this.clampY(this.getTargetBlock().getX(), z, this.getTargetBlock().getZ() + y), 0, z - yStartingPoint, y));
                this.clone.add(new BlockWrapper(this.clampY(this.getTargetBlock().getX(), z, this.getTargetBlock().getZ() - y), 0, z - yStartingPoint, -y));
                this.clone.add(new BlockWrapper(this.clampY(this.getTargetBlock().getX() + y, z, this.getTargetBlock().getZ()), y, z - yStartingPoint, 0));
                this.clone.add(new BlockWrapper(this.clampY(this.getTargetBlock().getX() - y, z, this.getTargetBlock().getZ()), -y, z - yStartingPoint, 0));
            }
            for (int x = 1; x <= brushSize; x++) {
                final double xSquared = Math.pow(x, 2);
                for (int y = 1; y <= brushSize; y++) {
                    if ((xSquared + Math.pow(y, 2)) <= bSquared) {
                        this.clone.add(new BlockWrapper(this.clampY(this.getTargetBlock().getX() + x, z, this.getTargetBlock().getZ() + y), x, z - yStartingPoint, y));
                        this.clone.add(new BlockWrapper(this.clampY(this.getTargetBlock().getX() + x, z, this.getTargetBlock().getZ() - y), x, z - yStartingPoint, -y));
                        this.clone.add(new BlockWrapper(this.clampY(this.getTargetBlock().getX() - x, z, this.getTargetBlock().getZ() + y), -x, z - yStartingPoint, y));
                        this.clone.add(new BlockWrapper(this.clampY(this.getTargetBlock().getX() - x, z, this.getTargetBlock().getZ() - y), -x, z - yStartingPoint, -y));
                    }
                }
            }
        }
        v.sendMessage(Component.text(String.valueOf(this.clone.size())).color(NamedTextColor.GREEN).append(Component.text(" blocks copied sucessfully.").color(NamedTextColor.AQUA)));
    }

    @Override
    protected final void powder(final SnipeData v) {
        this.clone(v);
    }

    @Override
    public final void info(final VoxelMessage vm) {
        vm.brushName(this.getName());
        vm.size();
        vm.height();
        vm.center();
        switch (this.stamp) {
            case DEFAULT -> vm.brushMessage("Default Stamp");
            case NO_AIR -> vm.brushMessage("No-Air Stamp");
            case FILL -> vm.brushMessage("Fill Stamp");
            default -> vm.custom(Component.text("Error while stamping! Report").color(NamedTextColor.DARK_RED));
        }
    }

    @Override
    public final void parseParameters(final String triggerHandle, final String[] params, final com.thevoxelbox.voxelsniper.snipe.SnipeData v) {
        if (params[0].equalsIgnoreCase("info")) {
            v.getVoxelMessage().commandParameters("Clone / Stamp Cylinder Brush Parameters: "
                    , null
                    , "/b " + triggerHandle + " fill  -- Change to Fill mode"
                    , "/b " + triggerHandle + " air  -- Change to No-Air mode"
                    , "/b " + triggerHandle + " default  -- Change to Default mode"
            );
            return;
        }

        if (params[0].equalsIgnoreCase("air")) {
            this.setStamp(StampType.NO_AIR);
            this.reSort();
            v.sendMessage(Component.text("Stamp Mode: No-Air").color(NamedTextColor.AQUA));
            return;
        }

        if (params[0].equalsIgnoreCase("fill")) {
            this.setStamp(StampType.FILL);
            this.reSort();
            v.sendMessage(Component.text("Stamp Mode: Fill").color(NamedTextColor.AQUA));
            return;
        }

        if (params[0].equalsIgnoreCase("default")) {
            this.setStamp(StampType.DEFAULT);
            this.reSort();
            v.sendMessage(Component.text("StampMode: Default").color(NamedTextColor.AQUA));
            return;
        }

        /* TODO: Implement if (params[0].startsWith("centre")) { v.setcCen(Integer.parseInt(params[0].replace("c", ""))); v.sendMessage(ChatColor.BLUE + "Center
            set to " + v.getcCen()); return; }
         */
        v.sendMessage(Component.text("Invalid parameter! Use ").color(NamedTextColor.RED)
                .append(Component.text("'/b " + triggerHandle + " info'").color(NamedTextColor.LIGHT_PURPLE))
                .append(Component.text(" to display valid parameters.").color(NamedTextColor.RED))
        );
    }

    @Override
    public List<String> registerArguments() {
        return new ArrayList<>(Lists.newArrayList("air", "fill", "default"));
    }

    @Override
    public String getPermissionNode() {
        return "voxelsniper.brush.clonestamp";
    }
}
