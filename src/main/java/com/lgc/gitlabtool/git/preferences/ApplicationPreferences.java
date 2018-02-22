package com.lgc.gitlabtool.git.preferences;

import com.lgc.gitlabtool.git.services.Service;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public class ApplicationPreferences implements Service {

    private static final Logger _logger = LogManager.getLogger(ApplicationPreferences.class);

    private static final String GLT_PREFERENCES_NODE = "gitlab_tool_application_preferences";

    private final Preferences parentPrefs;

    private String currentNode = GLT_PREFERENCES_NODE;

    public ApplicationPreferences() {
        parentPrefs = Preferences.userRoot().node(GLT_PREFERENCES_NODE);
    }

    // TODO
    // Think about splitting preferences on the nodes


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

    public ApplicationPreferences node(String pathName) {
//        parentPrefs.node(pathName);
        setCurrentNode(pathName);
        return this;
    }

    private void setCurrentNode(String node) {
        this.currentNode = node;
    }

    private Preferences getCurrentNode() {
        return currentNode.equals(GLT_PREFERENCES_NODE)
                ? parentPrefs
                : parentPrefs.node(currentNode);
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

    public String[] keys() throws BackingStoreException {
        return getCurrentNode().keys();
    }

    public String absolutePath() {
        return getCurrentNode().absolutePath();
    }
}
