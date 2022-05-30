package com.thevoxelbox.voxelsniper.command;

import com.google.common.collect.Lists;
import com.thevoxelbox.voxelsniper.VoxelProfileManager;
import com.thevoxelbox.voxelsniper.snipe.SnipeData;
import com.thevoxelbox.voxelsniper.snipe.Sniper;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class VoxelSniperCommand extends VoxelCommand {

    public VoxelSniperCommand() {

        super("VoxelSniper");
        setIdentifier("sniper");
        setPermission("voxelsniper.sniper");
    }

    @Override
    public boolean doCommand(Player player, String[] args) {
        Sniper sniper = VoxelProfileManager.getInstance().getSniperForPlayer(player);
        SnipeData snipeData = sniper.getSnipeData(sniper.getCurrentToolId());

        // Default command
        // Command: /sniper, /sniper help, /sniper info
        if ((args.length == 1 && (args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("info")))) {
            player.sendMessage(Component.empty()
                            .append(Component.text(getName() + " Command Syntax:").color(NamedTextColor.DARK_AQUA))
                            .append(Component.newline())
                            .append(Component.text("/" + getActiveAlias() + " <enable | disable>").color(NamedTextColor.GOLD))
                            .append(Component.newline())
                            .append(Component.text("    Activates or deactivates VoxelSniper for yourself.").color(NamedTextColor.YELLOW))
                            .append(Component.newline())
                            .append(Component.text("/" + getActiveAlias() + " range").color(NamedTextColor.GOLD))
                            .append(Component.newline())
                            .append(Component.text("    Toggles whether range limit is enabled or not.").color(NamedTextColor.YELLOW))
                            .append(Component.newline())
                            .append(Component.text("/" + getActiveAlias() + " range [number]").color(NamedTextColor.GOLD))
                            .append(Component.newline())
                            .append(Component.text("    Sets and enables the range limitation.").color(NamedTextColor.YELLOW))
            );
            // TODO: List all bound tools
            // player.sendMessage(ChatColor.GOLD + "/" + getActiveAlias() + " list"); 
            // player.sendMessage(ChatColor.YELLOW + "    Lists all items that you have bound an action to.");
            return true;
        }

        if (args.length == 0) {
            player.sendMessage(Component.text("VoxelSniper - Current Brush Settings:").color(NamedTextColor.DARK_RED));
            sniper.displayInfo();
            return true;
        }

        if (args.length >= 1) {
            if (args[0].equalsIgnoreCase("range")) {
                // Command: /sniper range
                if (args.length == 1) {
                    snipeData.setRanged(!snipeData.isRanged());
                    snipeData.getVoxelMessage().toggleRange();
                }

                // Command: /sniper range [number]
                if (args.length == 2) {
                    try {
                        int range = Integer.parseInt(args[1]);
                        if (range < 0) {
                            player.sendMessage(Component.text("Negative values are not allowed."));
                        } else {
                            snipeData.setRange(range);
                            snipeData.setRanged(true);
                            snipeData.getVoxelMessage().toggleRange();
                        }
                    } catch (NumberFormatException exception) {
                        player.sendMessage(Component.text("Can't parse number."));
                    }
                }
                return true;
            } else if (args[0].equalsIgnoreCase("enable")) {
                sniper.setEnabled(true);
                player.sendMessage(Component.text("VoxelSniper is now enabled for you."));
                return true;
            } else if (args[0].equalsIgnoreCase("disable")) {
                sniper.setEnabled(false);
                player.sendMessage(Component.text("VoxelSniper is now disabled for you."));
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    @Override
    public List<String> doSuggestion(Player player, String[] args) {
        if (args.length == 0) {
            return Lists.newArrayList("enable", "disable", "range");
        }

        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("range")) {
                return Lists.newArrayList("[number]");
            }
        }

        return new ArrayList<>();
    }
}
