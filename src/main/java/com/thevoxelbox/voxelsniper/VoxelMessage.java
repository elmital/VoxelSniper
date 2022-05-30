package com.thevoxelbox.voxelsniper;

import com.thevoxelbox.voxelsniper.snipe.SnipeData;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Collectors;

/**
 *  Messaging handler for various Voxel functions.
 *  // TODO: Rewrite messaging to builder functions.
 *  // TODO: Standardize message colors.
 */
public class VoxelMessage {

    private static final int BRUSH_SIZE_WARNING_THRESHOLD = 20;
    private final SnipeData snipeData;

    /**
     * @param snipeData
     */
    public VoxelMessage(SnipeData snipeData) {
        this.snipeData = snipeData;
    }

    /**
     * Send a brush message styled message to the player.
     *
     * @param brushMessage
     */
    public void brushMessage(String brushMessage) {
        snipeData.sendMessage(Component.text(brushMessage).color(NamedTextColor.LIGHT_PURPLE));
    }

    /**
     * Send a brush message styled message to the player.
     *
     * @param brushMessage
     */
    public void brushMessageError(String brushMessage) {
        snipeData.sendMessage(Component.text(brushMessage).color(NamedTextColor.RED));
    }

    /**
     * Send a brush message styled message to the player.
     */
    public void brushMessageError() {
        brushMessageError("An error occurred");
    }

    /**
     * Display Brush Name.
     *
     * @param brushName
     */
    public void brushName(String brushName) {
        snipeData.sendMessage(Component.text("Brush Type: ").color(NamedTextColor.AQUA).append(Component.text(brushName).color(NamedTextColor.LIGHT_PURPLE)));
    }

    /**
     * Display Center Parameter.
     */
    public void center() {
        snipeData.sendMessage(Component.text("Brush Center: ").color(NamedTextColor.DARK_BLUE).append(Component.text(snipeData.getcCen()).color(NamedTextColor.DARK_RED)));
    }

    /**
     * Display custom message.
     *
     * @param message
     */
    public void custom(Component message) {
        snipeData.sendMessage(message);
    }

    /**
     * Display voxel type.
     */
    @SuppressWarnings("deprecation")
    public void voxel() {
        snipeData.sendMessage(Component.text("Voxel Material: ").color(NamedTextColor.GOLD).append(Component.text(snipeData.getVoxelMaterial().toString()).color(NamedTextColor.RED)));
    }

    /**
     * Display data value.
     */
    public void data() {
        snipeData.sendMessage(Component.text("Voxel Data Value: ").color(NamedTextColor.DARK_BLUE).append(Component.text(snipeData.getVoxelSubstance().getAsString()).color(NamedTextColor.DARK_RED)));
    }

    /**
     * Display voxel height.
     */
    public void height() {
        snipeData.sendMessage(Component.text("Brush Height: ").color(NamedTextColor.DARK_AQUA).append(Component.text(snipeData.getVoxelHeight()).color(NamedTextColor.DARK_RED)));
    }

    public void invalidUseParameter(String triggerHandle) {
        snipeData.sendMessage(Component.text("Invalid parameter! Use ").color(NamedTextColor.RED).append(Component.text("'/b " + triggerHandle + " info'").color(NamedTextColor.LIGHT_PURPLE)).append(Component.text(" to display valid parameters.")));
    }

    /**
     * Display performer.
     *
     * @param performerName
     */
    public void performerName(String performerName) {
        this.snipeData.sendMessage(Component.text("Performer: ").color(NamedTextColor.DARK_PURPLE).append(Component.text(performerName).color(NamedTextColor.DARK_GREEN)));
    }

    /**
     * Display replace material.
     */
    @SuppressWarnings("deprecation")
    public void replace() {
        snipeData.sendMessage(Component.text("Replace Target Material: ").color(NamedTextColor.AQUA).append(Component.text(snipeData.getReplaceMaterial().toString()).color(NamedTextColor.RED)));
    }

    /**
     * Display replace data value.
     */
    public void replaceData() {
        snipeData.sendMessage(Component.text("Replace Target Data Value: ").color(NamedTextColor.DARK_GRAY).append(Component.text(snipeData.getReplaceSubstance().toString()).color(NamedTextColor.DARK_RED)));
    }

    /**
     * Display brush size.
     */
    public void size() {
        snipeData.sendMessage(Component.text("Brush Size: ").color(NamedTextColor.GREEN).append(Component.text(snipeData.getBrushSize()).color(NamedTextColor.DARK_RED)));
        if (snipeData.getBrushSize() >= BRUSH_SIZE_WARNING_THRESHOLD) {
            snipeData.sendMessage(Component.text("WARNING: Large brush size selected!").color(NamedTextColor.RED));
        }
    }

    /**
     * Display toggle lightning message.
     */
    public void toggleLightning() {
        snipeData.sendMessage(toggle("Lightning mode has been toggled ", ((snipeData.owner().getSnipeData(snipeData.owner().getCurrentToolId()).isLightningEnabled()) ? "on" : "off")));
    }

    /**
     * Display toggle printout message.
     */
    public final void togglePrintout() {
        snipeData.sendMessage(toggle("Brush info printout mode has been toggled ", ((snipeData.owner().getSnipeData(snipeData.owner().getCurrentToolId()).isLightningEnabled()) ? "on" : "off")));
    }

    /**
     * Display toggle range message.
     */
    public void toggleRange() {
        snipeData.sendMessage(toggle("Distance Restriction toggled ", ((snipeData.owner().getSnipeData(snipeData.owner().getCurrentToolId()).isRanged()) ? "on" : "off"))
                .append(Component.text(". Range is ").color(NamedTextColor.GOLD))
                .append(Component.text((double) snipeData.owner().getSnipeData(snipeData.owner().getCurrentToolId()).getRange()).color(NamedTextColor.LIGHT_PURPLE))
        );
    }

    private Component toggle(String first, String second) {
        return Component.empty().append(Component.text(first).color(NamedTextColor.GOLD).append(Component.text(second).color(NamedTextColor.DARK_RED)));
    }

    /**
     * Display voxel list.
     */
    public void voxelList() {
        if (snipeData.getVoxelList().isEmpty()) {
            snipeData.sendMessage(Component.text("No blocks selected!").color(NamedTextColor.DARK_GREEN));
        } else {
            snipeData.sendMessage(Component.empty()
                    .append(Component.text("Block Types Selected: ").color(NamedTextColor.DARK_GREEN))
                    .append(Component.text(snipeData.getVoxelList().getList().stream().map(e -> e.getKey().toString()).collect(Collectors.joining(","))).color(NamedTextColor.DARK_AQUA))
            );
        }
    }

    public void commandParameters(@Nullable String header, @Nullable Component footer, String ... parameters) {
        var comp = Component.empty();

        if (header != null)
            comp = comp.append(Component.text(header).color(NamedTextColor.GOLD));

        if(parameters != null) {
            for (String parameter : parameters) {
                comp = comp.append(Component.newline())
                        .append(Component.text(parameter).color(NamedTextColor.AQUA));
            }
        }

        if(footer != null)
            comp = comp.append(Component.newline().append(footer));

        snipeData.sendMessage(comp);
    }
}
