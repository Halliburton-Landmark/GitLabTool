package com.lgc.gitlabtool.git.preferences;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.awt.*;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 * Tests for {@link ApplicationPreferences} class
 *
 * @author Igor Khlaponin
 */
public class ApplicationPreferencesTest {

    private Preferences parentPrefsMock;
    private ApplicationPreferences applicationPrefs;

    private String key = "Yoda";
    private String value = "Master";
    private String defaultValue = "Jedi";

    @Before
    public void init() {
        parentPrefsMock = Mockito.mock(Preferences.class);
        applicationPrefs = new ApplicationPreferences(parentPrefsMock);
    }

    @After
    public void clear() {
        parentPrefsMock = null;
        applicationPrefs = null;
    }

    @Test
    public void testIfParentNodeCreated() {
        String nodeName = "darth_vader";
        applicationPrefs.node(nodeName);

        verify(parentPrefsMock, times(1)).node(nodeName);
    }

    @Test
    public void testPutAndGetString() {
        when(parentPrefsMock.get(key, defaultValue)).thenReturn(value);

        applicationPrefs.put(key, value);
        String result = applicationPrefs.get(key, defaultValue);

        assertEquals(value, result);
    }

    @Test
    public void testGetDefaultValue() {
        when(parentPrefsMock.get(key, defaultValue)).thenReturn(defaultValue);

        String result = applicationPrefs.get(key, defaultValue);

        assertEquals(defaultValue, result);
    }

    @Test
    public void testPutAndGetInts() {
        int intValue = 1;
        int defIntValue = 0;
        when(parentPrefsMock.getInt(key, defIntValue)).thenReturn(intValue);

        applicationPrefs.putInt(key, intValue);
        int result = applicationPrefs.getInt(key, defIntValue);

        assertEquals(intValue, result);
    }

    @Test
    public void testPutAndGetLong() {
        long longValue = 1L;
        long defValue = 0L;

        when(parentPrefsMock.getLong(key, defValue)).thenReturn(longValue);

        applicationPrefs.putLong(key, longValue);
        long result = applicationPrefs.getLong(key, defValue);

        assertEquals(longValue, result);
    }

    @Test
    public void testPutAndGetFloat() {
        float floatValue = 0.3F;
        float defValue = 0.1F;

        when(parentPrefsMock.getFloat(key, defValue)).thenReturn(floatValue);

        applicationPrefs.putFloat(key, floatValue);
        float result = applicationPrefs.getFloat(key, defValue);

        assertEquals(floatValue, result, 0.01F);
    }

    @Test
    public void testPutAndGetDouble() {
        double value = 0.23;
        double defValue = 0.47;

        when(parentPrefsMock.getDouble(key, defValue)).thenReturn(value);

        applicationPrefs.putDouble(key, value);
        double result = applicationPrefs.getDouble(key, defValue);

        assertEquals(value, result, 0.01);
    }

    @Test
    public void testPutAndGetByteArray() {
        byte[] array = new byte[] {1, 2, 3};
        byte[] defValue = new byte[] {4, 5, 6};

        when(parentPrefsMock.getByteArray(key, defValue)).thenReturn(array);

        applicationPrefs.putByteArray(key, array);
        byte[] result = applicationPrefs.getByteArray(key, defValue);

        assertArrayEquals(array, result);
    }

    @Test
    public void testGetKeys() throws BackingStoreException {
        String[] keys = {"foo1", "foo2"};
        when(parentPrefsMock.keys()).thenReturn(keys);

        String[] result = applicationPrefs.keys();

        verify(parentPrefsMock, times(1)).keys();
        assertArrayEquals(keys, result);
    }

    // Uncomment if you need to test storing objects as a preference
    // Make it unit, not integration test
    /*@Test
    public void testGetAndPutObjectIntegrationTest() {
        ApplicationPreferences appPrefs = new ApplicationPreferences(Preferences.userRoot().node("test_node"));
        java.awt.Color color = new java.awt.Color(72, 255, 96);
        java.awt.Font font = new java.awt.Font("Courier", Font.PLAIN, 24);

        appPrefs.putObject("color", color);
        appPrefs.putObject("font", font);

        java.awt.Color resultColor = (java.awt.Color) appPrefs.getObject("color", null);
        java.awt.Font resultFont = (java.awt.Font) appPrefs.getObject("font", null);

        assertEquals(color, resultColor);
        assertEquals(font, resultFont);
    }*/
}
