package com.lgc.gitlabtool.git.preferences;

import com.lgc.gitlabtool.git.services.Service;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 * Application preferences for GitlabTool application
 *
 * @see Preferences
 *
 * @author Igor Khlaponin
 */
public class ApplicationPreferences implements Service {

    private static final Logger _logger = LogManager.getLogger(ApplicationPreferences.class);

    /**
     * Parent root node for the whole application. It contains all of the preferences nodes
     * and couldn't be changed
     */
    private final Preferences parentPrefs;

    /**
     * Current node. Changes each time we call {@link #node(String)} method
     */
    private Preferences currentNode;

    public ApplicationPreferences() {
        parentPrefs = Preferences.userRoot().node(PreferencesNodes.GLT_PREFERENCES_NODE);
        currentNode = parentPrefs;
    }

    /**
     * Restores default settings
     */
    public void restoreDefaults() {
        try {
            for (String name : parentPrefs.childrenNames()) {
                parentPrefs.node(name).removeNode();
            }
        } catch (BackingStoreException e) {
            _logger.error("Could not restore defaults: " + e.getMessage());
        }
    }


    /**
     * Delegated methods
     */

    public String get(String key, String def) {
        return getCurrentNode().get(key, def);
    }

    public void put(String key, String value) {
        getCurrentNode().put(key, value);
    }

    public void remove(String key) {
        getCurrentNode().remove(key);
    }

    public void putInt(String key, int value) {
        getCurrentNode().putInt(key, value);
    }

    public int getInt(String key, int def) {
        return getCurrentNode().getInt(key, def);
    }

    public void putLong(String key, long value) {
        getCurrentNode().putLong(key, value);
    }

    public long getLong(String key, long def) {
        return getCurrentNode().getLong(key, def);
    }

    public void putBoolean(String key, boolean value) {
        getCurrentNode().putBoolean(key, value);
    }

    public boolean getBoolean(String key, boolean def) {
        return getCurrentNode().getBoolean(key, def);
    }

    public void putFloat(String key, float value) {
        getCurrentNode().putFloat(key, value);
    }

    public float getFloat(String key, float def) {
        return getCurrentNode().getFloat(key, def);
    }

    public void putDouble(String key, double value) {
        getCurrentNode().putDouble(key, value);
    }

    public double getDouble(String key, double def) {
        return getCurrentNode().getDouble(key, def);
    }

    public void putByteArray(String key, byte[] value) {
        getCurrentNode().putByteArray(key, value);
    }

    public byte[] getByteArray(String key, byte[] def) {
        return getCurrentNode().getByteArray(key, def);
    }

    public String[] keys() throws BackingStoreException {
        return getCurrentNode().keys();
    }

    public String absolutePath() {
        return getCurrentNode().absolutePath();
    }

    /**
     * Set the current node for preferences
     *
     * @param pathName - name of current node or a path for it
     * @return this instance of {@link ApplicationPreferences}
     */
    public ApplicationPreferences node(String pathName) {
        try {
            currentNode = parentPrefs.node(pathName);
            return this;
        } catch (IllegalArgumentException iae) {
            _logger.error("Consecutive slashes in path");
            return null;
        } catch (IllegalStateException ise) {
            _logger.error("Node has been removed with the removeNode() method");
            return null;
        } catch (NullPointerException npe) {
            _logger.error("Key is null");
            return null;
        }
    }

    private Preferences getCurrentNode() {
        return currentNode;
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
            getCurrentNode().putByteArray(key, bytes);
        }
    }

    /**
     * Returns the object from the preferences
     *
     * @param key - key with which the specified value is to be associated
     * @param def - the default value that should be used if preferences don't contain such key
     * @return the object from preferences
     */
    public Object getObject(String key, Object def) {
        // TODO: split to the parts according to the store size
        byte[] bytes = getCurrentNode().getByteArray(key, null);
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

}
