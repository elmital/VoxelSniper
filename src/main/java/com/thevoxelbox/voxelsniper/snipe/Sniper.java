package com.thevoxelbox.voxelsniper.snipe;

import com.google.common.collect.Maps;
import com.thevoxelbox.voxelsniper.VoxelSniper;
import com.thevoxelbox.voxelsniper.brush.IBrush;
import com.thevoxelbox.voxelsniper.brush.perform.IPerformerBrush;
import com.thevoxelbox.voxelsniper.brush.perform.PerformerBrush;
import com.thevoxelbox.voxelsniper.event.SniperMaterialChangedEvent;
import com.thevoxelbox.voxelsniper.event.SniperReplaceMaterialChangedEvent;
import com.thevoxelbox.voxelsniper.util.BlockHelper;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;

import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 *
 */
public class Sniper {

    private final VoxelSniper plugin;
    private final UUID player;
    private boolean enabled = true;
    private final LinkedList<Undo> undoList = new LinkedList<>();
    private final Map<String, SnipeTool> tools = Maps.newHashMap();

    public Sniper(VoxelSniper plugin, Player player) {
        this.plugin = plugin;
        this.player = player.getUniqueId();
        SnipeTool sniperTool = new SnipeTool(this);
        sniperTool.assignAction(SnipeAction.ARROW, Material.ARROW);
        sniperTool.assignAction(SnipeAction.GUNPOWDER, Material.GUNPOWDER);
        tools.put(null, sniperTool);
    }

    public String getCurrentToolId() {
        return getToolId((!getPlayer().getInventory().getItemInMainHand().getType().isAir()) ? getPlayer().getInventory().getItemInMainHand().getType() : null);
    }

    public String getToolId(Material itemInHand) {
        if (itemInHand == null) {
            return null;
        }

        for (Map.Entry<String, SnipeTool> entry : tools.entrySet()) {
            if (entry.getValue().hasToolAssigned(itemInHand)) {
                return entry.getKey();
            }
        }
        return null;
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(player);
    }

    /**
     * Sniper execution call.
     *
     * @param action Action player performed
     * @param itemInHand Item in hand of player
     * @param clickedBlock Block that the player targeted/interacted with
     * @param clickedFace Face of that targeted Block
     * @return true if command visibly processed, false otherwise.
     */
    public boolean snipe(Action action, Material itemInHand, Block clickedBlock, BlockFace clickedFace) {
        String toolId = getToolId(itemInHand);
        SnipeTool sniperTool = tools.get(toolId);

        switch (action) {
            case LEFT_CLICK_AIR:
            case LEFT_CLICK_BLOCK:
            case RIGHT_CLICK_AIR:
            case RIGHT_CLICK_BLOCK:
                break;
            default:
                return false;
        }

        if (sniperTool.hasToolAssigned(itemInHand)) {
            if (sniperTool.getCurrentBrush() == null) {
                getPlayer().sendMessage("No Brush selected.");
                return true;
            }

            if (!getPlayer().hasPermission(sniperTool.getCurrentBrush().getPermissionNode())) {
                getPlayer().sendMessage("You are not allowed to use this brush. You're missing the permission node '" + sniperTool.getCurrentBrush().getPermissionNode() + "'");
                return true;
            }

            SnipeData snipeData = sniperTool.getSnipeData();
            if (getPlayer().isSneaking()) {
                Block targetBlock;
                SnipeAction snipeAction = sniperTool.getActionAssigned(itemInHand);

                switch (action) {
                    case LEFT_CLICK_AIR:
                    case LEFT_CLICK_BLOCK:
                        if (clickedBlock != null) {
                            targetBlock = clickedBlock;
                        } else {
                            BlockHelper rangeBlockHelper = snipeData.isRanged() ? new BlockHelper(getPlayer(), getPlayer().getWorld(), snipeData.getRange()) : new BlockHelper(getPlayer(), getPlayer().getWorld());
                            targetBlock = snipeData.isRanged() ? rangeBlockHelper.getRangeBlock() : rangeBlockHelper.getTargetBlock();
                        }

                        switch (snipeAction) {
                            case GUNPOWDER, ARROW -> {
                                BlockData oldSubstance, newSubstance;
                                oldSubstance = snipeData.getVoxelSubstance();
                                if (targetBlock != null) {
                                    snipeData.setVoxelSubstance(targetBlock.getBlockData());
                                } else {
                                    snipeData.setVoxelSubstance(SnipeData.DEFAULT_VOXEL_SUBSTANCE);
                                }
                                newSubstance = snipeData.getVoxelSubstance();
                                SniperMaterialChangedEvent event = new SniperMaterialChangedEvent(this, toolId, oldSubstance, newSubstance);
                                Bukkit.getPluginManager().callEvent(event);
                                snipeData.getVoxelMessage().voxel();
                                return true;
                            }
                            default -> {
                            }
                        }
                        break;
                    case RIGHT_CLICK_AIR:
                    case RIGHT_CLICK_BLOCK:
                        if (clickedBlock != null) {
                            targetBlock = clickedBlock;
                        } else {
                            BlockHelper rangeBlockHelper = snipeData.isRanged() ? new BlockHelper(getPlayer(), getPlayer().getWorld(), snipeData.getRange()) : new BlockHelper(getPlayer(), getPlayer().getWorld());
                            targetBlock = snipeData.isRanged() ? rangeBlockHelper.getRangeBlock() : rangeBlockHelper.getTargetBlock();
                        }

                        switch (snipeAction) {
                            case ARROW:
                            case GUNPOWDER:
                                BlockData oldSubstance, newSubstance;
                                oldSubstance = snipeData.getReplaceSubstance();
                                if (targetBlock != null) {
                                    snipeData.setReplaceSubstance(targetBlock.getBlockData());
                                } else {
                                    snipeData.setVoxelSubstance(SnipeData.DEFAULT_VOXEL_SUBSTANCE);
                                }
                                newSubstance = snipeData.getReplaceSubstance();

                                SniperReplaceMaterialChangedEvent event = new SniperReplaceMaterialChangedEvent(this, toolId, oldSubstance, newSubstance);
                                Bukkit.getPluginManager().callEvent(event);

                                snipeData.getVoxelMessage().replace();
                            default:
                                break;
                        }
                        break;
                    default:
                        return false;
                }
            } else {
                Block targetBlock;
                Block lastBlock;
                SnipeAction snipeAction = sniperTool.getActionAssigned(itemInHand);

                switch (action) {
                    case RIGHT_CLICK_AIR:
                    case RIGHT_CLICK_BLOCK:
                        break;
                    default:
                        return false;
                }

                if (clickedBlock != null) {
                    targetBlock = clickedBlock;
                    lastBlock = clickedBlock.getRelative(clickedFace);
                } else {
                    BlockHelper rangeBlockHelper = snipeData.isRanged() ? new BlockHelper(getPlayer(), getPlayer().getWorld(), snipeData.getRange()) : new BlockHelper(getPlayer(), getPlayer().getWorld());
                    targetBlock = snipeData.isRanged() ? rangeBlockHelper.getRangeBlock() : rangeBlockHelper.getTargetBlock();
                    lastBlock = rangeBlockHelper.getLastBlock();

                    if (targetBlock == null || lastBlock == null) {
                        getPlayer().sendMessage(Component.text("Snipe target block must be visible.").color(NamedTextColor.RED));
                        return true;
                    }
                }

                if (sniperTool.getCurrentBrush() instanceof PerformerBrush performerBrush) {
                    performerBrush.initP(snipeData);
                }

                return sniperTool.getCurrentBrush().perform(snipeAction, snipeData, targetBlock, lastBlock);
            }
        }
        return false;
    }

    public IBrush setBrush(String toolId, Class<? extends IBrush> brush) {
        if (!tools.containsKey(toolId)) {
            return null;
        }

        return tools.get(toolId).setCurrentBrush(brush);
    }

    public IBrush getBrush(String toolId) {
        if (!tools.containsKey(toolId)) {
            return null;
        }

        return tools.get(toolId).getCurrentBrush();
    }

    public IBrush previousBrush(String toolId) {
        if (!tools.containsKey(toolId)) {
            return null;
        }

        return tools.get(toolId).previousBrush();
    }

    public boolean setTool(String toolId, SnipeAction action, Material itemInHand) {
        for (Map.Entry<String, SnipeTool> entry : tools.entrySet()) {
            if (!Objects.equals(entry.getKey(), toolId) && entry.getValue().hasToolAssigned(itemInHand)) {
                return false;
            }
        }

        if (!tools.containsKey(toolId)) {
            SnipeTool tool = new SnipeTool(this);
            tools.put(toolId, tool);
        }
        tools.get(toolId).assignAction(action, itemInHand);
        return true;
    }

    public void removeTool(String toolId, Material itemInHand) {
        if (!tools.containsKey(toolId)) {
            SnipeTool tool = new SnipeTool(this);
            tools.put(toolId, tool);
        }
        tools.get(toolId).unassignAction(itemInHand);
    }

    public void removeTool(String toolId) {
        if (toolId == null) {
            return;
        }
        tools.remove(toolId);
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void storeUndo(Undo undo) {
        if (VoxelSniper.getInstance().getVoxelSniperConfiguration().getUndoCacheSize() <= 0) {
            return;
        }
        if (undo != null && undo.getSize() > 0) {
            while (undoList.size() >= plugin.getVoxelSniperConfiguration().getUndoCacheSize()) {
                this.undoList.pollLast();
            }
            undoList.push(undo);
        }
    }

    public int undo() {
        return undo(1);
    }

    public int undo(int amount) {
        int changedBlocks = 0;
        if (this.undoList.isEmpty()) {
            getPlayer().sendMessage(Component.text("There's nothing to undo.").color(NamedTextColor.GREEN));
        } else {
            for (int x = 0; x < amount && !undoList.isEmpty(); x++) {
                Undo undo = this.undoList.pop();
                if (undo != null) {
                    undo.undo();
                    changedBlocks += undo.getSize();
                } else { // TODO: Check if this logic makes sense
                    break;
                }
            }

            getPlayer().sendMessage(Component.text("Undo successful: ").color(NamedTextColor.GREEN).append(Component.text(changedBlocks).color(NamedTextColor.RED)).append(Component.text(" blocks have been replaced.")));
        }
        return changedBlocks;
    }

    public void reset(String toolId) {
        SnipeTool backup = tools.remove(toolId);
        SnipeTool newTool = new SnipeTool(this);

        for (Map.Entry<SnipeAction, Material> entry : backup.getActionTools().entrySet()) {
            newTool.assignAction(entry.getKey(), entry.getValue());
        }
        tools.put(toolId, newTool);
    }

    public SnipeData getSnipeData(String toolId) {
        return tools.containsKey(toolId) ? tools.get(toolId).getSnipeData() : null;
    }

    public void displayInfo() {
        String currentToolId = getCurrentToolId();
        SnipeTool sniperTool = tools.get(currentToolId);
        IBrush brush = sniperTool.getCurrentBrush();
        getPlayer().sendMessage("Current Tool: " + ((currentToolId != null) ? currentToolId : "Default Tool"));
        if (brush == null) {
            getPlayer().sendMessage("No brush selected.");
            return;
        }
        brush.info(sniperTool.getMessageHelper());
        if (brush instanceof IPerformerBrush) {
            ((IPerformerBrush) brush).showInfo(sniperTool.getMessageHelper());
        }
    }

    public SnipeTool getSnipeTool(String toolId) {
        return tools.get(toolId);
    }
}
