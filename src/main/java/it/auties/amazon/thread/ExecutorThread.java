package it.auties.amazon.thread;

import it.auties.amazon.scraper.AmazonProductFinder;

import java.util.List;

public class ExecutorThread extends Thread{
    private final List<String> titles;
    private final boolean isFirst;
    private final boolean isLast;
    private final AmazonProductFinder finder;

    public ExecutorThread(List<String> titles, boolean isFirst, boolean isLast) {
        this.titles = titles;
        this.isFirst = isFirst;
        this.isLast = isLast;
        this.finder = new AmazonProductFinder();
    }


    @Override
    public void run() {
        for(int x = 0; x < titles.size(); x++){
            finder.updateProductsWithQuery(titles.get(x), x == 0 && isFirst, x == 0 & isLast, 0);
        }
    }
}