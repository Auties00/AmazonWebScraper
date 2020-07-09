package it.auties.amazon.extractor;

import it.auties.amazon.AmazonWebScraperApplication;
import it.auties.amazon.model.AmazonItem;
import it.auties.amazon.model.Price;
import it.auties.amazon.util.Color;
import it.auties.amazon.util.PriceParser;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import java.util.stream.Collectors;

@UtilityClass
class PriceExtractor {
    @SneakyThrows
    void extractUrlFromQuery(AmazonItem item) {
        final var document = Jsoup
                .connect(item.getUrl())
                .userAgent(AmazonWebScraperApplication.AGENT)
                .headers(AmazonWebScraperApplication.HEADERS)
                .get();

        final var elementsContainerOptional = document
                .getElementsByClass("a-section")
                .stream()
                .filter(e -> e.hasClass("a-spacing-double-large"))
                .findFirst();

        if (elementsContainerOptional.isEmpty()) {
            System.out.println(String.format(Color.RED + "[OfferIO] There are no offers available for %s with asin %s", item.getUrl(), item.getAsin()));
            return;
        }

        var elementsContainer = elementsContainerOptional.get();
        var offers = elementsContainer.children().stream().filter(e -> e.hasClass("a-row")).filter(e -> e.hasClass("a-spacing-mini")).filter(e -> e.hasClass("olpOffer")).collect(Collectors.toList());
        var offer = offers.get(0);
        var offerChildren = offer.children().stream().filter(e -> e.tagName().equals("div")).collect(Collectors.toList());
        var priceContainer = offerChildren.get(0).children();

        item.addPrice(new Price<>(PriceParser.parse(priceContainer.get(0).html()), PriceParser.parse(findInnerHtmlForItem(priceContainer.get(priceContainer.size() - 1)))));
        item.setStatus(offerChildren.get(1).child(0).child(0).html());
        item.setSeller(findInnerHtmlForItem(offerChildren.get(2)));
    }

    private String findInnerHtmlForItem(Element parent){
        return parent.children().size() > 0 ? findInnerHtmlForItem(parent.child(0)) : parent.html();
    }
}