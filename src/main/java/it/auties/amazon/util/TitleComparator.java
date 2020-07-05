package it.auties.amazon.util;

public class TitleComparator {
    private static boolean shouldExclude(String product, String match){
        return !match.contains(product);
    }

    public static boolean parseProductTitles(String product, String match){
        return shouldExclude(parseProductTitle(product), parseProductTitle(match));
    }

    private static String parseProductTitle(String title){
        return title
                .toLowerCase()
                .replaceAll(" ", "")
                .replaceAll("[-+.^:,]","");
    }
}
