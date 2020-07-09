package it.auties.amazon.extractor;

import it.auties.amazon.AmazonWebScraperApplication;
import it.auties.amazon.manager.DataManager;
import it.auties.amazon.model.AmazonItem;
import it.auties.amazon.util.Color;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class AmazonProductFinder{
    private final DataManager dataManager = DataManager.getInstance();
    public void updateProductsWithQuery(String product, int tries) {
        try {
            final var productUrl = "https://www.amazon.it/s?k=" + URLEncoder.encode(product, StandardCharsets.UTF_8);

            final var doc = Jsoup
                    .connect(productUrl)
                    .userAgent(AmazonWebScraperApplication.AGENT)
                    .headers(AmazonWebScraperApplication.HEADERS)
                    .get();

            if(doc.html().contains("Inserisci i caratteri visualizzati nello spazio sottostante")){
                throw new IOException("Amazon banned the request!");
            }

            final var elementContainer = doc
                    .getElementsByClass("s-main-slot")
                    .first();

            if(elementContainer == null){
                throw new NullPointerException("Cannot find items!");
            }

            elementContainer
                    .children()
                    .stream()
                    .filter(e -> e.hasClass("s-result-item"))
                    .forEachOrdered(entry -> {
                        var asin = entry.attr("data-asin");
                        if(asin.equals("")){
                            return;
                        }

                        var anchorContainerOptional = findAnchorContainerForParent(entry);
                        if (anchorContainerOptional == null || anchorContainerOptional.children().size() < 2) {
                            return;
                        }

                        var anchor = findAnchorForContainer(anchorContainerOptional.child(1));
                        if(anchor == null){
                            return;
                        }

                        var template = dataManager.getByAsin(asin);
                        if(template == null){
                            template = new AmazonItem(anchor.child(0).child(0).attr("alt"), asin, "https://www.amazon.it/gp/offer-listing/" + asin + "/condition=used/ref=olp_f_new?f_usedAcceptable=true&f_usedGood=true&f_used=true&f_usedLikeNew=true&f_usedVeryGood=true&f_new=true");
                            dataManager.addItem(template);
                        }

                        PriceExtractor.extractUrlFromQuery(template);
                    });
        }catch (NullPointerException | IOException e) {
            System.out.println(String.format(Color.RED + "[AmazonIO] Trying to unblock %s from amazon with error %s", product, e.getMessage()));
            updateProductsWithQuery(product,tries + 1);
            return;
        }

        if(tries > 0) System.out.println(Color.RED + "[AmazonIO] Amazon banned us, changed agent " + tries + " times!");
    }

    private Element findAnchorContainerForParent(Element element){
        if(element == null){
            return null;
        }

        return element.children().size() == 2 ? element : findAnchorContainerForParent(element.children().size() > 0 ? element.child(0) : null);
    }

    private Element findAnchorForContainer(Element element){
        if(element.children().size() == 0) return null;
        return element.tagName().equals("a") ? element : findAnchorForContainer(element.child(0));
    }
}