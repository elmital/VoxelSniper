package com.thevoxelbox.voxelsniper.command;

import com.thevoxelbox.voxelsniper.VoxelProfileManager;
import com.thevoxelbox.voxelsniper.snipe.SnipeData;
import com.thevoxelbox.voxelsniper.snipe.Sniper;
import com.thevoxelbox.voxelsniper.util.BlockHelper;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class VoxelInkReplaceCommand extends VoxelCommand {

    public VoxelInkReplaceCommand() {
        super("VoxelInkReplace");
        setIdentifier("vir");
        setPermission("voxelsniper.sniper");
    }
    
    @Override
    public boolean doCommand(Player player, String[] args) {
        Sniper sniper = VoxelProfileManager.getInstance().getSniperForPlayer(player);
        SnipeData snipeData = sniper.getSnipeData(sniper.getCurrentToolId());

        // Default command
        // Command: /vir info, /vir help
        if (args.length == 1 && (args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("info"))) {
            player.sendMessage(
                    Component.newline()
                            .append(Component.text(getName() + " Command Syntax:").color(NamedTextColor.DARK_AQUA))
                            .append(Component.newline())
                            .append(Component.text("/" + getActiveAlias()).color(NamedTextColor.GOLD))
                            .append(Component.newline())
                            .append(Component.text("    Copy data value of the block you are looking at into the active replace material.").color(NamedTextColor.YELLOW))
                            .append(Component.newline())
                            .append(Component.text("/" + getActiveAlias() + " [dataValue]").color(NamedTextColor.GOLD))
                            .append(Component.newline())
                            .append(Component.text("    Set specified data value to the active replace material.").color(NamedTextColor.YELLOW))
                            .append(Component.newline())
                            .append(Component.text("    Example: /" + getActiveAlias() + "rotation=3 waterlogged=false").color(NamedTextColor.DARK_AQUA))
            );
            return true;
        }

        // Command: /vir
        if (args.length == 0) {
            Block selectedBlock = new BlockHelper(player, player.getWorld()).getTargetBlock();
            if (selectedBlock != null) {
                if (selectedBlock.getType() != snipeData.getReplaceMaterial()) {
                    player.sendMessage(Component.text("That block is not the same as your active replace material.").color(NamedTextColor.RED));
                } else {
                    snipeData.setReplaceSubstance(selectedBlock.getBlockData());
                    snipeData.getVoxelMessage().replaceData();
                }
            } else {
                player.sendMessage(Component.text("No block to imitate replace material data values. No changes were made.").color(NamedTextColor.GOLD));
            }
            return true;
        }

        // Command: /vir [data]
        if (args.length >= 1) {
            try {
                BlockData newData = snipeData.getReplaceMaterial().createBlockData("[" + Arrays.stream(args).collect(Collectors.joining(",")) + "]");
                BlockData activeData = snipeData.getReplaceSubstance();

                snipeData.setReplaceSubstance(activeData.merge(newData));
                snipeData.getVoxelMessage().replaceData();
            } catch (IllegalArgumentException e) {
                player.sendMessage(Component.text("The data value(s) cannot be imitated to the active voxel material.").color(NamedTextColor.RED));
            }
            return true;
        }

        return false;
    }

    @Override
    public List<String> doSuggestion(Player player, String[] args) {
        // TODO: Very hacky parsing, find a more elegant solution.
        Sniper sniper = VoxelProfileManager.getInstance().getSniperForPlayer(player);
        SnipeData snipeData = sniper.getSnipeData(sniper.getCurrentToolId());

        String[] a = snipeData.getReplaceSubstance().getAsString().split("\\[");

        if (a.length == 2) {
            List<String> possibleDataValues = new ArrayList<>();
            
            String values = a[1].replace("]", "");

            for (String value : values.split(",")) {
                possibleDataValues.add(value.split("=")[0]);
            }
            return possibleDataValues;
        }

        return new ArrayList<>();
    }
}
