package com.thevoxelbox.voxelsniper.util;

import com.thevoxelbox.voxelsniper.snipe.Undo;
import org.bukkit.BlockChangeDelegate;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.jetbrains.annotations.NotNull;

/**
 *
 */
public class UndoDelegate implements BlockChangeDelegate {

    private final World targetWorld;
    private Undo currentUndo;

    public Undo getUndo() {
        final Undo pastUndo = currentUndo;
        currentUndo = new Undo();
        return pastUndo;
    }

    public UndoDelegate(World targetWorld) {
        this.targetWorld = targetWorld;
        this.currentUndo = new Undo();
    }

    @Override
    public boolean setBlockData(int x, int y, int z, @NotNull BlockData blockData) {
        this.currentUndo.put(targetWorld.getBlockAt(x, y, z));
        this.targetWorld.getBlockAt(x, y, z).setBlockData(blockData, false);
        return true;
    }

    @Override
    public @NotNull BlockData getBlockData(int x, int y, int z) {
        return this.targetWorld.getBlockAt(x, y, z).getBlockData();
    }

    public boolean setBlock(Block b) {
        this.currentUndo.put(this.targetWorld.getBlockAt(b.getLocation()));
        this.targetWorld.getBlockAt(b.getLocation()).setBlockData(b.getBlockData(), true);
        return true;
    }

    @Override
    public int getHeight() {
        return this.targetWorld.getMaxHeight();
    }

    @Override
    public boolean isEmpty(int x, int y, int z) {
        return this.targetWorld.getBlockAt(x, y, z).isEmpty();
    }
}
