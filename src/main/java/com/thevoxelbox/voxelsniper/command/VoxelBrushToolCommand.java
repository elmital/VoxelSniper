package com.thevoxelbox.voxelsniper.command;

import com.google.common.collect.Lists;
import com.thevoxelbox.voxelsniper.VoxelProfileManager;
import com.thevoxelbox.voxelsniper.snipe.SnipeAction;
import com.thevoxelbox.voxelsniper.snipe.Sniper;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class VoxelBrushToolCommand extends VoxelCommand {

    // TODO: Config file saving of custom brush tools
    public VoxelBrushToolCommand() {
        super("Voxel Brush Tool");
        setIdentifier("btool");
        setPermission("voxelsniper.sniper");
    }

    @Override
    public boolean doCommand(Player player, String[] args) {
        Sniper sniper = VoxelProfileManager.getInstance().getSniperForPlayer(player);

        // Default command
        // Command: /btool, /btool help, /btool info
        if (args.length == 0 || (args.length == 1 && (args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("info")))) {
            player.sendMessage(
                    Component.empty()
                            .append(Component.text(getName() + " Command Syntax:").color(NamedTextColor.DARK_AQUA))
                            .append(Component.newline())
                            .append(Component.text("/" + getActiveAlias() + " assign <arrow | powder> <label>").color(NamedTextColor.GOLD))
                            .append(Component.newline())
                            .append(Component.text("    Assign an action to your currently held item with the specified label.").color(NamedTextColor.YELLOW))
                            .append(Component.newline())
                            .append(Component.text("/" + getActiveAlias() + " remove").color(NamedTextColor.GOLD))
                            .append(Component.newline())
                            .append(Component.text("    Removes the action that is bound to the currently held item.").color(NamedTextColor.YELLOW))
            );
            return true;
        }

        // Command: /btool assign <arrow/powder> <label>
        if (args[0].equalsIgnoreCase("assign")) {
            SnipeAction action;
            if (args.length >= 3 && args[2] != null && !args[2].isEmpty()) {
                if (args[1].equalsIgnoreCase("arrow")) {
                    action = SnipeAction.ARROW;
                } else if (args[1].equalsIgnoreCase("powder")) {
                    action = SnipeAction.GUNPOWDER;
                } else {
                    return false;
                }

                Material itemInHand = player.getInventory().getItemInMainHand().getType();

                if (itemInHand.isAir()) {
                    player.sendMessage(Component.text("Please hold an item to assign a tool action to.").color(NamedTextColor.RED));
                    return true;
                }
                
                if (itemInHand.isBlock()) {
                    player.sendMessage(Component.text("You can't assign an action to an item that can be placed as a block!").color(NamedTextColor.RED));
                    return true;
                }

                String toolLabel = String.join(" ", Arrays.copyOfRange(args, 2, args.length));

                if (sniper.setTool(toolLabel, action, itemInHand)) {
                    player.sendMessage(Component.text(itemInHand.name() + " has been assigned to '" + toolLabel + "' as action " + action.name() + ".").color(NamedTextColor.GOLD));
                } else {
                    player.sendMessage(Component.text("Couldn't assign action to that tool.").color(NamedTextColor.RED));
                }

            } else {
                player.sendMessage(Component.text("\"Please assign your own label to the tool to identify it.\"").color(NamedTextColor.DARK_AQUA));
            }
            return true;
        }

        
        // Command: /btool remove
        if (args[0].equalsIgnoreCase("remove")) {
            Material itemInHand = player.getInventory().getItemInMainHand().getType();

            if (itemInHand.isAir()) {
                player.sendMessage(Component.text("Please hold an item to unassign a tool action.").color(NamedTextColor.RED));
                return true;
            }

            if (sniper.getCurrentToolId() == null) {
                player.sendMessage(Component.text("You are not allowed to unassign the default tool!").color(NamedTextColor.RED));
                return true;
            }

            sniper.removeTool(sniper.getCurrentToolId(), itemInHand);
            player.sendMessage(Component.text(itemInHand.name() + " has been unassigned as a tool.").color(NamedTextColor.GOLD));
            return true;
        }

        return false;
    }

    @Override
    public List<String> doSuggestion(Player player, String[] args) {
        if (args.length == 1) {
            return Lists.newArrayList("assign", "remove");
        }

        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("assign")) {
                return Lists.newArrayList("arrow", "powder");
            }
        }

        if (args.length == 3) {
            if (args[0].equalsIgnoreCase("assign"))  {
                return Lists.newArrayList("[label]");
            }
        }

        return new ArrayList<>();
    }

}
