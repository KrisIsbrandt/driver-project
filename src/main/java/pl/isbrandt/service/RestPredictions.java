package pl.isbrandt.service;

import pl.isbrandt.exception.ResourceNotFoundException;

public class RestPredictions {
    public static <T> T checkFound(T resource) {
        if (resource == null) {
            throw new ResourceNotFoundException("Resource not found");
        }
        return resource;
    }

    public static <T> T checkNotNull(T resource) {
        if (resource == null) {
            throw new NullPointerException("Resource is empty");
        }
        return resource;
    }

    public static String convertNewlineCharacterToHTMLBreakTag(String text) {
        String newText = text.replaceAll("(\r\n)+", "<br>");

        if (newText.contains("\r")) {
            newText = newText.replaceAll("\r+", "<br>");
        }

        if (newText.contains("\n")) {
            newText = newText.replaceAll("\n+", "<br>");
        }

        return newText;
    }
}
