package com.thevoxelbox.voxelsniper;

import com.thevoxelbox.voxelsniper.brush.IBrush;
import com.thevoxelbox.voxelsniper.command.VoxelBrushCommand;
import com.thevoxelbox.voxelsniper.command.VoxelBrushToolCommand;
import com.thevoxelbox.voxelsniper.command.VoxelCommand;
import com.thevoxelbox.voxelsniper.command.VoxelDefaultCommand;
import com.thevoxelbox.voxelsniper.command.VoxelInkCommand;
import com.thevoxelbox.voxelsniper.command.VoxelInkReplaceCommand;
import com.thevoxelbox.voxelsniper.command.VoxelPerformerCommand;
import com.thevoxelbox.voxelsniper.command.VoxelReplaceCommand;
import com.thevoxelbox.voxelsniper.command.VoxelSniperCommand;
import com.thevoxelbox.voxelsniper.command.VoxelUndoCommand;
import com.thevoxelbox.voxelsniper.command.VoxelVariablesCommand;
import com.thevoxelbox.voxelsniper.command.VoxelVoxCommand;
import com.thevoxelbox.voxelsniper.command.VoxelVoxelCommand;
import org.bukkit.command.PluginCommand;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ervinnnc
 */
public class VoxelCommandManager {

    private static VoxelCommandManager instance = null;

    public static final String BRUSH_SUBCOMMAND_PREFIX = "brush-";
    public static final String BRUSH_SUBCOMMAND_SUFFIX = "-";

    private final List<VoxelCommand> commands = new ArrayList<>();
    private final HashMap<String, List<String>> argumentsMap = new HashMap<>();

    public static VoxelCommandManager getInstance() {
        return instance;
    }

    public static void initialize() {
        VoxelCommandManager commandManager = getInstance();

        // Instantiate Command Manager if it's not yet instantiated.
        if (commandManager == null) {
            instance = new VoxelCommandManager();
            commandManager = getInstance();
        }

        commandManager.registerCommand(new VoxelBrushCommand());
        commandManager.registerCommand(new VoxelBrushToolCommand());
        commandManager.registerCommand(new VoxelDefaultCommand());
        commandManager.registerCommand(new VoxelInkCommand());
        commandManager.registerCommand(new VoxelInkReplaceCommand());
        commandManager.registerCommand(new VoxelPerformerCommand());
        commandManager.registerCommand(new VoxelReplaceCommand());
        commandManager.registerCommand(new VoxelSniperCommand());
        commandManager.registerCommand(new VoxelUndoCommand());
        commandManager.registerCommand(new VoxelVariablesCommand());
        commandManager.registerCommand(new VoxelVoxCommand());
        commandManager.registerCommand(new VoxelVoxelCommand());

        commandManager.registerBrushSubcommands();
    }

    public void registerCommand(VoxelCommand command) {
        // Add to local command map for persistence
        commands.add(command);

        // Initializes the Bukkit-sided registration of the command
        PluginCommand bukkitCommand = VoxelSniper.getInstance().getCommand(command.getIdentifier());
        argumentsMap.put(command.getIdentifier(), command.registerTabCompletion());

        if(bukkitCommand == null) {
            VoxelSniper.getInstance().getLogger().severe("Command " + command.getIdentifier() + " not found!");
            return;
        }

        bukkitCommand.setExecutor(command);
        bukkitCommand.getAliases().forEach(e -> argumentsMap.put(e, command.registerTabCompletion()));

        // Initializes command alternates that use the same executors
        command.getOtherIdentifiers().forEach((otherIdentifier) -> {
            PluginCommand bukkitCommandAlt = VoxelSniper.getInstance().getCommand(otherIdentifier);
            argumentsMap.put(otherIdentifier, command.registerTabCompletion());

            if(bukkitCommandAlt == null) {
                VoxelSniper.getInstance().getLogger().severe("Command alt " + otherIdentifier + " not found!");
                return;
            }

            bukkitCommandAlt.setExecutor(command);
            bukkitCommandAlt.getAliases().forEach(e -> argumentsMap.put(e, command.registerTabCompletion()));
        });
    }

    public void registerBrushSubcommands() {
        try {
            for (String brushHandle : VoxelBrushManager.getInstance().getBrushHandles()) {
                // Initialize brush to retrieve subcommand map
                IBrush brush = VoxelBrushManager.getInstance().getBrushForHandle(brushHandle).getConstructor().newInstance();

                if (argumentsMap.containsKey(BRUSH_SUBCOMMAND_PREFIX + brushHandle)) {
                    VoxelSniper.getInstance().getLogger().log(Level.WARNING, "Did not add clashing argument map: {0}, Brush handle: {1}", new Object[]{BRUSH_SUBCOMMAND_PREFIX + brushHandle, brushHandle});
                    return;
                }

                argumentsMap.put(BRUSH_SUBCOMMAND_PREFIX + brushHandle, brush.registerArguments());

                brush.registerArgumentValues().forEach((identifier, arguments) -> {
                    if (argumentsMap.containsKey(BRUSH_SUBCOMMAND_PREFIX + brushHandle + BRUSH_SUBCOMMAND_SUFFIX + identifier)) {
                        VoxelSniper.getInstance().getLogger().log(Level.WARNING, "Did not add clashing argument map: {0}, Brush handle: {1}", new Object[]{BRUSH_SUBCOMMAND_PREFIX + brushHandle + identifier, brushHandle});
                        return;
                    }

                    argumentsMap.put(BRUSH_SUBCOMMAND_PREFIX + brushHandle + BRUSH_SUBCOMMAND_SUFFIX + identifier, arguments);
                });
            }
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException ex) {
            Logger.getLogger(VoxelCommandManager.class.getName()).log(Level.SEVERE, "Could not initialize brush subcommand arguments!", ex);
        }
    }

    public List<String> getCommandArgumentsList(String commandName) {
        // If not defined, return an empty list.
        if (!argumentsMap.containsKey(commandName)) {
            return new ArrayList<>();
        }

        return argumentsMap.getOrDefault(commandName, new ArrayList<>());
    }
}
