package com.oberasoftware.iot.client;

import com.google.common.base.Joiner;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class UriBuilder {
    private final List<String> pathElements = new ArrayList<>();

    private UriBuilder(String baseUrl) {
        pathElements.add(baseUrl);
        pathElements.add("api");
    }

    public static UriBuilder create(String baseUrl) {
        return new UriBuilder(baseUrl);
    }

    public UriBuilder resource(String resourceName) {
        pathElements.add(resourceName);
        return this;
    }

    public UriBuilder resource(String resourceName, String resourceKey) {
        pathElements.add(resourceName + "(" + resourceKey + ")");
        return this;
    }

    public URI build() {
        return URI.create(Joiner.on("/").join(pathElements));
    }
}
