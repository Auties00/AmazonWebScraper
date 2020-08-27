package it.auties.amazon.scraper;

import it.auties.amazon.AmazonWebScraperApplication;
import it.auties.amazon.manager.DataManager;
import it.auties.amazon.model.AmazonItem;
import it.auties.amazon.logger.AmazonLogger;
import it.auties.amazon.util.Color;
import it.auties.amazon.util.PriceExtractor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.stream.Collectors;

import static it.auties.amazon.AmazonWebScraperApplication.START;

public class AmazonProductFinder{
    private final DataManager dataManager = DataManager.getInstance();
    public void updateProductsWithQuery(String product, boolean isFirst, boolean isLast, int tries) {
        try {
            if(isFirst) {
                AmazonLogger.info( "[OfferIO] Starting to fetch offers...", Color.MAGENTA);
                START = System.currentTimeMillis();
            }


            final var productUrl = "https://www.amazon.it/s?k=%s&page=%s&i=computers".formatted(URLEncoder.encode(product, StandardCharsets.UTF_8), 1);

            final var doc = Jsoup
                    .connect(productUrl)
                    .ignoreContentType(true)
                    .userAgent(AmazonWebScraperApplication.AGENT)
                    .headers(AmazonWebScraperApplication.HEADERS)
                    .referrer("https://www.google.com")
                    .timeout(12000)
                    .followRedirects(true)
                    .get();


            if (doc.html().contains("Inserisci i caratteri visualizzati nello spazio sottostante")) {
                throw new IOException("Amazon banned the request!");
            }

            final var elementContainer = doc
                    .getElementsByClass("s-main-slot")
                    .first();

            if (elementContainer == null) {
                throw new NullPointerException("Cannot find items!");
            }

            var children = elementContainer
                    .children()
                    .stream()
                    .filter(e -> e.hasClass("s-result-item"))
                    .collect(Collectors.toList());

            for (var entry : children) {
                var asin = entry.attr("data-asin");
                if (asin.equals("")) {
                    continue;
                }

                var anchorContainerOptional = findAnchorContainerForParent(entry);
                if (anchorContainerOptional.isEmpty() || anchorContainerOptional.get().children().size() < 2) {
                    continue;
                }

                var anchor = findAnchorForContainer(anchorContainerOptional.get().child(1));
                if (anchor.isEmpty()) {
                    continue;
                }

                var img = anchor.get().child(0).child(0);
                var template = dataManager.getByAsin(asin);
                if (template == null) {
                    template = new AmazonItem(img.attr("alt"), asin, "https://www.amazon.it/gp/offer-listing/" + asin + "/condition=used/ref=olp_f_new?f_usedAcceptable=true&f_usedGood=true&f_used=true&f_usedLikeNew=true&f_usedVeryGood=true&f_new=true&tag=amazingama057-21", img.attr("src"));
                    dataManager.addItem(template);
                }


                PriceExtractor.extractUrlFromQuery(template);
            }
        }catch (Exception e) {
            AmazonLogger.info("[AmazonIO] Trying to unblock %s from amazon with error %s".formatted(product, e.getMessage()), Color.RED);
            updateProductsWithQuery(product,isFirst, isLast, tries + 1);
            return;
        }

        if(tries > 0) AmazonLogger.info( "[AmazonIO] Amazon banned us, changed agent %s times!".formatted(tries), Color.RED);
        if(isLast) AmazonLogger.info( "[OfferIO] All offers were successfully fetched, it took %s seconds".formatted((System.currentTimeMillis() - START) / 1000L), Color.MAGENTA);
    }

    private Optional<Element> findAnchorContainerForParent(Element element){
        if(element == null){
            return Optional.empty();
        }

        return element.children().size() == 2 ? Optional.of(element) : findAnchorContainerForParent(element.children().size() > 0 ? element.child(0) : null);
    }

    private Optional<Element> findAnchorForContainer(Element element){
        if(element.children().size() == 0) return Optional.empty();
        return element.tagName().equals("a") ? Optional.of(element) : findAnchorForContainer(element.child(0));
    }
}