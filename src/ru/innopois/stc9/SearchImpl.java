package ru.innopois.stc9;

import org.apache.log4j.Logger;

import java.io.*;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SearchImpl implements Search {

    SearchResultKeeper resultKeeper = new SearchResultKeeper();
    final static Logger logger = Logger.getLogger("file");
    final static Logger errorLogger = logger.getLogger("error");
    /**
     * Returns a list of sentences stored in sources which are containing one or more fords from inprut list
     * and writes these sentences in res file.
     * @param sources a <tt>String</tt> list of sources which may be a file, http or ftp source.
     * @param words a <tt>String</tt> list of words to find.
     * @param res a <tt>String</tt> object with file path to put results in.
     */
    @Override
    public void getOccurencies(String[] sources, String[] words, String res) {
        if (sources == null || sources.length == 0) {
            return;
        }
        logger.info("Search started.");
        Executor executor = Executors.newFixedThreadPool(64);
        for (String source : sources) {
            if (isHugeFile(source)) {
                HugeFileProcessor hugeFileProcessor = new HugeFileProcessor(resultKeeper, source, words);
                hugeFileProcessor.process();
            } else {
                Runnable fw = new SourceWalker(resultKeeper, source, words);
                executor.execute(fw);

            }

        }
        ((ExecutorService) executor).shutdown();
        while (!((ExecutorService) executor).isTerminated()) {
        }

        try {
            PrintWriter pw = new PrintWriter(res);
            pw.close();
            BufferedWriter fileWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(res), "UTF-8"));
            for (String sentence : resultKeeper.getResult()) {
                fileWriter.write(sentence);
                fileWriter.newLine();
                fileWriter.flush();
            }
            resultKeeper.clear();
            fileWriter.close();
            logger.info("Search finished.");
        } catch (IOException e) {
            errorLogger.error("Output stream error. ", e);
        }
    }

    /**
     * Checks if file is big enough to process it through HugeFileProcessor
     * @param source A <tt>String</tt> object with file path.
     * @return true if file bigger than 2Mb
     */
    public boolean isHugeFile(String source) {
        if (source.matches("\\w{1}:\\\\.*")) {
            File file = new File(source);
            if (file.length() > 2 * 1024 * 1024) {
                return true;
            }
        }
        return false;
    }


}
