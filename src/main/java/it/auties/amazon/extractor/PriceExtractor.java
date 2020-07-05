package it.auties.amazon.extractor;

import it.auties.amazon.AmazonWebScraperApplication;
import it.auties.amazon.manager.UserAgentProvider;
import it.auties.amazon.model.AmazonItemOffer;
import it.auties.amazon.util.PriceParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.stream.Collectors;

class PriceExtractor {
    AmazonItemOffer extractUrlFromQuery(String link) {
        final var start = System.currentTimeMillis();
        try {
            var document = Jsoup
                    .connect(link)
                    .userAgent(UserAgentProvider.getInstance().getAgent())
                    .headers(AmazonWebScraperApplication.HEADERS)
                    .get();

            var elementsContainerOptional = document.getElementsByClass("a-section").stream().filter(e -> e.hasClass("a-spacing-double-large")).findFirst();
            if (elementsContainerOptional.isEmpty()) {
                throw new NullPointerException("You shouldn't see this exception!");
            }

            var elementsContainer = elementsContainerOptional.get();
            var offers = elementsContainer.children().stream().filter(e -> e.hasClass("a-row")).filter(e -> e.hasClass("a-spacing-mini")).filter(e -> e.hasClass("olpOffer")).collect(Collectors.toList());
            var offer = offers.get(0);
            var offerChildren = offer.children().stream().filter(e -> e.tagName().equals("div")).collect(Collectors.toList());
            var priceContainer = offerChildren.get(0).children();
            var elementPrice = priceContainer.get(0).html();
            var shippingPrice = findInnerHtmlForItem(priceContainer.get(priceContainer.size() - 1));
            var status = offerChildren.get(1).child(0).child(0).html();
            var seller = findInnerHtmlForItem(offerChildren.get(2));
            return new AmazonItemOffer(PriceParser.parse(elementPrice.replace("EUR ", "")), PriceParser.parse(shippingPrice.contains("Spedizione GRATUITA") ? "0" : shippingPrice.replace("EUR ", "")), link, status, seller);
        }catch (IOException e){
            UserAgentProvider.getInstance().changeAgent();
            return extractUrlFromQuery(link);
        }finally {
            if(AmazonWebScraperApplication.DEBUG) System.out.println(String.format("[DebugIO] Took %s for %s", System.currentTimeMillis() - start, link));
        }
    }

    private String findInnerHtmlForItem(Element parent){
        return parent.children().size() > 0 ? findInnerHtmlForItem(parent.child(0)) : parent.html();
    }
}