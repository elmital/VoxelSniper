package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.VoxelMessage;
import com.thevoxelbox.voxelsniper.brush.perform.PerformerBrush;
import com.thevoxelbox.voxelsniper.snipe.SnipeData;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.NumberConversions;
import org.bukkit.util.Vector;

/**
 * @author Gavjenks
 * @author giltwist
 * @author MikeMatrix
 */
public class LineBrush extends PerformerBrush {

    private static final Vector HALF_BLOCK_OFFSET = new Vector(0.5, 0.5, 0.5);
    private Vector originCoords = null;
    private Vector targetCoords = new Vector();
    private World targetWorld;

    /**
     *
     */
    public LineBrush() {
        this.setName("Line");
    }

    @Override
    public final void info(final VoxelMessage vm) {
        vm.brushName(this.getName());
    }

    @Override
    public final void parseParameters(final String triggerHandle, final String[] params, final SnipeData v) {
        if (params[0].equalsIgnoreCase("info")) {
            v.getVoxelMessage().commandParameters(null
                    , Component.text("Instructions: Right click first point with the arrow. Right click with powder to draw a line to set the second point.").color(NamedTextColor.BLUE)
                    );
            return;
        }

        v.getVoxelMessage().invalidUseParameter(triggerHandle);
        sendPerformerMessage(triggerHandle, v);
    }

    private void linePowder(final SnipeData v) {
        final Vector originClone = this.originCoords.clone().add(LineBrush.HALF_BLOCK_OFFSET);
        final Vector targetClone = this.targetCoords.clone().add(LineBrush.HALF_BLOCK_OFFSET);

        final Vector direction = targetClone.clone().subtract(originClone);
        final double length = this.targetCoords.distance(this.originCoords);

        if (length == 0) {
            this.currentPerformer.perform(this.targetCoords.toLocation(this.targetWorld).getBlock());
        } else {
            for (final BlockIterator blockIterator = new BlockIterator(this.targetWorld, originClone, direction, 0, NumberConversions.round(length)); blockIterator.hasNext();) {
                final Block currentBlock = blockIterator.next();
                this.currentPerformer.perform(currentBlock);
            }
        }

        v.owner().storeUndo(this.currentPerformer.getUndo());
    }

    @Override
    protected final void arrow(final SnipeData v) {
        this.originCoords = this.getTargetBlock().getLocation().toVector();
        this.targetWorld = this.getTargetBlock().getWorld();
        v.owner().getPlayer().sendMessage(Component.text("First point selected.").color(NamedTextColor.DARK_PURPLE));
    }

    @Override
    protected final void powder(final SnipeData v) {
        if (this.originCoords == null || !this.getTargetBlock().getWorld().equals(this.targetWorld)) {
            v.owner().getPlayer().sendMessage(Component.text("Warning: You did not select a first coordinate with the arrow").color(NamedTextColor.RED));
        } else {
            this.targetCoords = this.getTargetBlock().getLocation().toVector();
            this.linePowder(v);
        }
    }

    @Override
    public String getPermissionNode() {
        return "voxelsniper.brush.line";
    }
}
