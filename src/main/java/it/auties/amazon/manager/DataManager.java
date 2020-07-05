package it.auties.amazon.manager;

import it.auties.amazon.model.AmazonItemContainer;

import java.util.HashSet;
import java.util.Set;


public class DataManager {
    private static DataManager instance;
    private final Set<AmazonItemContainer> items;

    private DataManager() {
        this.items = new HashSet<>();
    }

    public static DataManager getInstance() {
        if (instance == null) {
            instance = new DataManager();
        }

        return instance;
    }

    public void addItem(AmazonItemContainer item) {
        items.add(item);
    }

    public void removeItem(AmazonItemContainer item) {
        items.remove(item);
    }

    public AmazonItemContainer getByName(String name) {
        return items.stream().filter(e -> e.getName().equals(name)).findFirst().orElse(null);
    }
}