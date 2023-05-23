package com.oberasoftware.iot.client;

import com.google.common.base.Joiner;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UriBuilder {
    private final List<String> pathElements = new ArrayList<>();

    private final Map<String, String> queryParam = new HashMap<>();

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

    public UriBuilder param(String key, String value) {
        this.queryParam.put(key, value);
        return this;
    }

    public URI build() {
        String base = Joiner.on("/").join(pathElements);

        var queryParams = Joiner.on("&").withKeyValueSeparator("=").join(queryParam);
        return URI.create(base + "?" + queryParams);
    }
}
