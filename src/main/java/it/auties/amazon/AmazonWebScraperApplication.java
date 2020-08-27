package it.auties.amazon;

import it.auties.amazon.logger.AmazonLogger;
import it.auties.amazon.thread.ExecutorThread;
import it.auties.amazon.util.Color;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

import java.util.Objects;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class AmazonWebScraperApplication {
    public static final Map<String, String> HEADERS = Map.of(
            "Accept-Encoding", "gzip, deflate",
            "Accept", "text/html",
            "DNT", "1",
            "Connection", "close",
            "Upgrade-Insecure-Requests", "1",
            "Cache-Control", "max-age=0",
            "rtt", "50",
            "downlink", "10"
    );

    public static final String AGENT =  "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/84.0.4147.135 Safari/537.36 Edg/84.0.522.63";
    public static long START = -1;
    public static boolean DEBUG = true;
    private static final int TIME = 10;
    private static final int DIVIDER = 2;

    public static void main(String[] args) throws IOException {
        final var scanner = new Scanner(System.in);

        AmazonLogger.log("[AmazonIO] Welcome to the offers finder!", Color.WHITE);

        AmazonLogger.log("[AmazonIO] Do you need debug messages? Press y for yes or anything else for no: ", Color.WHITE);
        DEBUG = Objects.requireNonNullElse(scanner.next(), "").equalsIgnoreCase("y");
        AmazonLogger.log("[AmazonIO] Debug status: %s".formatted(DEBUG ? "on" : "off"), Color.WHITE);

        if(DEBUG) AmazonLogger.log("[FileIO] Initializing products list from default file", Color.WHITE);

        final var inputs = new File(System.getProperty("user.home"), "input.txt");
        if(!inputs.exists()){
            AmazonLogger.log("[Error] The input file(%s) doesn't exist".formatted(inputs.getPath()), Color.RED);
            return;
        }

        final var products = Files.lines(Paths.get(new File(System.getProperty("user.home"), "input.txt").toURI())).collect(Collectors.toList());
        if(products.size() == 0){
            AmazonLogger.log("[Error] The input file(%s) doesn't contain any data".formatted(inputs.getPath()), Color.RED);
            return;
        }

        if(products.size() % 2 != 0){
            AmazonLogger.log("[Error] The input file(%s) contains an invalid amount of items(%s) for the default divider(%s)".formatted(inputs.getPath(), products.size(), DIVIDER), Color.RED);
            return;
        }

        AmazonLogger.log("[FileIO] " + products.size() + " products were found, starting server...", Color.WHITE);
        AmazonLogger.log("[SchedulerIO] Starting executor...", Color.WHITE);

        var executor = Executors.newScheduledThreadPool(products.size() / DIVIDER, Thread::new);
        AmazonLogger.log("[SchedulerIO] Executor started with " + products.size() / DIVIDER + " threads", Color.WHITE);

        for(int x = 0; x < products.size() / DIVIDER; x++){
            executor.scheduleAtFixedRate(new ExecutorThread(products.subList(x * DIVIDER, (x + 1) * DIVIDER), x == 0, x + 1 == products.size() / DIVIDER), 0, TIME, TimeUnit.MINUTES);
        }
    }
}