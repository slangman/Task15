package ru.innopois.stc9;

/**
 * Stores search results in temporary object.
 */

import java.util.ArrayList;

public class SearchResultKeeper {
    private ArrayList<String> result;

    public SearchResultKeeper() {
        result = new ArrayList<>();
    }

    public synchronized ArrayList<String> getResult() {
        return result;
    }

    public synchronized void add(String str) {
        result.add(str);
    }

    public synchronized void addAll(ArrayList<String> list) {
        result.addAll(list);
    }

    public void clear() {
        result.clear();
    }
}
