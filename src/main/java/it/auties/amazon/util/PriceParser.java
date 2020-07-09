package it.auties.amazon.util;

import it.auties.amazon.AmazonWebScraperApplication;

public class PriceParser {
    public static int parse(String input) {
        input = input.replace("EUR ", "");
        if(input.equals("0")) return 0;
        if(input.equals("Spedizione GRATUITA")) return 0;
        try {
            var parts = input.replace(".", "").split(",");
            return Integer.parseInt(parts[0]) * 100 + Integer.parseInt(parts[1]);
        }catch (NumberFormatException ignored){
            if(AmazonWebScraperApplication.DEBUG) System.out.println("[OfferIO] Product is currently unavailable");
            return 0;
        }
    }
}