package ru.innopois.stc9;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class SearchSingleThread implements Search {

    @Override
    public void getOccurencies(String[] sources, String[] words, String res) {
        if (sources == null || sources.length == 0) {
            return;
        }
        ArrayList<String> result = new ArrayList<>();
        for (String source : sources) {
            if (source != null && source.length() > 0) {
                try {
                    StringBuilder text = new StringBuilder("");
                    BufferedReader fileReader = createSourceReader(source);
                    String s;
                    while ((s = fileReader.readLine()) != null) {
                        text.append(s);
                        text.append("\n");
                    }
                    fileReader.close();
                    ArrayList<String> sentences = getSentences(text.toString(), words);
                    result.addAll(sentences);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        try {
            BufferedWriter fileWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(res), "UTF-8"));
            for (String sentence : result) {
                fileWriter.write(sentence);
                fileWriter.newLine();
                fileWriter.flush();
            }
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private ArrayList<String> getSentences(String text, String[] words) {
        ArrayList<String> result = new ArrayList<>();
        Pattern END_OF_SENTENCE = Pattern.compile("(?<=\\!\\?\\.|\\?\\!\\.|\\?\\.\\.|\\!\\.\\.|\\‽\\.\\.|\\.\\.\\.|\\?\\!|\\!\\?|\\.|\\…|\\!|\\?|\\‽)");
        for (String word : words) {
            String lcword = word.toLowerCase();
            for (String sentence : END_OF_SENTENCE.split(text)) {
                if (sentence.toLowerCase().contains(lcword)) {
                    if (sentence.indexOf(lcword) > 0) {
                        //Determine chars before and after founded word.
                        Character previousChar = sentence.charAt(sentence.indexOf(lcword) - 1);
                        Character followingChar = sentence.charAt(sentence.indexOf(lcword) + lcword.length());
                        if (!(previousChar.toString().matches("[\\wА-яЁё]")) && !(followingChar.toString().matches("[\\wА-яЁё]"))) {
                            result.add(sentence.trim());
                        }
                    }
                }
            }
        }
        return result;
    }

    private BufferedReader createSourceReader(String source) {
        BufferedReader reader = null;
        if (source.startsWith("http") || source.startsWith("ftp")) {
            URL url;
            try {
                url = new URL(source);
                try {
                    reader = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));
                } catch (IOException e) {
                    System.out.println("Source reading error.");
                    e.printStackTrace();
                }
            } catch (MalformedURLException e) {
                System.out.println("Invalid source.");
                e.printStackTrace();
            }
        }
        if (source.startsWith("c:\\")) {
            try {
                reader = new BufferedReader(new InputStreamReader(new FileInputStream(source), "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                System.out.println("Source reading error.");
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                System.out.println("Invalid source.");
                e.printStackTrace();
            }
        }
        return reader;
    }

    private void findInFtp() {

    }

}
