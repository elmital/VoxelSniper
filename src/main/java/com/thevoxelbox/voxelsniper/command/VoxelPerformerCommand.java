package com.thevoxelbox.voxelsniper.command;

import com.google.common.collect.Lists;
import com.thevoxelbox.voxelsniper.VoxelProfileManager;
import com.thevoxelbox.voxelsniper.brush.IBrush;
import com.thevoxelbox.voxelsniper.brush.perform.IPerformerBrush;
import com.thevoxelbox.voxelsniper.brush.perform.Performer;
import com.thevoxelbox.voxelsniper.snipe.SnipeData;
import com.thevoxelbox.voxelsniper.snipe.Sniper;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class VoxelPerformerCommand extends VoxelCommand {

    public VoxelPerformerCommand() {
        super("VoxelPerformer");
        setIdentifier("p");
        setPermission("voxelsniper.sniper");
    }

    @Override
    public List<String> registerTabCompletion() {
        return Lists.newArrayList(Performer.getPerformerHandles());
    }

    @Override
    public boolean doCommand(Player player, String[] args) {
        Sniper sniper = VoxelProfileManager.getInstance().getSniperForPlayer(player);
        SnipeData snipeData = sniper.getSnipeData(sniper.getCurrentToolId());
        
        // Default command
        // Command: /p info, /p help
        if (args.length == 1 && (args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("info"))) {
            player.sendMessage(
                    Component.empty()
                            .append(Component.text(getName() + " Command Syntax:").color(NamedTextColor.DARK_AQUA))
                            .append(Component.newline())
                            .append(Component.text("/" + getActiveAlias() + "").color(NamedTextColor.GOLD))
                            .append(Component.newline())
                            .append(Component.text("    Sets the performer to the default performer; Material performer.").color(NamedTextColor.YELLOW))
                            .append(Component.newline())
                            .append(Component.text("/" + getActiveAlias() + " [performerHandle]").color(NamedTextColor.GOLD))
                            .append(Component.newline())
                            .append(Component.text("    Sets the performer to the specified performer.").color(NamedTextColor.YELLOW))
            );
            return true;
        }


        if (args.length == 0) {
            IBrush brush = sniper.getBrush(sniper.getCurrentToolId());
            if (brush instanceof IPerformerBrush) {
                ((IPerformerBrush) brush).parsePerformer("m", snipeData);
            } else {
                player.sendMessage(Component.text("The active brush is not a performer brush."));
            }
            return true;
        }

        if (args.length == 1) {
            IBrush brush = sniper.getBrush(sniper.getCurrentToolId());
            if (brush instanceof IPerformerBrush) {
                boolean success = ((IPerformerBrush) brush).parsePerformer(args[0], snipeData);
                if (!success) {
                    player.sendMessage(Component.text("No such performer with the handle ").color(NamedTextColor.RED).append(Component.text("'" + args[0] + "' exists.").color(NamedTextColor.DARK_RED)));
                }
            } else {
                player.sendMessage(Component.text("The active brush is not a performer brush."));
            }
            return true;
        }

        return false;
    }

    @Override
    public List<String> doSuggestion(Player player, String[] args) {
        if (args.length == 1) {
            return getTabCompletion();
        }
        
        return new ArrayList<>();
    }
}
