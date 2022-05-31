package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.VoxelMessage;
import com.thevoxelbox.voxelsniper.brush.perform.PerformerBrush;
import com.thevoxelbox.voxelsniper.snipe.SnipeData;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.util.NumberConversions;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * http://www.voxelwiki.com/minecraft/Voxelsniper#Three-Point_Circle_Brush
 *
 * @author Giltwist
 */
public class ThreePointCircleBrush extends PerformerBrush {

    private Vector coordsOne;
    private Vector coordsTwo;
    private Vector coordsThree;
    private Tolerance tolerance = Tolerance.DEFAULT;

    /**
     * Default Constructor.
     */
    public ThreePointCircleBrush() {
        this.setName("3-Point Circle");
    }

    @Override
    protected final void arrow(final SnipeData v) {
        if (this.coordsOne == null) {
            this.coordsOne = this.getTargetBlock().getLocation().toVector();
            v.sendMessage(Component.text("First Corner set.").color(NamedTextColor.GRAY));
        } else if (this.coordsTwo == null) {
            this.coordsTwo = this.getTargetBlock().getLocation().toVector();
            v.sendMessage(Component.text("Second Corner set.").color(NamedTextColor.GRAY));
        } else if (this.coordsThree == null) {
            this.coordsThree = this.getTargetBlock().getLocation().toVector();
            v.sendMessage(Component.text("Third Corner set.").color(NamedTextColor.GRAY));
        } else {
            this.coordsOne = this.getTargetBlock().getLocation().toVector();
            this.coordsTwo = null;
            this.coordsThree = null;
            v.sendMessage(Component.text("First Corner set.").color(NamedTextColor.GRAY));
        }
    }

    @Override
    protected final void powder(final SnipeData v) {
        if (this.coordsOne == null || this.coordsTwo == null || this.coordsThree == null) {
            return;
        }

        // Calculate triangle defining vectors
        final Vector vectorOne = this.coordsTwo.clone();
        vectorOne.subtract(this.coordsOne);
        final Vector vectorTwo = this.coordsThree.clone();
        vectorTwo.subtract(this.coordsOne);
        final Vector vectorThree = this.coordsThree.clone();
        vectorThree.subtract(vectorTwo);

        // Redundant data check
        if (vectorOne.length() == 0 || vectorTwo.length() == 0 || vectorThree.length() == 0 || vectorOne.angle(vectorTwo) == 0 || vectorOne.angle(vectorThree) == 0 || vectorThree.angle(vectorTwo) == 0) {

            v.getVoxelMessage().brushMessageError("ERROR: Invalid points, try again.");
            this.coordsOne = null;
            this.coordsTwo = null;
            this.coordsThree = null;
            return;
        }

        // Calculate normal vector of the plane.
        final Vector normalVector = vectorOne.clone();
        normalVector.crossProduct(vectorTwo);

        // Calculate constant term of the plane.
        final double planeConstant = normalVector.getX() * this.coordsOne.getX() + normalVector.getY() * this.coordsOne.getY() + normalVector.getZ() * this.coordsOne.getZ();

        final Vector midpointOne = this.coordsOne.getMidpoint(this.coordsTwo);
        final Vector midpointTwo = this.coordsOne.getMidpoint(this.coordsThree);

        // Find perpendicular vectors to two sides in the plane
        final Vector perpendicularOne = normalVector.clone();
        perpendicularOne.crossProduct(vectorOne);
        final Vector perpendicularTwo = normalVector.clone();
        perpendicularTwo.crossProduct(vectorTwo);

        // determine value of parametric variable at intersection of two perpendicular bisectors
        final Vector tNumerator = midpointTwo.clone();
        tNumerator.subtract(midpointOne);
        tNumerator.crossProduct(perpendicularTwo);
        final Vector tDenominator = perpendicularOne.clone();
        tDenominator.crossProduct(perpendicularTwo);
        final double t = tNumerator.length() / tDenominator.length();

        // Calculate Circumcenter and Brushcenter.
        final Vector circumcenter = new Vector();
        circumcenter.copy(perpendicularOne);
        circumcenter.multiply(t);
        circumcenter.add(midpointOne);

        final Vector brushCenter = new Vector(Math.round(circumcenter.getX()), Math.round(circumcenter.getY()), Math.round(circumcenter.getZ()));

        // Calculate radius of circumcircle and determine brushsize
        final double radius = circumcenter.distance(new Vector(this.coordsOne.getX(), this.coordsOne.getY(), this.coordsOne.getZ()));
        final int brushSize = NumberConversions.ceil(radius) + 1;

        for (int x = -brushSize; x <= brushSize; x++) {
            for (int y = -brushSize; y <= brushSize; y++) {
                for (int z = -brushSize; z <= brushSize; z++) {
                    // Calculate distance from center
                    final double tempDistance = Math.pow(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2), .5);

                    // gets corner-on blocks
                    final double cornerConstant = normalVector.getX() * (circumcenter.getX() + x) + normalVector.getY() * (circumcenter.getY() + y) + normalVector.getZ() * (circumcenter.getZ() + z);

                    // gets center-on blocks
                    final double centerConstant = normalVector.getX() * (circumcenter.getX() + x + .5) + normalVector.getY() * (circumcenter.getY() + y + .5) + normalVector.getZ() * (circumcenter.getZ() + z + .5);

                    // Check if point is within sphere and on plane (some tolerance given)
                    if (tempDistance <= radius && (Math.abs(cornerConstant - planeConstant) < this.tolerance.getValue() || Math.abs(centerConstant - planeConstant) < this.tolerance.getValue())) {
                        this.currentPerformer.perform(this.clampY(brushCenter.getBlockX() + x, brushCenter.getBlockY() + y, brushCenter.getBlockZ() + z));
                    }

                }
            }
        }

        v.sendMessage(Component.text("Done.").color(NamedTextColor.GREEN));
        v.owner().storeUndo(this.currentPerformer.getUndo());

        // Reset Brush
        this.coordsOne = null;
        this.coordsTwo = null;
        this.coordsThree = null;

    }

    @Override
    public final void info(final VoxelMessage vm) {
        vm.brushName(this.getName());
        switch (this.tolerance) {
            case ACCURATE -> vm.custom(Component.text("Mode: Accurate").color(NamedTextColor.GOLD));
            case DEFAULT -> vm.custom(Component.text("Mode: Default").color(NamedTextColor.GOLD));
            case SMOOTH -> vm.custom(Component.text("Mode: Smooth").color(NamedTextColor.GOLD));
            default -> vm.custom(Component.text("Mode: Unknown").color(NamedTextColor.GOLD));
        }

    }

    @Override
    public final void parseParameters(final String triggerHandle, final String[] params, final SnipeData v) {
        if (params[0].equalsIgnoreCase("info")) {
            v.getVoxelMessage().commandParameters("Spline Brush Parameters:"
                    , Component.text("Instructions: Select three corners with the arrow brush, then generate the Circle with the powder brush.").color(NamedTextColor.BLUE)
                    , "/b " + triggerHandle + " [mode]  -- Change mode to prioritize accuracy or smoothness"
            );
            return;
        }

        try {
            this.tolerance = Tolerance.valueOf(params[0].toUpperCase());
            v.sendMessage(Component.text("Brush tolerance set to ").color(NamedTextColor.GOLD).append(Component.text(this.tolerance.name().toLowerCase()).color(NamedTextColor.YELLOW)).append(Component.text(".")));
        } catch (Exception e) {
            v.getVoxelMessage().invalidUseParameter(triggerHandle);
            sendPerformerMessage(triggerHandle, v);
        }
    }

    @Override
    public List<String> registerArguments() {
        List<String> arguments = new ArrayList<>();
        
        arguments.addAll(Arrays.stream(Tolerance.values()).map(Enum::name).toList());

        arguments.addAll(super.registerArguments());
        return arguments;
    }

    /**
     * Enumeration on Tolerance values.
     *
     * @author MikeMatrix
     */
    private enum Tolerance {
        DEFAULT(1000), ACCURATE(10), SMOOTH(2000);
        private final int value;

        Tolerance(final int value) {
            this.value = value;
        }

        public int getValue() {
            return this.value;
        }
    }

    @Override
    public String getPermissionNode() {
        return "voxelsniper.brush.threepointcircle";
    }
}
