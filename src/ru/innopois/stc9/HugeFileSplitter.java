package ru.innopois.stc9;

/**
 * Splits a big file in a number of temporary files
 *
 * @author Daniil Ivantsov
 * @version 1.0
 */

import org.apache.log4j.Logger;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;

public class HugeFileSplitter {
    final static Logger logger = Logger.getLogger("console");
    final static Logger errorLogger = Logger.getLogger("error");

    public final int TEMP_FILE_LENGTH = 512*1024;
    private File file;
    private ArrayList<File> files;

    public ArrayList<File> getTempFiles() {
        return files;
    }

    public File getFile() {
        return file;
    }


    public HugeFileSplitter(String fileName) {
        file = new File(fileName);
        files = new ArrayList<>();
    }

    public void split() {
        int counter = 1;
        byte[] buffer = new byte[TEMP_FILE_LENGTH];
        try(BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(file))) {
            int bytes = 0;
            while((bytes = inputStream.read(buffer)) > 0) {
                File tempFile = new File(file.getParent(), "temp" + counter + ".txt");
                files.add(tempFile);
                logger.info("file " + tempFile.getAbsolutePath() + " written");
                counter++;
                try (FileOutputStream outputStream = new FileOutputStream(tempFile)) {
                    outputStream.write(buffer, 0, bytes);
                }
            }
        } catch (IOException e) {
            errorLogger.error("Reading error. ", e);
        }
    }

    /*public void printTempFiles() {
        for (File file : files) {
            System.out.println(file.getAbsolutePath());
        }
    }*/

    public void saveTempFileNames(String outputFile) {
        BufferedWriter fileWriter = null;
        try {
            fileWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            errorLogger.error("Encoding error. ", e);
        } catch (FileNotFoundException e) {
            errorLogger.error("Reading error. ", e);
        }
        for (File tempFile : files) {
            String s = (tempFile.getAbsolutePath());
            try {
                fileWriter.write(s);
                fileWriter.newLine();
            } catch (IOException e) {
                errorLogger.error("Reading error. ", e);
            }
        }
    }

    public void deleteTempFiles() {
        for (File file : files) {
            try {
                Files.delete(file.toPath());
            } catch (IOException e) {
                errorLogger.error("Deleting error. ", e);
            }
        }
    }

}
