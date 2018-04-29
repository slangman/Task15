package ru.innopois.stc9;

import java.io.*;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) throws IOException {
        SearchSingleThread searchSingleThread = new SearchSingleThread();
        SearchImpl searchImpl = new SearchImpl();
        String result = "c:\\task7\\result_file.txt";
        String result1 = "c:\\task7\\result_file_1.txt";

        String[] sourceList = new String[]{"c:\\task7\\1.txt", "c:\\task7\\random3.txt", "c:\\task7\\2.txt"};
        String[] wordsToFind = new String[]{"бог", "сон", "лес"};

        //Testing single-thread solution. Do not run it with huge files!
        /*
        System.out.println("Run in single thread");
        long startTime = System.currentTimeMillis();
        searchSingleThread.getOccurencies(sourceList, wordsToFind, result);
        long elapsedTime = System.currentTimeMillis()-startTime;
        System.out.println("Elapsed time: " + elapsedTime + " ms");
        */

        //Testing a big amount of small files.
        /*
        BufferedReader fileReader = new BufferedReader(new InputStreamReader(new FileInputStream("c:\\task7\\tempFilesList.txt")));
        String s;
        ArrayList<String> smallFiles = new ArrayList<>();
        while ((s = fileReader.readLine()) != null) {
            smallFiles.add(s);
        }
        String[] smallFilesArray = new String[smallFiles.size()];
        for (int i = 0; i < smallFiles.size(); i++) {
            smallFilesArray[i] = smallFiles.get(i);
            System.out.println(smallFilesArray[i]);
        }
        long startTime = System.currentTimeMillis();
        searchSingleThread.getOccurencies(smallFilesArray, wordsToFind, result);
        long elapsedTime = System.currentTimeMillis()-startTime;
        System.out.println("Elapsed time: " + elapsedTime + " ms");
        */

        //Testing different sources (small files, big files, web, ftp)
        //System.out.println("Run multithreading");
        long startTime1 = System.currentTimeMillis();
        searchImpl.getOccurencies(sourceList, wordsToFind, result1);
        long elapsedTime1 = System.currentTimeMillis() - startTime1;
        //System.out.println("Elapsed time: " + (int) elapsedTime1 / 1000 + " seconds");

    }
}
