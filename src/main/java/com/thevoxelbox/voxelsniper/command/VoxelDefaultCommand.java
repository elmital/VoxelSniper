package com.thevoxelbox.voxelsniper.command;

import com.thevoxelbox.voxelsniper.VoxelProfileManager;
import com.thevoxelbox.voxelsniper.snipe.Sniper;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class VoxelDefaultCommand extends VoxelCommand {

    public VoxelDefaultCommand() {
        super("VoxelDefault");
        setIdentifier("d");
        setPermission("voxelsniper.sniper");
    }

    @Override
    public boolean doCommand(Player player, String[] args) {
        Sniper sniper = VoxelProfileManager.getInstance().getSniperForPlayer(player);

        // Default command
        // Command: /d info, /d help
        if (args.length == 1 && (args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("info"))) {
            player.sendMessage(
                    Component.empty()
                            .append(Component.text(getName() + " Command Syntax:").color(NamedTextColor.DARK_AQUA))
                            .append(Component.newline())
                            .append(Component.text("/" + getActiveAlias()).color(NamedTextColor.GOLD))
                            .append(Component.newline())
                            .append(Component.text("    Resets tool to default values.").color(NamedTextColor.YELLOW))
            );
            return true;
        }

        if (args.length == 0) {
            sniper.reset(sniper.getCurrentToolId());
            player.sendMessage(Component.text("Brush settings reset to their default values.").color(NamedTextColor.AQUA));
            return true;
        }
        
        return false;
    }

    @Override
    public List<String> doSuggestion(Player player, String[] args) {
        return new ArrayList<>();
    }
}
