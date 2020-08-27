import it.auties.amazon.telegram.TelegramMessageHandler;

import java.io.IOException;
import java.net.URISyntaxException;

public class TelegramTest {
    public static void main(String[] args) throws IOException, URISyntaxException {
        TelegramMessageHandler
                .getInstance()
                .sendMessage(
                        "Test",
                        "https://google.com",
                        "https://images-na.ssl-images-amazon.com/images/I/91Qg5K-cmfL._AC_SX466_.jpg",
                        200,
                        10,
                        "Nuovo",
                        ""
                );
    }
}
