package it.auties.amazon;

import it.auties.amazon.thread.ExecutorThread;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

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

    public static String AGENT =  "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/83.0.4103.116 Safari/537.36";
    public static boolean DEBUG = true;
    private static final int TIME = 10;
    private static final int DIVIDER = 5;

    public static void main(String[] args) throws Exception {
        final var scanner = new Scanner(System.in);

        System.out.println("[AmazonIO] Welcome to the offers finder!");

        System.out.println("[AmazonIO] Do you need debug messages? Press y for yes or anything else for no: ");
        DEBUG = scanner.next().equalsIgnoreCase("y");
        System.out.println(String.format("[AmazonIO] Debug status: %s", DEBUG ? "on" : "off"));

        if(DEBUG) System.out.println("[FileIO] Initializing products list from default file");

        final var products = Files.lines(Paths.get(new File(System.getProperty("user.home"), "input.txt").toURI())).collect(Collectors.toList());
        if(DEBUG) System.out.println("[FileIO] " + products.size() + " products were found, starting server...");
        if(DEBUG) System.out.println("[SchedulerIO] Starting executor...");

        var executor = Executors.newScheduledThreadPool(products.size() / DIVIDER, Thread::new);
        if(DEBUG) System.out.println("[SchedulerIO] Executor started with " + products.size() / DIVIDER + " threads");
        final var start = System.currentTimeMillis();

        for(int x = 0; x < products.size() / DIVIDER; x++){
            executor.scheduleAtFixedRate(new ExecutorThread(products.subList(x * DIVIDER, (x + 1) * DIVIDER), start, x == 0, x + 1 == products.size() / DIVIDER), 0, TIME, TimeUnit.MINUTES);
        }
    }
}