package it.auties.amazon.model;

import it.auties.amazon.AmazonWebScraperApplication;
import it.auties.amazon.util.Color;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class AmazonItem {
    private String name;
    private String asin;
    private List<Price<Integer>> prices;
    private String url;
    private String status;
    private String seller;

    public AmazonItem(String name, String asin, String url){
        this.name = name;
        this.asin = asin;
        this.url = url;
        this.prices = new ArrayList<>();
    }

    public synchronized void addPrice(Price<Integer> price){
        if (prices.size() > 0) {
            var last = prices.get(prices.size() - 1);
            var lastPrice = last.getItem() + last.getShipping();
            var nextPrice = price.getItem() + price.getShipping();
            if (lastPrice > nextPrice) {
                System.out.println(Color.GREEN + "[OfferIO] A new offer was found for " + name + " with a " + (lastPrice - nextPrice) / 100D + " euro price difference by the assigned thread with link " + url);
            } else if(lastPrice < nextPrice) {
                System.out.println(Color.RED + "[OfferIO] The last offer(" + lastPrice + ") for " + name + " is no longer available and the price increased by " + (nextPrice - lastPrice) / 100D + " euro. Link: " + url);
            }
        } else {
            if(AmazonWebScraperApplication.DEBUG) System.out.println(Color.MAGENTA + "[OfferIO] Added new product to watch list from thread with name " + name);
        }

        prices.add(price);
    }
}