package it.auties.amazon.logger;

import it.auties.amazon.util.Color;
import lombok.NonNull;
import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import static it.auties.amazon.AmazonWebScraperApplication.DEBUG;

@UtilityClass
public class AmazonLogger {
    private final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM", Locale.ITALY);
    private final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("hh:mm:ss", Locale.ITALY);

    public void log(@NonNull String message, @NonNull Color color){
        if(!DEBUG) return;
        sharedLog(message, color);
    }

    public void info(@NonNull String message, @NonNull Color color){
        sharedLog(message, color);
    }

    private void sharedLog(@NonNull String message, @NonNull Color color){
        var now = LocalDateTime.now();
        System.out.println("%s[%s][%s]%s%s".formatted(color, DATE_FORMATTER.format(now), TIME_FORMATTER.format(now), message, Color.RESET));
    }
}
