package it.auties.amazon.manager;

import it.auties.amazon.model.AmazonItem;

import java.util.HashSet;
import java.util.Set;

public class DataManager {
    private static DataManager instance;
    private final Set<AmazonItem> items;

    private DataManager() {
        this.items = new HashSet<>();
    }

    public synchronized static DataManager getInstance() {
        if (instance == null) {
            instance = new DataManager();
        }

        return instance;
    }

    public synchronized void addItem(AmazonItem item) {
        items.add(item);
    }

    public synchronized AmazonItem getByAsin(String asin) {
        return items.stream().filter(e -> e.getAsin().equals(asin)).findFirst().orElse(null);
    }
}