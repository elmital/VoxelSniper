package com.thevoxelbox.voxelsniper.command;

import com.thevoxelbox.voxelsniper.VoxelBrushManager;
import com.thevoxelbox.voxelsniper.VoxelProfileManager;
import com.thevoxelbox.voxelsniper.brush.IBrush;
import com.thevoxelbox.voxelsniper.brush.perform.IPerformerBrush;
import com.thevoxelbox.voxelsniper.event.SniperBrushChangedEvent;
import com.thevoxelbox.voxelsniper.event.SniperBrushSizeChangedEvent;
import com.thevoxelbox.voxelsniper.snipe.SnipeData;
import com.thevoxelbox.voxelsniper.snipe.Sniper;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.thevoxelbox.voxelsniper.VoxelCommandManager.BRUSH_SUBCOMMAND_PREFIX;
import static com.thevoxelbox.voxelsniper.VoxelCommandManager.BRUSH_SUBCOMMAND_SUFFIX;

public class VoxelBrushCommand extends VoxelCommand {

    public VoxelBrushCommand() {
        super("VoxelBrush");
        setIdentifier("b");
        setPermission("voxelsniper.sniper");
    }

    @Override
    public List<String> registerTabCompletion() {
        List<String> brushes = new ArrayList<>(VoxelBrushManager.getInstance().getBrushHandles());
        brushes.add("<brushSize>");

        return brushes;
    }

    @Override
    public boolean doCommand(Player player, String[] args) {
        Sniper sniper = VoxelProfileManager.getInstance().getSniperForPlayer(player);
        String currentToolId = sniper.getCurrentToolId();
        SnipeData snipeData = sniper.getSnipeData(currentToolId);

        // Default command
        // Command: /b, /b help, /b info
        if (args.length == 1 && (args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("info"))) {
            player.sendMessage(Component.empty()
                    .append(Component.text(getName() + " Command Syntax:").color(NamedTextColor.DARK_AQUA))
                    .append(Component.newline())
                    .append(Component.text("/" + getActiveAlias() + " [brushHandle] [arguments...]").color(NamedTextColor.GOLD))
                    .append(Component.newline())
                    .append(Component.text("    Changes to the brush with the specified brush handle, with the specified arguments.").color(NamedTextColor.YELLOW))
                    .append(Component.newline())
                    .append(Component.text("/" + getActiveAlias() + " p [performerHandle]").color(NamedTextColor.GOLD))
                    .append(Component.newline())
                    .append(Component.text("    Changes to the brush with the specified brush handle and the specified performer.").color(NamedTextColor.YELLOW))
                    .append(Component.newline())
                    .append(Component.text("/" + getActiveAlias() + " [brushSize]").color(NamedTextColor.GOLD))
                    .append(Component.newline())
                    .append(Component.text("    Sets the brush size of the active brush.").color(NamedTextColor.YELLOW)));
            return true;
        }

        // No arguments -> show brush settings
        if (args.length == 0) {
            player.sendMessage(Component.text("VoxelSniper - Current Brush Settings:").color(NamedTextColor.DARK_RED));
            sniper.displayInfo();
            return true;
        }

        // Command: /b <number> -- Change brush size
        if (args.length > 0) {
            try {
                int originalSize = snipeData.getBrushSize();
                snipeData.setBrushSize(Integer.parseInt(args[0]));

                SniperBrushSizeChangedEvent event = new SniperBrushSizeChangedEvent(sniper, currentToolId, originalSize, snipeData.getBrushSize());
                Bukkit.getPluginManager().callEvent(event);

                snipeData.getVoxelMessage().size();
                return true;
            } catch (NumberFormatException exception) {
            }
        }

        if (args.length > 0) {
            // Command: /b list -- list all brushes
            if (args[0].equals("list")) {
                // TODO: LIST BRUSHES
                return true;
            }

            // Command: /b <brush> -- change brush to <brush>
            Class<? extends IBrush> brush = VoxelBrushManager.getInstance().getBrushForHandle(args[0]);

            if (brush == null) {
                player.sendMessage(Component.text("No brush exists with the brush handle '" + args[0] + "'.").color(NamedTextColor.RED));
            } else {
                IBrush oldBrush = sniper.getBrush(currentToolId);
                IBrush newBrush = sniper.setBrush(currentToolId, brush);

                if (newBrush == null) {
                    player.sendMessage(Component.text("You do not have the required permissions to use that brush.").color(NamedTextColor.RED));
                    return true;
                }

                // Command: /b <brush> <...> -- Handles additional variables
                if (args.length > 1) {
                    String[] additionalParameters = Arrays.copyOfRange(args, 1, args.length);

                    // Parse performer if the brush is a performer
                    if (newBrush instanceof IPerformerBrush) {
                        ((IPerformerBrush) newBrush).parsePerformer(args[0], additionalParameters, snipeData);
                        return true;
                    } else {
                        newBrush.parseParameters(args[0], additionalParameters, snipeData);
                        return true;
                    }
                }
                SniperBrushChangedEvent event = new SniperBrushChangedEvent(sniper, currentToolId, oldBrush, newBrush);
                sniper.displayInfo();
            }

            return true;
        }

        return false;
    }

    @Override
    public List<String> doSuggestion(Player player, String[] args) {
        if (args.length == 1) {
            return getTabCompletion(args.length);
        }

        if (args.length >= 2) {
            if (args.length % 2 == 0) {
                return getTabCompletion(BRUSH_SUBCOMMAND_PREFIX + args[0], 1);
            } else {
                return getTabCompletion(BRUSH_SUBCOMMAND_PREFIX + args[0] + BRUSH_SUBCOMMAND_SUFFIX + args[args.length - 2], 1);
            }
        }

        return new ArrayList<>();
    }
}
