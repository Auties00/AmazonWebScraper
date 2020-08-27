package it.auties.amazon.util;

import it.auties.amazon.logger.AmazonLogger;
import lombok.experimental.UtilityClass;

@UtilityClass
public class PriceParser {
    public int parse(String input) {
        input = input.replace("EUR ", "");
        if(input.equals("0") || input.equals("")) return 0;
        if(input.equals("Spedizione GRATUITA")) return 0;
        try {
            var parts = input.replace(".", "").split(",");
            return Integer.parseInt(parts[0]) * 100 + Integer.parseInt(parts[1]);
        }catch (NumberFormatException ignored){
            AmazonLogger.log("[OfferIO] Product is currently unavailable for input %s".formatted(input), Color.WHITE);
            return 0;
        }
    }
}