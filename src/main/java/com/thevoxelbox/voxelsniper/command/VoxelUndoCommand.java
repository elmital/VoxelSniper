package com.thevoxelbox.voxelsniper.command;

import com.thevoxelbox.voxelsniper.VoxelProfileManager;
import com.thevoxelbox.voxelsniper.snipe.Sniper;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class VoxelUndoCommand extends VoxelCommand {

    public VoxelUndoCommand() {
        super("VoxelUndo");
        setIdentifier("u");
        addOtherIdentifiers("uu");
        setPermission("voxelsniper.sniper");
    }

    @Override
    public boolean doCommand(Player player, String[] args) {
        VoxelProfileManager profileManager = VoxelProfileManager.getInstance();
        Sniper sniper = profileManager.getSniperForPlayer(player);

        if ((args.length == 1 && (args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("info"))) || args.length > 2) {
            var comp = Component.empty().append(Component.text(" Command Syntax:").color(NamedTextColor.DARK_AQUA));

            if (getActiveIdentifier().equalsIgnoreCase("u")) {
                comp = comp.append(Component.text("/" + getActiveAlias()).color(NamedTextColor.GOLD))
                        .append(Component.newline())
                        .append(Component.text("    Undo latest changes for yourself.").color(NamedTextColor.YELLOW))
                        .append(Component.newline())
                        .append(Component.text("/" + getActiveAlias() + " [changes]").color(NamedTextColor.GOLD))
                        .append(Component.newline())
                        .append(Component.text("    Undo previous [amount] changes for yourself.").color(NamedTextColor.YELLOW));
            }
            player.sendMessage(
                    comp.append(Component.text("/" + getActiveAlias() + " [player]").color(NamedTextColor.GOLD))
                            .append(Component.newline())
                            .append(Component.text("    Undo the latest changes for specified player.").color(NamedTextColor.YELLOW))
                            .append(Component.newline())
                            .append(Component.text("/" + getActiveAlias() + " [player] [amount]").color(NamedTextColor.GOLD))
                            .append(Component.newline())
                            .append(Component.text("    Undo the previous [amount] changes for specified player.").color(NamedTextColor.YELLOW))
            );
            return true;
        }

        // Command: /u      <- Undo the latest changes for yourself.
        if (args.length == 0 && getActiveIdentifier().equalsIgnoreCase("u")) {
            sniper.undo();
            return true;
        }

        // Command: /u [amount]         <- Undo the previous [amount] changes for yourself.
        if (args.length == 1 && getActiveIdentifier().equalsIgnoreCase("u")) {
            try {
                sniper.undo(Integer.parseInt(args[0]));
                return true;
            } catch (NumberFormatException ignored) {
            }
        }

        if (!player.hasPermission("voxelsniper.undouser")) {
            player.sendMessage(Component.text("You need the 'voxelsniper.undouser' permission to undo other user's changes.").color(NamedTextColor.RED));
            return true;
        }

        // Command: /u [playerName]             <- Undo [playerName]'s changes.
        if (args.length == 1 || args.length == 2) {
            try {
                Player targetPlayer = Bukkit.getPlayer(args[0]);
                assert targetPlayer != null;

                sniper = profileManager.getSniperForPlayer(targetPlayer);
                int undoAmount = 1;

                // Command: /u [playerName] [amount]    <- Undo the previous [amount] changes made by [playerName].
                if (args.length == 2) {
                    try {
                        undoAmount = Integer.parseInt(args[1]);
                    } catch (NumberFormatException e) {
                        player.sendMessage(Component.text("Please enter a valid amount to undo. Value must be a number.").color(NamedTextColor.RED));
                        return true;
                    }
                }

                targetPlayer.sendMessage(Component.text("Your changes were undone by someone else.").color(NamedTextColor.LIGHT_PURPLE));
                int amountChanged = sniper.undo(undoAmount);
                player.sendMessage(Component.text("Undid " + sniper.getPlayer().getName() + "'s changes: ").color(NamedTextColor.GOLD).append(Component.text(amountChanged + " blocks replaced").color(NamedTextColor.DARK_AQUA)));
                return true;
            } catch (Exception e) {
                player.sendMessage(Component.text("Could not find the player ").color(NamedTextColor.RED).append(Component.text("'" + args[0] + "'").color(NamedTextColor.GOLD)).append(Component.text(".").color(NamedTextColor.RED)));
                return true;
            }
        }

        return false;
    }

    @Override
    public List<String> doSuggestion(Player player, String[] args) {
        List<String> suggestions = new ArrayList<>();

        if (args.length == 1 || args.length == 2) {
            suggestions.add("[amount]");
        }

        if (args.length == 1) {
            Bukkit.getOnlinePlayers().stream().map(HumanEntity::getName).forEach(suggestions::add);
        }

        return suggestions;
    }
}
