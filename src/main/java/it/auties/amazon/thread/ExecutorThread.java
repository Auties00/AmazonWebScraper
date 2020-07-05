package it.auties.amazon.thread;

import it.auties.amazon.extractor.LinkExtractor;

import it.auties.amazon.manager.DataManager;
import it.auties.amazon.model.AmazonItemContainer;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class ExecutorThread extends Thread{
    private final List<String> titles;
    private final int id;
    private final long start;
    private final DataManager dataManager = DataManager.getInstance();
    private final LinkExtractor linkExtractor = new LinkExtractor();

    @Override
    public void run() {
        try {
            for (var entry : titles) {
                var last = dataManager.getByName(entry);
                var result = linkExtractor.extractUrlFromQuery(entry);
                if(result == null){
                    continue;
                }

                var next = new AmazonItemContainer(entry, result);
                if (last != null) {
                    var lastPrice = last.getItem().getItemPrice() + last.getItem().getShippingPrice();
                    var nextPrice = next.getItem().getItemPrice() + next.getItem().getShippingPrice();
                    if (lastPrice > nextPrice) {
                        synchronized (this) {
                            dataManager.removeItem(last);
                            dataManager.addItem(next);
                        }

                        System.out.println("[OfferIO] A new offer was found for " + entry + " with a " + ((lastPrice - nextPrice) / 100D) + " euro price difference by the assigned thread(index: " + id + ", id: " + currentThread().getId() + ") with link " + next.getItem().getUrl());
                    } else if(lastPrice < nextPrice) {
                        synchronized (this) {
                            dataManager.removeItem(last);
                            dataManager.addItem(next);
                        }

                        System.out.println("[OfferIO] The last offer for " + entry + " is no longer available and the price increased by " + ((nextPrice - lastPrice) / 100D) + " euro. Thread(index: " + id + ", id: " + currentThread().getId() + ") with link " + next.getItem().getUrl());
                    } else{
                        System.out.println("[OfferIO] No new offers were found for " + entry + " by the assigned thread(index: " + id + ", id: " + currentThread().getId() + ")" + ". Current price: " + (lastPrice / 100D) + ". Current link: " + last.getItem().getUrl());
                    }
                } else {
                    System.out.println("[OfferIO] Added new product to watch list from thread(index: " + id + ", id: " + currentThread().getId() + ") with name " + entry + " after " + (System.currentTimeMillis() - start) / 1000 + " seconds");
                    synchronized (this) {
                        dataManager.addItem(next);
                    }
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}