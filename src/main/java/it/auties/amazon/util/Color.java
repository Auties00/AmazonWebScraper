package it.auties.amazon.util;

public enum Color {
    RESET("\033[0m"),
    RED("\033[0;31m"),
    GREEN("\033[0;32m"),
    MAGENTA("\033[0;35m"),
    WHITE("\033[0;37m");

    private final String code;

    Color(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return code;
    }
}