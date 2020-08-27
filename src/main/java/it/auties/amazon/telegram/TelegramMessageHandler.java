package it.auties.amazon.telegram;

import java.io.IOException;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import com.google.gson.Gson;

public class TelegramMessageHandler {
    private static TelegramMessageHandler instance;
    private final String TELEGRAM_URL_FORMAT;
    private final String CHAT_ID;
    private final String MESSAGE_FORMAT;
    private final String API_TOKEN;

    private TelegramMessageHandler() throws IOException, URISyntaxException{
        this.TELEGRAM_URL_FORMAT = "https://api.telegram.org/bot%s/sendPhoto?chat_id=%s&photo=%s&caption=%s&reply_markup=%s";
        this.CHAT_ID = "@AmazingAmazonOfferte";
        this.MESSAGE_FORMAT = """
                %s

                ✨Condizione: %s

                \uD83D\uDCB8 Prezzo: %s€ %s

                \uD83D\uDE9A Venduto e spedito da %s

                ✅ Verified by @AmazingAmazonOfferte""";
        this.API_TOKEN = Files.readString(Paths.get(getClass().getResource("/telegram.key").toURI()));
    }

    public static TelegramMessageHandler getInstance() throws IOException, URISyntaxException {
        if(instance == null){
            instance = new TelegramMessageHandler();
        }

        return instance;
    }

    public void sendMessage(String productName, String productLink, String productImage, double productPrice, double shippingPrice, String status, String productSeller) {
        try {
            final var gson = new Gson();
            final var client = HttpClient.newHttpClient();
            final var tinyUrlRequest = HttpRequest
                    .newBuilder()
                    .GET()
                    .uri(URI.create("http://tinyurl.com/api-create.php?url=%s".formatted(URLEncoder.encode(productLink, StandardCharsets.UTF_8))))
                    .build();

            productLink = client
                    .send(tinyUrlRequest, HttpResponse.BodyHandlers.ofString())
                    .body();

            var style = new InlineKeyboardContainer(List.of(List.of(new InlineKeyboard("🔥 Compralo Ora 🔥", productLink))));
            final var url = TELEGRAM_URL_FORMAT.formatted(API_TOKEN, CHAT_ID, productImage, URLEncoder.encode(MESSAGE_FORMAT.formatted(productName, status, productPrice / 100D, shippingPrice == 0 ? "+ Spedizione Gratuita Prime" : "+ " + shippingPrice / 100D + "€ di spedizione", productSeller.equals("") ? "Amazon Wherehouse Deals" : productSeller, status, productImage), StandardCharsets.UTF_8), URLEncoder.encode(gson.toJson(style), StandardCharsets.UTF_8));
            var request = HttpRequest
                    .newBuilder()
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .uri(URI.create(url))
                    .build();

            client.sendAsync(request, HttpResponse.BodyHandlers.discarding());
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
