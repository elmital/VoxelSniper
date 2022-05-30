package com.thevoxelbox.voxelsniper.brush;

import com.google.common.collect.Lists;
import com.thevoxelbox.voxelsniper.VoxelMessage;
import com.thevoxelbox.voxelsniper.snipe.SnipeData;
import com.thevoxelbox.voxelsniper.snipe.Undo;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * http://www.voxelwiki.com/minecraft/Voxelsniper#CopyPasta_Brush
 *
 * @author giltwist
 */
public class CopyPastaBrush extends Brush {

    private static final int BLOCK_LIMIT = 10000;

    private boolean pasteAir = true; // False = no air, true = air
    private int points = 0; //
    private int numBlocks = 0;
    private int[] firstPoint = new int[3];
    private int[] secondPoint = new int[3];
    private int[] pastePoint = new int[3];
    private int[] minPoint = new int[3];
    private int[] offsetPoint = new int[3];

    private BlockData[] substanceArray;

    private int[] arraySize = new int[3];
    private int pivot = 0; // ccw degrees    

    /**
     *
     */
    public CopyPastaBrush() {
        this.setName("CopyPasta");
    }

    @SuppressWarnings("deprecation")
    private void doCopy(final SnipeData v) {
        for (int i = 0; i < 3; i++) {
            this.arraySize[i] = Math.abs(this.firstPoint[i] - this.secondPoint[i]) + 1;
            this.minPoint[i] = Math.min(this.firstPoint[i], this.secondPoint[i]);
            this.offsetPoint[i] = this.minPoint[i] - this.firstPoint[i]; // will always be negative or zero
        }

        this.numBlocks = (this.arraySize[0]) * (this.arraySize[1]) * (this.arraySize[2]);

        if (this.numBlocks > 0 && this.numBlocks < CopyPastaBrush.BLOCK_LIMIT) {
            this.substanceArray = new BlockData[this.numBlocks];

            for (int i = 0; i < this.arraySize[0]; i++) {
                for (int j = 0; j < this.arraySize[1]; j++) {
                    for (int k = 0; k < this.arraySize[2]; k++) {
                        final int currentPosition = i + this.arraySize[0] * j + this.arraySize[0] * this.arraySize[1] * k;
                        this.substanceArray[currentPosition] = this.getWorld().getBlockAt(this.minPoint[0] + i, this.minPoint[1] + j, this.minPoint[2] + k).getBlockData();
                    }
                }
            }

            v.sendMessage(Component.text(this.numBlocks + " blocks copied.").color(NamedTextColor.AQUA));
        } else {
            v.sendMessage(Component.text("Copy area too big: " + this.numBlocks + "(Limit: " + CopyPastaBrush.BLOCK_LIMIT + ")").color(NamedTextColor.RED));
        }
    }

    @SuppressWarnings("deprecation")
    private void doPasta(final SnipeData v) {
        final Undo undo = new Undo();

        for (int i = 0; i < this.arraySize[0]; i++) {
            for (int j = 0; j < this.arraySize[1]; j++) {
                for (int k = 0; k < this.arraySize[2]; k++) {
                    final int currentPosition = i + this.arraySize[0] * j + this.arraySize[0] * this.arraySize[1] * k;
                    Block block;

                    switch (this.pivot) {
                        case 180:
                            block = this.clampY(this.pastePoint[0] - this.offsetPoint[0] - i, this.pastePoint[1] + this.offsetPoint[1] + j, this.pastePoint[2] - this.offsetPoint[2] - k);
                            break;
                        case 270:
                            block = this.clampY(this.pastePoint[0] + this.offsetPoint[2] + k, this.pastePoint[1] + this.offsetPoint[1] + j, this.pastePoint[2] - this.offsetPoint[0] - i);
                            break;
                        case 90:
                            block = this.clampY(this.pastePoint[0] - this.offsetPoint[2] - k, this.pastePoint[1] + this.offsetPoint[1] + j, this.pastePoint[2] + this.offsetPoint[0] + i);
                            break;
                        default: // assume no rotation
                            block = this.clampY(this.pastePoint[0] + this.offsetPoint[0] + i, this.pastePoint[1] + this.offsetPoint[1] + j, this.pastePoint[2] + this.offsetPoint[2] + k);
                            break;
                    }

                    if (!(this.substanceArray[currentPosition].getMaterial().isAir() && !this.pasteAir)) {
                        if (block.getType() != this.substanceArray[currentPosition].getMaterial() || !block.getBlockData().matches(this.substanceArray[currentPosition])) {
                            undo.put(block);
                        }
                        block.setBlockData(this.substanceArray[currentPosition], true);
                    }
                }
            }
        }
        v.sendMessage(Component.text(this.numBlocks + " blocks pasted.").color(NamedTextColor.AQUA));

        v.owner().storeUndo(undo);
    }

    @Override
    protected final void arrow(final com.thevoxelbox.voxelsniper.snipe.SnipeData v) {
        switch (this.points) {
            case 0 -> {
                this.firstPoint[0] = this.getTargetBlock().getX();
                this.firstPoint[1] = this.getTargetBlock().getY();
                this.firstPoint[2] = this.getTargetBlock().getZ();
                v.sendMessage(Component.text("First point").color(NamedTextColor.GRAY));
                this.points = 1;
            }
            case 1 -> {
                this.secondPoint[0] = this.getTargetBlock().getX();
                this.secondPoint[1] = this.getTargetBlock().getY();
                this.secondPoint[2] = this.getTargetBlock().getZ();
                v.sendMessage(Component.text("Second point").color(NamedTextColor.GRAY));
                this.points = 2;
            }
            default -> {
                this.firstPoint = new int[3];
                this.secondPoint = new int[3];
                this.numBlocks = 0;
                this.substanceArray = new BlockData[1];
                this.points = 0;
                v.sendMessage(Component.text("Points cleared.").color(NamedTextColor.GRAY));
            }
        }
    }

    @Override
    protected final void powder(final com.thevoxelbox.voxelsniper.snipe.SnipeData v) {
        if (this.points == 2) {
            if (this.numBlocks == 0) {
                this.doCopy(v);
            } else if (this.numBlocks > 0 && this.numBlocks < CopyPastaBrush.BLOCK_LIMIT) {
                this.pastePoint[0] = this.getTargetBlock().getX();
                this.pastePoint[1] = this.getTargetBlock().getY();
                this.pastePoint[2] = this.getTargetBlock().getZ();
                this.doPasta(v);
            } else {
                v.sendMessage(Component.text("Error").color(NamedTextColor.RED));
            }
        } else {
            v.sendMessage(Component.text("You must select exactly two points.").color(NamedTextColor.RED));
        }
    }

    @Override
    public final void info(final VoxelMessage vm) {
        vm.brushName(this.getName());
        vm.custom(
                Component.text("Paste air: " + this.pasteAir).color(NamedTextColor.GOLD)
                        .append(Component.newline())
                        .append(Component.text("Pivot angle: " + this.pivot))
        );
    }

    @Override
    public final void parseParameters(final String triggerHandle, final String[] params, final com.thevoxelbox.voxelsniper.snipe.SnipeData v) {
        if (params[0].equalsIgnoreCase("info")) {
            v.getVoxelMessage().commandParameters("CopyPasta Brush Parameters:"
                    , null
                    , "/b " + triggerHandle + " air  -- Toggle include air during paste (default: true)"
                    , "/b " + triggerHandle + " rotate [number]  -- Set rotation pivot (default: 0)"
            );
            return;
        }

        if (params[0].equalsIgnoreCase("air")) {
            this.pasteAir = !this.pasteAir;

            v.sendMessage(Component.text("Air included in paste: " + this.pasteAir).color(NamedTextColor.GOLD));
            return;
        }

        if (params[0].equalsIgnoreCase("rotate")) {
            if (params[1].equalsIgnoreCase("90") || params[1].equalsIgnoreCase("180") || params[1].equalsIgnoreCase("270") || params[1].equalsIgnoreCase("0")) {
                this.pivot = Integer.parseInt(params[1]);
                v.sendMessage(Component.text("Pivot angle: " + this.pivot + " degrees").color(NamedTextColor.GOLD));
                return;
            }
        }

        v.sendMessage(Component.empty()
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
        
        arguments.addAll(Lists.newArrayList("rotate", "air"));
        
        return arguments;
    }

    @Override
    public HashMap<String, List<String>> registerArgumentValues() {
        HashMap<String, List<String>> argumentValues = new HashMap<>();

        
        argumentValues.put("rotate", Lists.newArrayList("0", "90", "180", "270"));

        return argumentValues;
    }

    @Override
    public String getPermissionNode() {
        return "voxelsniper.brush.copypasta";
    }
}
