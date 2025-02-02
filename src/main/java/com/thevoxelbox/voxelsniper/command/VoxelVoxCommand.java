package com.thevoxelbox.voxelsniper.command;

import com.google.common.collect.Lists;
import com.thevoxelbox.voxelsniper.util.BlockHelper;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ervinnnc
 */
public class VoxelVoxCommand extends VoxelCommand {

    public VoxelVoxCommand() {
        super("Vox Utility");
        setIdentifier("vox");
        setPermission("voxelsniper.sniper");
    }

    @Override
    public boolean doCommand(Player player, String[] args) {
        // Command: /paint
        if (getActiveAlias().equalsIgnoreCase("paint")) {
            if (args.length == 0) {
                BlockHelper.paint(player, true, false, 0);
                return true;
            }

            if (args.length == 1) {
                try {
                    BlockHelper.paint(player, false, false, Integer.parseInt(args[0]));
                } catch (NumberFormatException e) {
                    player.sendMessage(Component.text("Invalid syntax. Command: /paint <number>").color(NamedTextColor.RED));
                }
                return true;
            }
        }

        // Command: /vchunk
        if (getActiveAlias().equalsIgnoreCase("vchunk")) {
            player.getWorld().refreshChunk(player.getLocation().getBlockX(), player.getLocation().getBlockZ());
            player.sendMessage(Component.text("Refreshed the chunk that you are standing in."));
            return true;
        }

        // Command: /goto
        if (getActiveAlias().equalsIgnoreCase("goto")) {
            try {
                final int x = Integer.parseInt(args[0]);
                final int z = Integer.parseInt(args[1]);

                player.teleport(new Location(player.getWorld(), x, player.getWorld().getHighestBlockYAt(x, z), z));
                player.sendMessage(Component.text("Whoosh!").color(NamedTextColor.DARK_PURPLE));
            } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                player.sendMessage(Component.text("Invalid syntax. Command:").color(NamedTextColor.RED).append(Component.text("/goto <x> <z>").color(NamedTextColor.GOLD)));
            }
            return true;
        }

        // Default command
        // Command: /vox, /vox help, /vox info
        if (args.length == 0 || (args.length == 1 && (args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("info")))) {
            player.sendMessage(Component.empty()
                    .append(Component.text(getName() + " Command Syntax:").color(NamedTextColor.DARK_AQUA))
                    .append(Component.newline())
                    .append(Component.text("/" + getActiveAlias() + " chunk").color(NamedTextColor.GOLD))
                    .append(Component.newline())
                    .append(Component.text("    Force refreshes the chunk that you are standing in.").color(NamedTextColor.YELLOW))
                    .append(Component.newline())
                    .append(Component.text("/" + getActiveAlias() + " painting").color(NamedTextColor.GOLD))
                    .append(Component.newline())
                    .append(Component.text("    Changes the painting you are looking at.").color(NamedTextColor.YELLOW))
                    .append(Component.newline())
                    .append(Component.text("/" + getActiveAlias() + " painting [number]").color(NamedTextColor.GOLD))
                    .append(Component.newline())
                    .append(Component.text("    Changes the painting you are looking at to a specified ID.").color(NamedTextColor.YELLOW))
            );
            return true;
        }

        // Command: /vox paint
        if (args[0].equalsIgnoreCase("paint")) {
            if (args.length == 1) {
                BlockHelper.paint(player, true, false, 0);
                return true;
            }

            // Command: /vox paint [number]
            if (args.length == 2) {
                try {
                    BlockHelper.paint(player, false, false, Integer.parseInt(args[0]));
                } catch (NumberFormatException e) {
                    player.sendMessage(Component.text("Invalid syntax. Command: /" + getActiveAlias() + " paint [number]").color(NamedTextColor.RED));
                }
                return true;
            }
        }

        // Command: /vox chunk
        if (args[0].equalsIgnoreCase("chunk")) {
            player.getWorld().refreshChunk(player.getLocation().getBlockX(), player.getLocation().getBlockZ());
            player.sendMessage(Component.text("Refreshed the chunk that you are standing in."));
            return true;
        }

        // Command: /vox goto <x> <z>
        if (args[0].equalsIgnoreCase("goto")) {
            try {
                final int x = Integer.parseInt(args[1]);
                final int z = Integer.parseInt(args[2]);

                player.teleport(new Location(player.getWorld(), x, player.getWorld().getHighestBlockYAt(x, z), z));
                player.sendMessage(Component.text("Whoosh!").color(NamedTextColor.DARK_PURPLE));
            } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                player.sendMessage(Component.text("Invalid syntax. Command:").color(NamedTextColor.RED).append(Component.text("/" + getActiveAlias() + " goto <x> <z>").color(NamedTextColor.GOLD)));
            }
            return true;
        }

        return false;
    }

    @Override
    public List<String> doSuggestion(Player player, String[] args) {
        if (getActiveAlias().equalsIgnoreCase("paint")) {
            if (args.length == 1) {
                return Lists.newArrayList("[number]");
            }
        }

        if (getActiveAlias().equalsIgnoreCase("goto")) {
            if (args.length <= 2) {
                return Lists.newArrayList("[number]");
            }
        }

        if (getActiveIdentifier().equalsIgnoreCase("vox")) {
            if (args.length == 1) {
                return Lists.newArrayList("goto", "paint", "chunk");
            }

            if (args[0].equalsIgnoreCase("paint")) {
                if (args.length == 2) {
                    return Lists.newArrayList("[number]");
                }
            }

            if (args[0].equalsIgnoreCase("goto")) {
                if (args.length <= 3) {
                    return Lists.newArrayList("[number]");
                }
            }
        }

        return new ArrayList<>();
    }

}
