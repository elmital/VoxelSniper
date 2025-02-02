package com.thevoxelbox.voxelsniper.command;

import com.google.common.collect.Lists;
import com.thevoxelbox.voxelsniper.VoxelProfileManager;
import com.thevoxelbox.voxelsniper.snipe.SnipeData;
import com.thevoxelbox.voxelsniper.snipe.Sniper;
import com.thevoxelbox.voxelsniper.util.BlockHelper;
import com.thevoxelbox.voxelsniper.util.MaterialTranslator;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class VoxelReplaceCommand extends VoxelCommand {

    public VoxelReplaceCommand() {
        super("VoxelReplace");
        setIdentifier("vr");
        setPermission("voxelsniper.sniper");
    }

    @Override
    public List<String> registerTabCompletion() {
        return Arrays.stream(Material.values()).filter(Material::isBlock).map(e -> e.getKey().toString()).collect(Collectors.toList());
    }

    @Override
    public boolean doCommand(Player player, String[] args) {
        Sniper sniper = VoxelProfileManager.getInstance().getSniperForPlayer(player);
        SnipeData snipeData = sniper.getSnipeData(sniper.getCurrentToolId());
        
        // Default command
        // Command: /vr info, /vr help
        if (args.length == 1 && (args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("info"))) {
            player.sendMessage(Component.empty()
                    .append(Component.text(getName() + " Command Syntax:").color(NamedTextColor.DARK_AQUA))
                    .append(Component.newline())
                    .append(Component.text("/" + getActiveAlias() + "").color(NamedTextColor.GOLD))
                    .append(Component.newline())
                    .append(Component.text("    Sets the block you are looking at as the active replace material.").color(NamedTextColor.YELLOW))
                    .append(Component.newline())
                    .append(Component.text("/" + getActiveAlias() + " [material]").color(NamedTextColor.GOLD))
                    .append(Component.newline())
                    .append(Component.text("    Sets the specified block as the active replace material.").color(NamedTextColor.YELLOW))
            );
            return true;
        }

        // Command: /vr          <- Sets the block user is looking at as voxel substance.
        if (args.length == 0) {
            Block selectedBlock = new BlockHelper(player, player.getWorld()).getTargetBlock();
            if (selectedBlock != null) {
                snipeData.setReplaceSubstance(selectedBlock.getBlockData());
                snipeData.getVoxelMessage().replace();
            } else {
                player.sendMessage(Component.text("Nothing to imitate replace material. No changes were made.").color(NamedTextColor.GOLD));
            }
            return true;
        }

        // Command: /vr [material]       <- Sets the defined material as voxel substance.
        Material material = Material.matchMaterial(args[0]); // TODO: Match old ID numbers to materials
        BlockData blockData;
        if (material == null) {
            blockData = MaterialTranslator.resolveMaterial(args[0]);
        } else {
            blockData = material.createBlockData();
        }

        if (blockData != null && blockData.getMaterial().isBlock()) {
            snipeData.setReplaceSubstance(blockData);
            snipeData.getVoxelMessage().replace();
        } else {
            player.sendMessage(Component.text("You have entered an invalid Item ID.").color(NamedTextColor.RED));
        }
        return true;
    }

    @Override
    public List<String> doSuggestion(Player player, String[] args) {
        if (args.length == 1) {
            // Preprocess the string for partial matching
            args[0] = args[0].toLowerCase();

            if (!args[0].startsWith("minecraft:")) {
                if (args[0].startsWith("mi") && !args[0].equalsIgnoreCase("minecraft:")) {
                    return Lists.newArrayList("minecraft:");
                }

                args[0] = "minecraft:" + args[0];
            }

            return getTabCompletion();
        }

        return new ArrayList<>();
    }
}
