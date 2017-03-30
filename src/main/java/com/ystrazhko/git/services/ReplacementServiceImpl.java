package com.ystrazhko.git.services;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * Class for work with mass changes in cloned files.
 *
 * @author Lyska Lyudmila
 */
class ReplacementServiceImpl implements ReplacementService {

    @Override
    public void replaceTextInFiles(String groupFolderPath, String fileName, String fromText, String toText) {
        Collection<File> listOfFolders = getAllFilesInFolder(groupFolderPath);

        for (File file : listOfFolders) {
            File foundFile = findFileInFolder(file, fileName);
            if (foundFile == null) {
                System.err.println("ERROR: File not found!");
                continue;
            }

            replaceText(foundFile, fromText, toText);
        }
    }



    private Collection<File> getAllFilesInFolder(String groupFolderPath) {
        File folder = new File(groupFolderPath);
        File[] listOfFolders = folder.listFiles();
        return listOfFolders != null ? Arrays.asList(listOfFolders) : Collections.EMPTY_LIST;
    }

    private File findFileInFolder(File folder, String fileName) {
        Collection<File> listOfFolders = getAllFilesInFolder(folder.getPath());

        for (File file : listOfFolders) {
            if (file.getName().equals(fileName)) {
                return file;
            }
        }
        return null;
    }

    private void replaceText(File file, String fromText, String toText) {
        if (fromText == null || toText == null) {
            return;
        }

        Optional<String> text = readFile(file);
        if (!text.isPresent()) {
            return;
        }

        String textFromFile = text.get();
        if(!textFromFile.contains(fromText)) {
            return;
        }

        textFromFile = textFromFile.replace(fromText, toText);
        writeFile(file, textFromFile);
    }

    private Optional<String> readFile(File file) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            StringBuilder textFile = new StringBuilder();

            String line = br.readLine();
            while (line != null) {
                textFile.append(line);

                if((line = br.readLine()) != null) {
                    textFile.append("\n");
                }
            }

            br.close();
            return Optional.of(textFile.toString());

        } catch (IOException e) {
            System.err.println("ERROR: " + e.getMessage());
            return Optional.empty();
        }
    }

    private void writeFile(File file, String data) {
        try {
            FileWriter fw = new FileWriter(file);
            fw.write(data);
            fw.close();
        } catch (IOException e) {
            System.err.println("ERROR: " + e.getMessage());
        }
    }
}
