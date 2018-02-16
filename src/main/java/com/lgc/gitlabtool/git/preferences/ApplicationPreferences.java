package com.lgc.gitlabtool.git.preferences;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public class ApplicationPreferences {

    private static final Logger _logger = LogManager.getLogger(ApplicationPreferences.class);

    private static final String GLT_PREFERENCES_NODE = "gitlab_tool_application_preferences";

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

    // TODO
    // 1. move default Preferences from ThemeServiceImpl
    // 2. Look for the Preferences usages in GTL and change it to use this class
    // 3. Think about splitting preferences on the nodes


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

    public ApplicationPreferences node(String pathName) {
        _preferences.node(pathName);
        return this;
    }

    /**
     * Stores the object as a preference
     *
     * Important: object should implement {@link Serializable} interface to may possibility to store as a preference
     *
     * @param key - key with which the specified value is to be associated
     * @param object - the object to be stored as a preference. Should implement {@link Serializable} interface
     */
    public void putObject(String key, Object object) {
        // TODO: split to the parts according to the store size
        byte[] bytes = object2Bytes(object);
        if (bytes != null) {
            _preferences.putByteArray(key, bytes);
        }
    }

    public Object getObject(String key, Object def) {
        // TODO: split to the parts according to the store size
        byte[] bytes = _preferences.getByteArray(key, null);
        if (bytes != null) {
            Object obj = bytes2Object(bytes);
            return obj != null ? obj : def;
        } else {
            return def;
        }
    }

    private static byte[] object2Bytes(Object object) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(object);
            return baos.toByteArray();
        } catch (IOException e) {
            _logger.error("Couldn't convert object to byte array: " + e.getMessage());
            return null;
        }
    }

    private static Object bytes2Object(byte[] bytes) {
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
            ObjectInputStream ois = new ObjectInputStream(bais);
            return ois.readObject();
        } catch(IOException | ClassNotFoundException e) {
            _logger.error("Couldn't convert byte array to object: " + e.getMessage());
            return null;
        }
    }

    public String[] keys() throws BackingStoreException {
        return _preferences.keys();
    }

    public String absolutePath() {
        return _preferences.absolutePath();
    }
}
