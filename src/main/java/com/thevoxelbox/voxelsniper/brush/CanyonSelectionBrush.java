package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.VoxelMessage;
import com.thevoxelbox.voxelsniper.snipe.SnipeData;
import com.thevoxelbox.voxelsniper.snipe.Undo;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Chunk;

/**
 * http://www.voxelwiki.com/minecraft/Voxelsniper#The_Canyon_Selection_Brush
 *
 * @author Voxel
 */
public class CanyonSelectionBrush extends CanyonBrush {

    private boolean first = true;
    private int fx;
    private int fz;

    /**
     *
     */
    public CanyonSelectionBrush() {
        this.setName("Canyon Selection");
    }

    private void execute(final SnipeData v) {
        final Chunk chunk = getTargetBlock().getChunk();

        if (this.first) {
            this.fx = chunk.getX();
            this.fz = chunk.getZ();

            v.sendMessage(Component.text("First point selected!").color(NamedTextColor.YELLOW));
            this.first = !this.first;
        } else {
            v.sendMessage(Component.text("Second point selected!").color(NamedTextColor.YELLOW));
            selection(Math.min(fx, chunk.getX()), Math.min(fz, chunk.getZ()), Math.max(fx, chunk.getX()), Math.max(fz, chunk.getZ()), v);

            this.first = !this.first;
        }
    }

    private void selection(final int lowX, final int lowZ, final int highX, final int highZ, final SnipeData v) {
        final Undo undo = new Undo();

        for (int x = lowX; x <= highX; x++) {
            for (int z = lowZ; z <= highZ; z++) {
                canyon(getWorld().getChunkAt(x, z), undo);
            }
        }

        v.owner().storeUndo(undo);
    }

    @Override
    protected final void arrow(final SnipeData v) {
        execute(v);
    }

    @Override
    protected final void powder(final SnipeData v) {
        execute(v);
    }

    @Override
    public final void info(final VoxelMessage vm) {
        vm.brushName(this.getName());
        vm.custom(Component.text("Shift Level set to " + this.getYLevel()).color(NamedTextColor.GREEN));
    }

    @Override
    public String getPermissionNode() {
        return "voxelsniper.brush.canyonselection";
    }
}
