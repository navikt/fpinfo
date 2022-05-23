package no.nav.foreldrepenger.info.app;

import java.net.URI;


public record ResourceLink(URI href, String rel, HttpMethod type, Object requestPayload) {

    public ResourceLink(String href, String rel) {
        this(URI.create(href), rel, HttpMethod.GET, null);
    }

    public static ResourceLink post(String href, String rel, Object requestPayload) {
        return new ResourceLink(URI.create(href), rel, HttpMethod.POST, requestPayload);
    }

    public static ResourceLink get(String href, String rel, Object requestPayload) {
        return new ResourceLink(URI.create(href), rel, HttpMethod.GET, requestPayload);
    }

    public enum HttpMethod {
        POST,
        GET,
        PUT,
        PATCH,
        DELETE,
    }
}
