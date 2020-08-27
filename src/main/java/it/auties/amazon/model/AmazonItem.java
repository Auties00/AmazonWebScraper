package it.auties.amazon.model;

import it.auties.amazon.logger.AmazonLogger;
import it.auties.amazon.util.Color;
import it.auties.amazon.telegram.TelegramMessageHandler;
import lombok.Data;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

@Data
public class AmazonItem {
    private final String name;
    private final String asin;
    private final List<Price<Integer>> prices;
    private final String url;
    private final String image;
    private String status;
    private String seller;

    public AmazonItem(String name, String asin, String url, String image){
        this.name = name;
        this.asin = asin;
        this.url = url;
        this.image = image;
        this.prices = new ArrayList<>();
    }

    public synchronized void addPrice(Price<Integer> price) throws IOException, URISyntaxException {
        if (prices.size() > 0) {
            var last = prices.get(prices.size() - 1);
            double lastPrice = last.getItem() + last.getShipping();
            double nextPrice = price.getItem() + price.getShipping();
            if (lastPrice > nextPrice) {
                var percentage = ((lastPrice - nextPrice) / lastPrice) * 100D;
                if(percentage >= 10){
                    AmazonLogger.info( "[TelegramIO] Sent message", Color.GREEN);
                    TelegramMessageHandler.getInstance().sendMessage(name, url, image, price.getItem(), price.getShipping(), status, seller);
                }

                TelegramMessageHandler.getInstance().sendMessage(name, url, image, price.getItem(), price.getShipping(), status, seller);
                AmazonLogger.info( "[OfferIO] A new offer was found for %s with a %s euro price difference(%s%) by the assigned thread with link %s".formatted(name, (lastPrice - nextPrice) / 100D, percentage, url), Color.GREEN);
            } else if(lastPrice < nextPrice) {
                AmazonLogger.info(Color.RED + "[OfferIO] The last offer(%s) for %s is no longer available and the price increased by %s euro. Link: %s".formatted(lastPrice, name, (nextPrice - lastPrice) / 100D, (nextPrice - lastPrice) / 100D, url), Color.RED);
            }
        } else {
            AmazonLogger.log( "[OfferIO] Added new product to watch list from thread with name %s".formatted(name), Color.MAGENTA);
        }

        prices.add(price);
    }
}