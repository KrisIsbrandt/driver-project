package pl.coderslab.service;

import pl.coderslab.exception.ResourceNotFoundException;

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
}
