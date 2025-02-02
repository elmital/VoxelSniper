package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.VoxelMessage;
import com.thevoxelbox.voxelsniper.VoxelSniper;
import com.thevoxelbox.voxelsniper.snipe.SnipeData;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.ChatColor;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

/**
 * Overwrites signs.
 *
 * @author Monofraps
 */
public class SignOverwriteBrush extends Brush {

    private static final int MAX_SIGN_LINE_LENGTH = 15;
    private static final int NUM_SIGN_LINES = 4;
    // these are no array indices
    private static final int SIGN_LINE_1 = 1;
    private static final int SIGN_LINE_2 = 2;
    private static final int SIGN_LINE_3 = 3;
    private static final int SIGN_LINE_4 = 4;
    private final String[] signTextLines = new String[NUM_SIGN_LINES];
    private final boolean[] signLinesEnabled = new boolean[NUM_SIGN_LINES];
    private boolean rangedMode = false;

    /**
     *
     */
    public SignOverwriteBrush() {
        this.setName("Sign Overwrite Brush");

        clearBuffer();
        resetStates();
    }

    /**
     * Sets the text of a given sign.
     *
     * @param sign
     */
    private void setSignText(final Sign sign) {
        for (int i = 0; i < this.signTextLines.length; i++) {
            if (this.signLinesEnabled[i]) {
                sign.setLine(i, this.signTextLines[i]);
            }
        }

        sign.update();
    }

    /**
     * Sets the text of the target sign if the target block is a sign.
     *
     * @param v
     */
    private void setSingle(final SnipeData v) {
        if (this.getTargetBlock().getState() instanceof Sign) {
            setSignText((Sign) this.getTargetBlock().getState());
        } else {
            v.getVoxelMessage().brushMessageError("Target block is not a sign.");
        }
    }

    /**
     * Sets all signs in a range of box{x=z=brushSize*2+1 ; z=voxelHeight*2+1}.
     *
     * @param v
     */
    private void setRanged(final SnipeData v) {
        final int minX = getTargetBlock().getX() - v.getBrushSize();
        final int maxX = getTargetBlock().getX() + v.getBrushSize();
        final int minY = getTargetBlock().getY() - v.getVoxelHeight();
        final int maxY = getTargetBlock().getY() + v.getVoxelHeight();
        final int minZ = getTargetBlock().getZ() - v.getBrushSize();
        final int maxZ = getTargetBlock().getZ() + v.getBrushSize();

        boolean signFound = false; // indicates whether or not a sign was set

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    BlockState blockState = this.getWorld().getBlockAt(x, y, z).getState();
                    if (blockState instanceof Sign) {
                        setSignText((Sign) blockState);
                        signFound = true;
                    }
                }
            }
        }

        if (!signFound) {
            v.getVoxelMessage().brushMessageError("Did not found any sign in selection box.");
        }
    }

    @Override
    protected final void arrow(final SnipeData v) {
        if (this.rangedMode) {
            setRanged(v);
        } else {
            setSingle(v);
        }
    }

    @Override
    protected final void powder(final SnipeData v) {
        if (this.getTargetBlock().getState() instanceof Sign sign) {
            for (int i = 0; i < this.signTextLines.length; i++) {
                if (this.signLinesEnabled[i]) {
                    this.signTextLines[i] = sign.getLine(i);
                }
            }

            displayBuffer(v);
        } else {
            v.getVoxelMessage().brushMessageError("Target block is not a sign.");
        }
    }

    @Override
    // TODO: Rewrite this
    public final void parseParameters(final String triggerHandle, final String[] params, final SnipeData v) {
        boolean textChanged = false;

        for (int i = 0; i < params.length; i++) {
            String parameter = params[i];

            try {
                if (parameter.equalsIgnoreCase("info")) {
                    v.getVoxelMessage().commandParameters("Sign Overwrite Brush Powder/Arrow:"
                            , Component.text("The arrow writes the internal line buffer to the tearget sign.").color(NamedTextColor.BLUE).append(Component.text("The powder reads the text of the target sign into the internal buffer."))
                            , "-1[:(enabled|disabled)] ... -- Sets the text of the first sign line. (e.g. -1 Blah Blah)"
                            , "-2[:(enabled|disabled)] ... -- Sets the text of the second sign line. (e.g. -2 Blah Blah)"
                            , "-3[:(enabled|disabled)] ... -- Sets the text of the third sign line. (e.g. -3 Blah Blah)"
                            , "-4[:(enabled|disabled)] ... -- Sets the text of the fourth sign line. (e.g. -4 Blah Blah)"
                            , "-clear -- Clears the line buffer. (Alias: -c)"
                            , "-clearall -- Clears the line buffer and sets all lines back to enabled. (Alias: -ca)"
                            , "-multiple [on|off] -- Enables or disables ranged mode. (Alias: -m) (see Wiki for more information)"
                            , "-save (name) -- Save your buffer to a file named [name]. (Alias: -s)"
                            , "-open (name) -- Loads a buffer from a file named [name]. (Alias: -o)"
                    );
                } else if (parameter.startsWith("-1")) {
                    textChanged = true;
                    i = parseSignLineFromParam(params, SIGN_LINE_1, v, i);
                } else if (parameter.startsWith("-2")) {
                    textChanged = true;
                    i = parseSignLineFromParam(params, SIGN_LINE_2, v, i);
                } else if (parameter.startsWith("-3")) {
                    textChanged = true;
                    i = parseSignLineFromParam(params, SIGN_LINE_3, v, i);
                } else if (parameter.startsWith("-4")) {
                    textChanged = true;
                    i = parseSignLineFromParam(params, SIGN_LINE_4, v, i);
                } else if (parameter.equalsIgnoreCase("-clear") || parameter.equalsIgnoreCase("-c")) {
                    clearBuffer();
                    v.sendMessage(Component.text("Internal text buffer cleard.").color(NamedTextColor.BLUE));
                } else if (parameter.equalsIgnoreCase("-clearall") || parameter.equalsIgnoreCase("-ca")) {
                    clearBuffer();
                    resetStates();
                    v.sendMessage(Component.text("Internal text buffer cleard and states back to enabled.").color(NamedTextColor.BLUE));
                } else if (parameter.equalsIgnoreCase("-multiple") || parameter.equalsIgnoreCase("-m")) {
                    if ((i + 1) >= params.length) {
                        v.getVoxelMessage().brushMessageError(String.format("Missing parameter after %s.", parameter));
                        continue;
                    }

                    rangedMode = (params[++i].equalsIgnoreCase("on") || params[++i].equalsIgnoreCase("yes"));
                    v.sendMessage(Component.text(String.format("Ranged mode is %s", (rangedMode ? "enabled" : "disabled"))).color(NamedTextColor.BLUE));
                    if (this.rangedMode) {
                        v.sendMessage(Component.text("Brush size set to ").color(NamedTextColor.GREEN).append(Component.text(v.getBrushSize()).color(NamedTextColor.RED))
                                .append(Component.newline())
                                .append(Component.text("Brush height set to ").color(NamedTextColor.AQUA).append(Component.text(v.getVoxelHeight()).color(NamedTextColor.RED)))
                        );
                    }
                } else if (parameter.equalsIgnoreCase("-save") || parameter.equalsIgnoreCase("-s")) {
                    if ((i + 1) >= params.length) {
                        v.getVoxelMessage().brushMessageError(String.format("Missing parameter after %s.", parameter));
                        continue;
                    }

                    String fileName = params[++i];
                    saveBufferToFile(fileName, v);
                } else if (parameter.equalsIgnoreCase("-open") || parameter.equalsIgnoreCase("-o")) {
                    if ((i + 1) >= params.length) {
                        v.getVoxelMessage().brushMessageError(String.format("Missing parameter after %s.", parameter));
                        continue;
                    }

                    String fileName = params[++i];
                    loadBufferFromFile(fileName, v);
                    textChanged = true;
                }
            } catch (Exception exception) {
                v.getVoxelMessage().brushMessageError(String.format("Error while parsing parameter %s", parameter));
                exception.printStackTrace();
            }
        }

        if (textChanged) {
            displayBuffer(v);
        }
    }

    /**
     * Parses parameter input text of line [param:lineNumber]. Iterates though the given array until the next top level param (a parameter which starts with a
     * dash -) is found.
     *
     * @param params
     * @param lineNumber
     * @param v
     * @param i
     * @return
     */
    private int parseSignLineFromParam(final String[] params, final int lineNumber, final SnipeData v, int i) {
        final int lineIndex = lineNumber - 1;
        final String parameter = params[i];

        boolean statusSet = false;

        if (parameter.contains(":")) {
            this.signLinesEnabled[lineIndex] = parameter.substring(parameter.indexOf(":")).equalsIgnoreCase(":enabled");
            v.sendMessage(Component.text("Line " + lineNumber + " is ").color(NamedTextColor.BLUE).append(Component.text(this.signLinesEnabled[lineIndex] ? "enabled" : "disabled").color(NamedTextColor.GREEN)));
            statusSet = true;
        }

        if ((i + 1) >= params.length) {
            // return if the user just wanted to set the status
            if (statusSet) {
                return i;
            }

            v.getVoxelMessage().brushMessageError("Warning: No text after -" + lineNumber + ". Setting buffer text to \"\" (empty string)");
            signTextLines[lineIndex] = "";
            return i;
        }

        StringBuilder newText = new StringBuilder();

        // go through the array until the next top level parameter is found
        for (i++; i < params.length; i++) {
            final String currentParameter = params[i];

            if (currentParameter.startsWith("-")) {
                i--;
                break;
            } else {
                newText.append(currentParameter).append(" ");
            }
        }

        newText = new StringBuilder(ChatColor.translateAlternateColorCodes('&', newText.toString())); //TODO find a way to produce same result with Paper/Adventure api and use it instead ChatColor

        // remove last space or return if the string is empty and the user just wanted to set the status
        if ((newText.length() > 0) && newText.toString().endsWith(" ")) {
            newText = new StringBuilder(newText.substring(0, newText.length() - 1));
        } else if (newText.length() == 0) {
            if (statusSet) {
                return i;
            }
            v.getVoxelMessage().brushMessageError("Warning: No text after -" + lineNumber + ". Setting buffer text to \"\" (empty string)");
        }

        // check the line length and cut the text if needed
        if (newText.length() > MAX_SIGN_LINE_LENGTH) {
            v.getVoxelMessage().brushMessageError("Warning: Text on line " + lineNumber + " exceeds the maximum line length of " + MAX_SIGN_LINE_LENGTH + " characters. Your text will be cut.");
            newText = new StringBuilder(newText.substring(0, MAX_SIGN_LINE_LENGTH));
        }

        this.signTextLines[lineIndex] = newText.toString();
        return i;
    }

    private void displayBuffer(final SnipeData v) {
        v.sendMessage(Component.text("Buffer text set to: ").color(NamedTextColor.BLUE));
        for (int i = 0; i < this.signTextLines.length; i++) {
            v.sendMessage((this.signLinesEnabled[i] ? Component.text("(E): ").color(NamedTextColor.GREEN) : Component.text("(D): ").color(NamedTextColor.RED)).append(Component.text(this.signTextLines[i]).color(NamedTextColor.BLACK)));
        }
    }

    /**
     * Saves the buffer to file.
     *
     * @param fileName
     * @param v
     */
    private void saveBufferToFile(final String fileName, final SnipeData v) {
        final File store = new File(VoxelSniper.getInstance().getDataFolder() + "/" + fileName + ".vsign");
        if (store.exists()) {
            v.getVoxelMessage().brushMessageError("This file already exists.");
            return;
        }

        try {
            store.createNewFile();
            FileWriter outFile = new FileWriter(store);
            BufferedWriter outStream = new BufferedWriter(outFile);

            for (int i = 0; i < this.signTextLines.length; i++) {
                outStream.write(this.signLinesEnabled[i] + "\n");
                outStream.write(this.signTextLines[i] + "\n");
            }

            outStream.close();
            outFile.close();

            v.sendMessage(Component.text("File saved successfully.").color(NamedTextColor.BLUE));
        } catch (IOException exception) {
            v.getVoxelMessage().brushMessageError("Failed to save file. " + exception.getMessage());
            exception.printStackTrace();
        }
    }

    /**
     * Loads a buffer from a file.
     *
     * @param fileName
     * @param v
     */
    private void loadBufferFromFile(final String fileName, final SnipeData v) {
        final File store = new File(VoxelSniper.getInstance().getDataFolder() + "/" + fileName + ".vsign");
        if (!store.exists()) {
            v.getVoxelMessage().brushMessageError("This file does not exist.");
            return;
        }

        try {
            FileReader inFile = new FileReader(store);
            BufferedReader inStream = new BufferedReader(inFile);

            for (int i = 0; i < this.signTextLines.length; i++) {
                this.signLinesEnabled[i] = Boolean.parseBoolean(inStream.readLine());
                this.signTextLines[i] = inStream.readLine();
            }

            inStream.close();
            inFile.close();

            v.sendMessage(Component.text("File loaded successfully.").color(NamedTextColor.BLUE));
        } catch (IOException exception) {
            v.getVoxelMessage().brushMessageError("Failed to load file. " + exception.getMessage());
            exception.printStackTrace();
        }
    }

    /**
     * Clears the internal text buffer. (Sets it to empty strings)
     */
    private void clearBuffer() {
        Arrays.fill(this.signTextLines, "");
    }

    /**
     * Resets line enabled states to enabled.
     */
    private void resetStates() {
        Arrays.fill(this.signLinesEnabled, true);
    }

    @Override
    public final void info(final VoxelMessage vm) {
        vm.brushName("Sign Overwrite Brush");

        vm.custom(Component.text("Buffer text: ").color(NamedTextColor.BLUE));
        for (int i = 0; i < this.signTextLines.length; i++) {
            vm.custom((this.signLinesEnabled[i] ? Component.text("(E): ").color(NamedTextColor.GREEN) : Component.text("(D): ").color(NamedTextColor.RED)).append(Component.text(this.signTextLines[i]).color(NamedTextColor.BLACK)));
        }

        vm.custom(Component.text(String.format("Ranged mode is %s", rangedMode ? "enabled" : "disabled")).color(NamedTextColor.BLUE));
        if (rangedMode) {
            vm.size();
            vm.height();
        }
    }

    @Override
    public String getPermissionNode() {
        return "voxelsniper.brush.signoverwrite";
    }
}
