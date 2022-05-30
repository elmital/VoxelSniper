package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.VoxelMessage;
import com.thevoxelbox.voxelsniper.snipe.SnipeData;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.List;

/**
 * http://www.voxelwiki.com/minecraft/Voxelsniper#The_Entity_Brush
 *
 * @author Piotr
 */
public class EntityBrush extends Brush {

    private EntityType entityType = EntityType.ZOMBIE;

    /**
     *
     */
    public EntityBrush() {
        this.setName("Entity");
    }

    private void spawn(final SnipeData v) {
        for (int x = 0; x < v.getBrushSize(); x++) {
            try {
                this.getWorld().spawn(this.getLastBlock().getLocation(), this.entityType.getEntityClass());
            } catch (final IllegalArgumentException exception) {
                v.sendMessage(Component.text("Cannot spawn entity!").color(NamedTextColor.RED));
            }
        }
    }

    @Override
    protected final void arrow(final SnipeData v) {
        this.spawn(v);
    }

    @Override
    protected final void powder(final SnipeData v) {
        this.spawn(v);
    }

    @SuppressWarnings("deprecation")
    @Override
    public final void info(final VoxelMessage vm) {
        vm.brushMessage("Entity Brush" + " (" + this.entityType.getName() + ")");
        vm.size();
    }

    @SuppressWarnings("deprecation")
    @Override
    public final void parseParameters(final String triggerHandle, final String[] params, final SnipeData v) {
        if (params[0].equalsIgnoreCase("info")) {
            v.getVoxelMessage().commandParameters("Entity Brush Parameters:", null, "/b " + triggerHandle + " [entityType] -- Change brush to the specified entity type");
            return;
        }

        try {
            this.entityType = EntityType.valueOf(params[0]);
            v.sendMessage(Component.text("Entity type: ").color(NamedTextColor.GOLD).append(Component.text(this.entityType.name()).color(NamedTextColor.DARK_GREEN)));
        } catch (IllegalArgumentException e) {
            v.sendMessage(Component.text("That entity type does not exist.").color(NamedTextColor.RED));
        }
    }

    @Override
    public List<String> registerArguments() {
        List<String> arguments = new ArrayList<>();
        List<String> entities = new ArrayList<>();

        for (EntityType entity : EntityType.values()) {
            entities.add(entity.name());
        }

        arguments.addAll(entities);
        
        return arguments;
    }

    @Override
    public String getPermissionNode() {
        return "voxelsniper.brush.entity";
    }
}
