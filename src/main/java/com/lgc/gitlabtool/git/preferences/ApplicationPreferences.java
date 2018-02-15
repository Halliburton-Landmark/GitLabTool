package com.lgc.gitlabtool.git.preferences;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public class ApplicationPreferences {

    private static final String GLT_PREFERENCES_NODE = "GitlabTool_ApplicationPreferences";

    private final Preferences _preferences;

    private static ApplicationPreferences _instance;

    private ApplicationPreferences() {
        _preferences = Preferences.userRoot().node(GLT_PREFERENCES_NODE);
    }

    public static ApplicationPreferences getInstance() {
        if (_instance == null) {
            _instance = new ApplicationPreferences();
        }
        return _instance;
    }

    private void setUpDefauls() {
        // TODO
        // 1. set up OS
        // 2. set up UI preferences from ModularController
        // 3. set up terminal preferences
    }


    public void restoreDefaults() {
        // TODO
        // do some restore steps
    }


    /**
     * Delegated methods
     */

    public String get(String key, String def) {
        return _preferences.get(key, def);
    }

    public void put(String key, String value) {
        _preferences.put(key, value);
    }

    public void remove(String key) {
        _preferences.remove(key);
    }

    public void putInt(String key, int value) {
        _preferences.putInt(key, value);
    }

    public int getInt(String key, int def) {
        return _preferences.getInt(key, def);
    }

    public void putLong(String key, long value) {
        _preferences.putLong(key, value);
    }

    public long getLong(String key, long def) {
        return _preferences.getLong(key, def);
    }

    public void putBoolean(String key, boolean value) {
        _preferences.putBoolean(key, value);
    }

    public boolean getBoolean(String key, boolean def) {
        return _preferences.getBoolean(key, def);
    }

    public void putFloat(String key, float value) {
        _preferences.putFloat(key, value);
    }

    public float getFloat(String key, float def) {
        return _preferences.getFloat(key, def);
    }

    public void putDouble(String key, double value) {
        _preferences.putDouble(key, value);
    }

    public double getDouble(String key, double def) {
        return _preferences.getDouble(key, def);
    }

    public void putByteArray(String key, byte[] value) {
        _preferences.putByteArray(key, value);
    }

    public byte[] getByteArray(String key, byte[] def) {
        return _preferences.getByteArray(key, def);
    }

    public String[] keys() throws BackingStoreException {
        return _preferences.keys();
    }

    public String absolutePath() {
        return _preferences.absolutePath();
    }
}
