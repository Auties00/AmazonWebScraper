package it.auties.amazon.util;

import it.auties.amazon.AmazonWebScraperApplication;
import it.auties.amazon.logger.AmazonLogger;
import it.auties.amazon.model.AmazonItem;
import it.auties.amazon.model.Price;
import lombok.experimental.UtilityClass;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.stream.Collectors;

@UtilityClass
public class PriceExtractor {
    public void extractUrlFromQuery(AmazonItem item) throws IOException, URISyntaxException {
        final var document = Jsoup
                .connect(item.getUrl())
                .ignoreContentType(true)
                .userAgent(AmazonWebScraperApplication.AGENT)
                .headers(AmazonWebScraperApplication.HEADERS)
                .referrer("http://www.google.com")
                .timeout(12000)
                .followRedirects(true)
                .get();

        if(document.html().contains("Inserisci i caratteri visualizzati nello spazio sottostante")){
            throw new IOException("Amazon banned the request!");
        }

        final var elementsContainerOptional = document
                .getElementsByClass("a-section")
                .stream()
                .filter(e -> e.hasClass("a-spacing-double-large"))
                .findFirst();

        if (elementsContainerOptional.isEmpty()) {
            AmazonLogger.log("[OfferIO] There are no offers available for %s with asin %s".formatted(item.getUrl(), item.getAsin()), Color.RED);
            return;
        }

        var elementsContainer = elementsContainerOptional.get();
        var offers = elementsContainer.children().stream().filter(e -> e.hasClass("a-row")).filter(e -> e.hasClass("a-spacing-mini")).filter(e -> e.hasClass("olpOffer")).collect(Collectors.toList());
        var offer = offers.get(0);
        var offerChildren = offer.children().stream().filter(e -> e.tagName().equals("div")).collect(Collectors.toList());
        var priceContainer = offerChildren.get(0).children();

        item.setStatus(offerChildren.get(1).child(0).child(0).html());
        item.setSeller(findInnerHtmlForItem(offerChildren.get(2)));
        item.addPrice(new Price<>(PriceParser.parse(priceContainer.get(0).html()), PriceParser.parse(findInnerHtmlForItem(priceContainer.get(priceContainer.size() - 1)))));
    }

    private String findInnerHtmlForItem(Element parent){
        return parent.children().size() > 0 ? findInnerHtmlForItem(parent.child(0)) : parent.html();
    }
}