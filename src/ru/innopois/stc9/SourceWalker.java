package ru.innopois.stc9;

/**
 * Reads and searches through different types of sources.
 */

import org.apache.log4j.Logger;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Pattern;

public class SourceWalker implements Runnable {
    final static Logger logger = Logger.getLogger("console");
    final static Logger errorLogger = logger.getLogger("error");
    //The pattern works in lots of cases, but it extremely slows down the code.
    //Pattern END_OF_SENTENCE = Pattern.compile("(?<=\\!\\?\\.|\\?\\!\\.|\\?\\.\\.|\\!\\.\\.|\\‽\\.\\.|\\.\\.\\.|\\?\\!|\\!\\?|\\.|\\…|\\!|\\?|\\‽)");
    Pattern END_OF_SENTENCE = Pattern.compile("(?<=\\!|\\?|\\.)");
    private SearchResultKeeper resultKeeper;
    private String source;
    private String[] words;
    private String text;

    public SourceWalker(SearchResultKeeper resultKeeper, String source, String[] words) {
        this.resultKeeper = resultKeeper;
        this.source = source;
        this.words = words;
    }

    @Override
    public void run() {
        logger.info("Thread for source" + source + "started");
        //System.out.println("Thread for source " + source + " started");
        readSource();
        getSentences(text, words);
        logger.info("Thread for source " + source + " finished");
    }

    /**
     * Reads text and finds sentences which contain given words
     * @param text a <tt>String</tt> object with text to look through
     * @param words a <tt>String</tt> array with words to find
     */
    private void getSentences(String text, String[] words) {
        for (String sentence : END_OF_SENTENCE.split(text)) {
            if (words.length > 0) {
                for (String word : words) {
                    if (word!=null && word.length()>0) {
                        String lcword = word.toLowerCase();
                        if (sentence.toLowerCase().contains(lcword)) {
                            if (sentence.indexOf(lcword) > 0) {
                                //Determine chars before and after founded word.
                                Character previousChar = sentence.charAt(sentence.indexOf(lcword) - 1);
                                Character followingChar = sentence.charAt(sentence.indexOf(lcword) + lcword.length());
                                if (!(previousChar.toString().matches("[\\wА-яЁё]")) && !(followingChar.toString().matches("[\\wА-яЁё]"))) {
                                    resultKeeper.add(sentence.trim());
                                }
                            }
                        }
                    } else {
                        errorLogger.error("Invalid input.");
                    }
                }
            } else {
                errorLogger.error("Invalid input.");
            }
        }
    }

    /**
     * Returns appropriate BufferedReader
     * @param source
     * @return <tt>BufferedReader</tt> object
     */
    private BufferedReader getSourceReader(String source) {
        if (source.startsWith("http") || source.startsWith("ftp")) {
            return webReader(source);
        }
        if (source.matches("\\w{1}:\\\\.*")) {
            return fileReader(source);
        }
        return null;
    }

    /**
     * Creates BufferedReader for sources stored at local drive.
     * @param source a <tt>String</tt> object
     * @return <tt>BufferedReader</tt> object
     */
    private BufferedReader fileReader(String source) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(source), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            errorLogger.error("Invalid source. ", e);
            //System.out.println("Source reading error.");
            //e.printStackTrace();
        } catch (FileNotFoundException e) {
            errorLogger.error("Invalid source. ", e);
        }
        return reader;
    }

    /**
     * Creates BufferedReader for web or "file://" sources.
     * @param source a <tt>String</tt> object
     * @return <tt>BufferedReader</tt> object
     */
    private BufferedReader webReader(String source) {
        BufferedReader reader = null;
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
            errorLogger.error("Invalid source. ", e);
        }
        return reader;
    }

    /**
     * Reads source with appropriate source reader.
     */
    private void readSource() {
        BufferedReader sourceReader = getSourceReader(source);
        StringBuilder sb = new StringBuilder("");
        String s;
        try {
            while ((s = sourceReader.readLine()) != null) {
                sb.append(s);
                sb.append("\n");
            }
            text = sb.toString();
            sourceReader.close();
        } catch (IOException e) {
            errorLogger.error("Source reading error. ", e);
        }
    }


}
