package com.ystrazhko.git.services;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

/**
 *
 *
 * @author Lyska Lyudmila
 */
public class ReplacementProvider {

    private static final ReplacementProvider _replacementProvider;

    static {
        _replacementProvider = new ReplacementProvider();
    }

    /**
     *
     * @return
     */
    public static ReplacementProvider getInstance() {
        return _replacementProvider;
    }

    /**
     *
     * @param groupFolderPath
     * @param fileName
     * @param fromText
     * @param toText
     */
    public void replacementTextInFiles(String groupFolderPath, String fileName, String fromText, String toText) {
        Collection<File> listOfFolders = getAllFilesInFolder(groupFolderPath);

        for (File file : listOfFolders) {
            File foundFile = findFileInFolder(file, fileName);
            if (foundFile == null) {
                System.err.println("ERROR: File not found!");
                return;
            }

            replaceText(foundFile, fromText, toText);
        }
    }



    private Collection<File> getAllFilesInFolder(String groupFolderPath) {
        File folder = new File(groupFolderPath);
        File[] listOfFolders = folder.listFiles();
        return Arrays.asList(listOfFolders);
    }

    private File findFileInFolder(File folder, String fileName) {
        Collection<File> listOfFolders = getAllFilesInFolder(folder.getPath());
        if (listOfFolders == null) {
            return null;
        }

        for (File file : listOfFolders) {
            if (file.getName().equals(fileName)) {
                return file;
            }
        }
        return null;
    }

    private void replaceText(File file, String fromText, String toText) {
        if (file == null || fromText == null || toText == null) {
            return;
        }

        String text = readFile(file);
        if (text == null) {
            return;
        }

        if(!text.contains(fromText)) {
            return;
        }
        text = text.replace(fromText, toText);
        writeFile(file, text);
    }

    private String readFile(File file) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            StringBuilder textFile = new StringBuilder();

            while ((line = br.readLine()) != null) {
                textFile.append(line);
                textFile.append("\n");
            }

            br.close();
            return textFile.toString();

        } catch (FileNotFoundException e) {
            System.err.println("ERROR: " + e.getMessage());
            return null;
        } catch (IOException e) {
            System.err.println("ERROR: " + e.getMessage());
            return null;
        }
    }

    private boolean writeFile(File file, String data) {
        if (file == null || data == null) {
            return false;
        }
        try {
            FileWriter fw = new FileWriter(file);
            fw.write(data);
            fw.close();
            return true;
        } catch (IOException e) {
            System.err.println("ERROR: " + e.getMessage());
            return false;
        }
    }
}
