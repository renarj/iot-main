package com.oberasoftware.iot.client;

import org.junit.Test;

import java.net.URI;

import static org.junit.Assert.assertEquals;

public class URIBuilderTest {

    @Test
    public void testCreateBaseUri() {
        URI uri = UriBuilder.create("http://example.com").build();
        assertEquals("http://example.com/api?", uri.toString());
    }

    @Test
    public void testResourceAddition() {
        URI uri = UriBuilder.create("http://example.com")
                .resource("users")
                .build();
        assertEquals("http://example.com/api/users?", uri.toString());
    }

    @Test
    public void testResourceWithKey() {
        URI uri = UriBuilder.create("http://example.com")
                .resource("users", "123")
                .build();
        assertEquals("http://example.com/api/users(123)?", uri.toString());
    }

    @Test
    public void testQueryParam() {
        URI uri = UriBuilder.create("http://example.com")
                .param("key1", "value1")
                .param("key2", "value2")
                .build();
        assertEquals("http://example.com/api?key1=value1&key2=value2", uri.toString());
    }

    @Test
    public void testFullUriConstruction() {
        URI uri = UriBuilder.create("http://example.com")
                .resource("users")
                .resource("123")
                .param("active", "true")
                .param("sort", "asc")
                .build();
        assertEquals("http://example.com/api/users/123?active=true&sort=asc", uri.toString());
    }

    @Test
    public void testEmptyQueryParams() {
        URI uri = UriBuilder.create("http://example.com")
                .resource("users")
                .build();
        assertEquals("http://example.com/api/users?", uri.toString());
    }

    @Test
    public void testNoResourceOrParams() {
        URI uri = UriBuilder.create("http://example.com").build();
        assertEquals("http://example.com/api?", uri.toString());
    }
}
