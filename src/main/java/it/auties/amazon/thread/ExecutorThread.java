package it.auties.amazon.thread;

import it.auties.amazon.extractor.AmazonProductFinder;

import it.auties.amazon.util.Color;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class ExecutorThread extends Thread{
    private final List<String> titles;
    private final long start;
    private final boolean isFirst;
    private final boolean isLast;
    private final AmazonProductFinder finder = new AmazonProductFinder();

    @Override
    public void run() {
        if(isFirst) System.out.println(Color.MAGENTA + "[OfferIO] Starting to fetch offers...");
        titles.forEach(entry -> finder.updateProductsWithQuery(entry, 0));
        if(isLast) System.out.println(String.format(Color.MAGENTA + "[OfferIO] All offers were successfully fetched, it took %s seconds", (System.currentTimeMillis() - start) / 1000L));
    }
}