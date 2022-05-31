package com.thevoxelbox.voxelsniper;

import com.google.common.collect.Multimap;
import com.thevoxelbox.voxelsniper.brush.IBrush;
import com.thevoxelbox.voxelsniper.brush.perform.Performer;
import com.thevoxelbox.voxelsniper.brush.perform.PerformerBrush;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import static org.hamcrest.CoreMatchers.hasItems;

/**
 *
 */
public class BrushesTest {

    private VoxelBrushManager brushes;
    private VoxelCommandManager commands;

    @BeforeEach
    public void setUp() throws Exception {
        brushes = new VoxelBrushManager();
    }

    @Test
    public void testRegisterSniperBrush() throws Exception {
        IBrush brush = Mockito.mock(IBrush.class);
        brushes.registerSniperBrush(brush.getClass(), "mockhandle", "testhandle");
    }

    @Test
    public void testGetBrushForHandle() throws Exception {
        IBrush brush = Mockito.mock(IBrush.class);
        brushes.registerSniperBrush(brush.getClass(), "mockhandle", "testhandle");
        Assertions.assertEquals(brush.getClass(), brushes.getBrushForHandle("mockhandle"));
        Assertions.assertEquals(brush.getClass(), brushes.getBrushForHandle("testhandle"));
        Assertions.assertNull(brushes.getBrushForHandle("notExistant"));
    }

    @Test
    public void testRegisteredSniperBrushes() throws Exception {
        Assertions.assertEquals(0, brushes.registeredSniperBrushes());
        IBrush brush = Mockito.mock(IBrush.class);
        brushes.registerSniperBrush(brush.getClass(), "mockhandle", "testhandle");
        Assertions.assertEquals(1, brushes.registeredSniperBrushes());
    }

    @Test
    public void testRegisteredSniperBrushHandles() throws Exception {
        Assertions.assertEquals(0, brushes.registeredSniperBrushHandles());
        IBrush brush = Mockito.mock(IBrush.class);
        brushes.registerSniperBrush(brush.getClass(), "mockhandle", "testhandle");
        Assertions.assertEquals(2, brushes.registeredSniperBrushHandles());
    }

    @Test
    public void testGetSniperBrushHandles() throws Exception {
        IBrush brush = Mockito.mock(IBrush.class);
        brushes.registerSniperBrush(brush.getClass(), "mockhandle", "testhandle");
        Set<String> sniperBrushHandles = brushes.getSniperBrushHandles(brush.getClass());
        Assertions.assertTrue(sniperBrushHandles.contains("mockhandle"));
        Assertions.assertTrue(sniperBrushHandles.contains("testhandle"));
        Assertions.assertFalse(sniperBrushHandles.contains("notInSet"));
    }

    @Test
    public void testGetRegisteredBrushesMultimap() throws Exception {
        IBrush brush = Mockito.mock(IBrush.class);
        brushes.registerSniperBrush(brush.getClass(), "mockhandle", "testhandle");
        Multimap<Class<? extends IBrush>, String> registeredBrushesMultimap = brushes.getRegisteredBrushesMultimap();
        Assertions.assertTrue(registeredBrushesMultimap.containsKey(brush.getClass()));
        Assertions.assertFalse(registeredBrushesMultimap.containsKey(IBrush.class));
        Assertions.assertTrue(registeredBrushesMultimap.containsEntry(brush.getClass(), "mockhandle"));
        Assertions.assertTrue(registeredBrushesMultimap.containsEntry(brush.getClass(), "testhandle"));
        Assertions.assertFalse(registeredBrushesMultimap.containsEntry(brush.getClass(), "notAnEntry"));
    }

    @Test
    public void testPerformerBrushesArgumentsOverloading() throws Exception {
        // Load all brushes
        brushes = VoxelBrushManager.initialize();

        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println("======================================================================");
        System.out.println("PERFORMER ARGUMENTS TEST");
        System.out.println("HINT A:     If this test fails, you need go to registerArguments where the class is failing, and add super.registerArguments() into your own arguments list.");
        System.out.println("EXAMPLE:    arguments.addAll(super.registerArguments());");
        System.out.println();
        System.out.println("HINT Z:     If this fails, your own argument is overriding the performer arguments. Please rename your arguments to something else other than \"p\".");
        System.out.println("======================================================================");

        for (String brushHandle : brushes.getBrushHandles()) {
            Class<? extends IBrush> clazz = brushes.getBrushForHandle(brushHandle);
            var constructor = clazz.getConstructor();
            IBrush brush = constructor.newInstance();

            if (brush instanceof PerformerBrush) {
                List<String> arguments = brush.registerArguments();
                Assertions.assertTrue(arguments.contains("p"), "PERFORMER ARGUMENTS TEST: Please see the HINT A above. Failing at: " + clazz.getName());

                Assertions.assertEquals(Collections.frequency(arguments, "p"), 1,"PERFORMER ARGUMENTS TEST: Duplicate argument 'p'. Please see the HINT Z above. Failing at: " + clazz.getName());
            }
        }
        System.out.println("Performer Arguments Test OK!");
        System.out.println();
        System.out.println();
        System.out.println();
        // Unload and revert.
        brushes = new VoxelBrushManager();
    }

    @Test
    public void testPerformerBrushesArgumentValuesOverloading() throws Exception {
        // Load all brushes
        brushes = VoxelBrushManager.initialize();
        System.out.println(" ");
        System.out.println(" ");
        System.out.println(" ");
        System.out.println("======================================================================");
        System.out.println("PERFORMER ARGUMENTS VALUES TEST");
        System.out.println("HINT A:     If this fails, you need go to registerArgumentValues where the class is failing, and add super.registerArgumentValues() into your own arguments map.");
        System.out.println("EXAMPLE:    argumentValues.putAll(super.registerArgumentValues());");
        System.out.println();
        System.out.println("HINT Z:     If this fails, your own argument values are overriding the performer argument values. Please rename your arguments to something else other than \"p\".");
        System.out.println("======================================================================");

        Collection<String> performerHandles = Performer.getPerformerHandles();
        String[] performerHandlesArray = performerHandles.toArray(new String[performerHandles.size()]);

        for (String brushHandle : brushes.getBrushHandles()) {
            Class<? extends IBrush> clazz = brushes.getBrushForHandle(brushHandle);
            var constructor = clazz.getConstructor();
            IBrush brush = constructor.newInstance();

            if (brush instanceof PerformerBrush) {
                HashMap<String, List<String>> argumentValues = brush.registerArgumentValues();
                Assertions.assertTrue(argumentValues.containsKey("p"), "PERFORMER ARGUMENTS VALUES TEST: Please see the HINT A above. Failing at: " + clazz.getName());
                MatcherAssert.assertThat("PERFORMER ARGUMENTS VALUES TEST: Please see the HINT Z above. Failing at: " + clazz.getName(), argumentValues.get("p"), hasItems(performerHandlesArray));
            }
        }
        System.out.println("Performer Arguments VALUES Test OK!");
        // Unload and revert.
        brushes = new VoxelBrushManager();
    }
}
