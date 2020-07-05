package it.auties.amazon.extractor;

import it.auties.amazon.AmazonWebScraperApplication;
import it.auties.amazon.manager.UserAgentProvider;
import it.auties.amazon.model.AmazonItemOffer;
import it.auties.amazon.util.TitleComparator;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Collectors;

public class LinkExtractor {
    public AmazonItemOffer extractUrlFromQuery(String product) throws InterruptedException, NullPointerException {
        try {
            final var productUrl = String.format("https://www.amazon.it/s?k=%s&i=electronics", URLEncoder.encode(product, StandardCharsets.UTF_8));

            var doc = Jsoup
                    .connect(productUrl)
                    .userAgent(UserAgentProvider.getInstance().getAgent())
                    .headers(AmazonWebScraperApplication.HEADERS)
                    .get();

            var elementContainer = doc.getElementsByClass("s-main-slot");
            if (elementContainer.isEmpty()) {
                throw new NullPointerException("You shouldn't see this exception!");
            }

            var products = new ArrayList<AmazonItemOffer>();
            var children = elementContainer.first().children().stream().filter(e -> e.hasClass("s-result-item")).collect(Collectors.toList());
            for (var entry : children) {
                if(entry.attr("data-asin").equals("")){
                    continue;
                }

                var anchorContainerOptional = findAnchorContainerForParent(entry);
                if (anchorContainerOptional == null || anchorContainerOptional.children().size() < 2) {
                    continue;
                }

                var anchor = findAnchorForContainer(anchorContainerOptional.child(1));
                if(anchor == null){
                    continue;
                }

                var title = anchor.child(0).child(0).attr("alt");
                if(TitleComparator.parseProductTitles(product, title)){
                    if(AmazonWebScraperApplication.DEBUG) System.out.println(String.format("[OfferIO] Excluded result(%s) because of name filtering(%s)", product, title));
                    continue;
                }

                if(title.contains("waterblock")){
                    if(AmazonWebScraperApplication.DEBUG) System.out.println("[OfferIO] Excluded result because of filtering");
                    continue;
                }

                var link = "https://www.amazon.it/gp/offer-listing/" + anchor.attr("href").split("/")[3] + "/s=price-asc-ran";
                products.add(new PriceExtractor().extractUrlFromQuery(link));
            }

            if(AmazonWebScraperApplication.DEBUG) System.out.println(String.format("[OfferIO] There were %s matches for %s", products.size(), product));
            products.sort(Comparator.comparingInt(AmazonItemOffer::getItemPrice));
            if(products.isEmpty()){
                if(AmazonWebScraperApplication.DEBUG) System.out.println("[OfferIO] No products were found for " + product);
                return null;
            }

            return products.get(0);
        }catch (NullPointerException | IOException e){
            if(AmazonWebScraperApplication.DEBUG) System.out.println("[AmazonIO] Amazon banned us, changing agent...");
            UserAgentProvider.getInstance().changeAgent();
            return extractUrlFromQuery(product);
        }
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