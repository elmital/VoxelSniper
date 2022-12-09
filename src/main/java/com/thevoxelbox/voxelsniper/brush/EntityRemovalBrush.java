package com.thevoxelbox.voxelsniper.brush;

import com.google.common.collect.Lists;
import com.thevoxelbox.voxelsniper.VoxelMessage;
import com.thevoxelbox.voxelsniper.snipe.SnipeData;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Chunk;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;

/**
 *
 */
public class EntityRemovalBrush extends Brush {

    private final List<EntityType> exclusionList = new ArrayList<>();

    /**
     *
     */
    public EntityRemovalBrush() {
        this.setName("Entity Removal Brush");
        defaultValues();
    }

    private void defaultValues() {
        exclusionList.clear();

        exclusionList.add(EntityType.ARMOR_STAND);
        exclusionList.add(EntityType.BOAT);
        exclusionList.add(EntityType.CHEST_BOAT);
        exclusionList.add(EntityType.DROPPED_ITEM);
        exclusionList.add(EntityType.ITEM_FRAME);
        exclusionList.add(EntityType.LEASH_HITCH);
        exclusionList.add(EntityType.MINECART);
        exclusionList.add(EntityType.MINECART_CHEST);
        exclusionList.add(EntityType.MINECART_COMMAND);
        exclusionList.add(EntityType.MINECART_FURNACE);
        exclusionList.add(EntityType.MINECART_HOPPER);
        exclusionList.add(EntityType.MINECART_MOB_SPAWNER);
        exclusionList.add(EntityType.MINECART_TNT);
        exclusionList.add(EntityType.PAINTING);
        exclusionList.add(EntityType.PLAYER);
        exclusionList.add(EntityType.VILLAGER);
        exclusionList.add(EntityType.WANDERING_TRADER);

    }

    private void radialRemoval(SnipeData v) {
        final Chunk targetChunk = getTargetBlock().getChunk();
        int entityCount = 0;
        int chunkCount = 0;

        try {
            entityCount += removeEntities(targetChunk);

            int radius = v.getBrushSize() / 16;

            for (int x = targetChunk.getX() - radius; x <= targetChunk.getX() + radius; x++) {
                for (int z = targetChunk.getZ() - radius; z <= targetChunk.getZ() + radius; z++) {
                    entityCount += removeEntities(getWorld().getChunkAt(x, z));

                    chunkCount++;
                }
            }
        } catch (final PatternSyntaxException pse) {
            pse.printStackTrace();
            v.sendMessage(Component.text("Error in RegEx: ").color(NamedTextColor.RED)
                    .append(Component.text(pse.getPattern()).color(NamedTextColor.LIGHT_PURPLE))
                    .append(Component.newline())
                    .append(Component.text(String.format("%s (Index: %d)", pse.getDescription(), pse.getIndex())))
            );
        }
        v.sendMessage(Component.text("Removed ").color(NamedTextColor.GREEN)
                .append(Component.text(entityCount).color(NamedTextColor.RED))
                .append(Component.text(" entities out of "))
                .append(Component.text(chunkCount).color(NamedTextColor.BLUE))
                .append(Component.text(chunkCount == 1 ? " chunk." : " chunks."))
        );
    }

    private int removeEntities(Chunk chunk) throws PatternSyntaxException {
        int entityCount = 0;

        for (Entity entity : chunk.getEntities()) {
            if (exclusionList.contains(entity.getType())) {
                continue;
            }

            entity.remove();
            entityCount++;
        }

        return entityCount;
    }

    @Override
    protected void arrow(SnipeData v) {
        this.radialRemoval(v);
    }

    @Override
    protected void powder(SnipeData v) {
        this.radialRemoval(v);
    }

    @Override
    public void info(VoxelMessage vm) {
        vm.brushName(getName());
        vm.custom(Component.text( "Exclusions: ").color(NamedTextColor.GREEN).append(Component.text(exclusionList.stream().map(Enum::name).collect(Collectors.joining(", "))).color(NamedTextColor.DARK_GREEN)));
        vm.size();
    }

    @Override
    public void parseParameters(final String triggerHandle, final String[] params, final SnipeData v) {
        if (params[0].equalsIgnoreCase("info")) {
            v.getVoxelMessage().commandParameters("Entity Brush Parameters: "
                    , null
                    , "/b " + triggerHandle + " + [entityType]  -- Add entity to exception list"
                    , "/b " + triggerHandle + " - [entityType]  -- Remove entity from exception list"
                    , "/b " + triggerHandle + " reset  -- Resets exception list to defaults"
                    , "/b " + triggerHandle + " clear  -- Clear exception list"
                    , "/b " + triggerHandle + " list  -- Shows entities in exception list"
            );
            return;
        }

        if (params[0].equalsIgnoreCase("reset")) {
            defaultValues();
            v.sendMessage(Component.text("Reset exclusions list to default values.").color(NamedTextColor.GOLD));
            return;
        }

        if (params[0].equalsIgnoreCase("clear")) {
            exclusionList.clear();
            v.sendMessage(Component.text("Cleared the exclusions list.").color(NamedTextColor.GOLD)
                    .append(Component.text(" WARNING! ").color(NamedTextColor.RED))
                    .append(Component.text("All").color(NamedTextColor.DARK_RED))
                    .append(Component.text(" entities can now be removed by the brush. BE CAREFUL!").color(NamedTextColor.RED)));
            return;
        }

        if (params[0].equalsIgnoreCase("list")) {
            v.sendMessage(Component.text("Exclusions: ").color(NamedTextColor.GREEN)
                    .append(Component.text(exclusionList.stream().map(Enum::name).collect(Collectors.joining(", "))).color(NamedTextColor.DARK_GREEN)));
            return;
        }

        if (params[0].equalsIgnoreCase("+") || params[0].equalsIgnoreCase("-")) {
            try {
                EntityType entity = EntityType.valueOf(params[1]);

                if (params[0].equals("+")) {
                    exclusionList.add(entity);
                    v.sendMessage(Component.text("Added " + entity.name() + " to exclusion list.").color(NamedTextColor.GOLD));
                } else {
                    if (exclusionList.contains(entity)) {
                        exclusionList.remove(entity);
                        v.sendMessage(Component.text("Removed " + entity.name() + " from exclusion list.").color(NamedTextColor.GOLD));
                    } else {
                        v.sendMessage(Component.text(entity.name() + " wasn't in exclusion list. Nothing happened.").color(NamedTextColor.GOLD));
                    }
                }

                return;
            } catch (IllegalArgumentException | ArrayIndexOutOfBoundsException ignored) {
            }
        }

        v.getVoxelMessage().invalidUseParameter(triggerHandle);
    }

    @Override
    public List<String> registerArguments() {
        return new ArrayList<>(Lists.newArrayList("+", "-", "reset", "clear", "list"));
    }

    @Override
    public HashMap<String, List<String>> registerArgumentValues() {
        HashMap<String, List<String>> argumentValues = new HashMap<>();

        List<String> entities = new ArrayList<>();

        for (EntityType entity : EntityType.values()) {
            entities.add(entity.name());
        }

        
        argumentValues.put("+", entities);
        argumentValues.put("-", entities);
        return argumentValues;
    }

    @Override
    public String getPermissionNode() {
        return "voxelsniper.brush.entityremoval";
    }
}
