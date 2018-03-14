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

    /**
     * Max byte count is 3/4 max string length (according to the {@link Preferences} documentation).
     */
    static private final int pieceLength = (3*Preferences.MAX_VALUE_LENGTH) / 4;

    /**
     * Creates the instance of the ApplicationPreferences
     */
    public ApplicationPreferences() {
        parentPrefs = Preferences.userRoot().node(PreferencesNodes.GLT_PREFERENCES_NODE);
        currentNode = parentPrefs;
    }

    /**
     * Creates the instance of the ApplicationPreferences based on the parent {@link Preferences}<br>
     * Important: This constructor can be used only for tests
     *
     * @param parentPrefs - preferences parent node
     */
    ApplicationPreferences(Preferences parentPrefs) {
        this.parentPrefs = parentPrefs;
        currentNode = this.parentPrefs;
    }

    /**
     * Restores default settings
     * TODO will be used in Settings UI
     */
    public void restoreDefaults() {
        try {
            parentPrefs.clear();
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
        } catch (IllegalStateException ise) {
            _logger.error("Node has been removed with the removeNode() method");
        } catch (NullPointerException npe) {
            _logger.error("Key is null");
        }
        return null;
    }

    private Preferences getCurrentNode() {
        return currentNode;
    }

    // Uncomment if you need to store the object as a preference
    /**
     * Stores the object as a preference
     *
     * Important: object should implement {@link Serializable} interface to may possibility to store as a preference
     *
     * @param key - key with which the specified value is to be associated
     * @param object - the object to be stored as a preference. Should implement {@link Serializable} interface
     *//*
    public void putObject(String key, Object object) {
        byte[] bytes = object2Bytes(object);
        if (bytes != null) {
            byte[][] pieces = breakIntoPieces(bytes);
            writePieces(getCurrentNode(), key, pieces);
        }
    }

    *//**
     * Returns the object from the preferences
     *
     * @param key - key with which the specified value is to be associated
     * @param def - the default value that should be used if preferences don't contain such key
     * @return the object from preferences
     *//*
    public Object getObject(String key, Object def) {
        byte[][] pieces = readPieces(getCurrentNode(), key);
        byte[] bytes = combinePieces(pieces);
        Object obj = bytes2Object(bytes);
        return obj != null ? obj : def;
    }

    private static byte[] object2Bytes(Object object) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(object);
            return baos.toByteArray();
        } catch (IOException e) {
            _logger.error("Couldn't convert object to byte array: " + e.getMessage());
            return null;
        }
    }

    private static Object bytes2Object(byte[] bytes) {
        String errorMessage = "Couldn't convert byte array to object: ";
        if (bytes == null) {
            throw new IllegalArgumentException(errorMessage + "bytes array cannot be null");
        }
        try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes))) {
            return ois.readObject();
        } catch(IOException | ClassNotFoundException e) {
            _logger.error(errorMessage + e.getMessage());
            return null;
        }
    }

    private static byte[][] breakIntoPieces(byte[] bytes) {
        if (bytes == null) {
            throw new IllegalArgumentException("Wrong arguments! bytes array cannot be null");
        }
        int numPieces = (bytes.length + pieceLength - 1) / pieceLength;
        byte pieces[][] = new byte[numPieces][];
        for (int i=0; i<numPieces; ++i) {
            int startByte = i * pieceLength;
            int endByte = startByte + pieceLength;
            if (endByte > bytes.length) endByte = bytes.length;
            int length = endByte - startByte;
            pieces[i] = new byte[length];
            System.arraycopy(bytes, startByte, pieces[i], 0, length);
        }
        return pieces;
    }

    private static byte[] combinePieces(byte[][] pieces) {
        if (pieces == null) {
            throw new IllegalArgumentException("Cannot combine preference pieces: bytes array cannot be null");
        }
        int length = 0;
        for (int i=0; i<pieces.length; ++i) {
            length += pieces[i].length;
        }
        byte bytes[] = new byte[length];
        int cursor = 0;
        for (int i=0; i<pieces.length; ++i) {
            System.arraycopy(pieces[i], 0, bytes, cursor, pieces[i].length);
            cursor += pieces[i].length;
        }
        return bytes;
    }

    private static byte[][] readPieces(Preferences prefs, String key) {
        try {
            Preferences node = prefs.node(key);
            String keys[] = node.keys();
            int numPieces = keys.length;
            byte pieces[][] = new byte[numPieces][];
            for (int i=0; i<numPieces; ++i) {
                pieces[i] = node.getByteArray(""+i, null);
            }
            return pieces;
        } catch (BackingStoreException e) {
            _logger.error("Couldn't read peaces: " + e.getMessage());
            return null;
        }
    }

    private static void writePieces(Preferences prefs, String key, byte[][] pieces) {
        try {
            Preferences currentNode = prefs.node(key);
            currentNode.clear();
            for (int i=0; i<pieces.length; ++i) {
                currentNode.putByteArray(""+i, pieces[i]);
            }
        } catch(BackingStoreException e) {
            _logger.error("Couldn't write peaces as a child nodes: " + e.getMessage());
        }
    }*/

}
