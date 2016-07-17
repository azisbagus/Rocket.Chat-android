package chat.rocket.rc.models;

import java.util.Map;

/**
 * Created by julio on 21/11/15.
 */
public class UrlParts {
    public UrlParts() {
    }

    public UrlParts(String url, Map<String, String> meta, Map<String, String> headers, Map<String, String> parsedUrl) {
        this.url = url;
        this.meta = meta;
        this.headers = headers;
        this.parsedUrl = parsedUrl;
    }

    protected String url;
    protected Map<String, String> meta;
    protected Map<String, String> headers;
    protected Map<String, String> parsedUrl;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public Map<String, String> getParsedUrl() {
        return parsedUrl;
    }

    public void setParsedUrl(Map<String, String> parsedUrl) {
        this.parsedUrl = parsedUrl;
    }

    public Map<String, String> getMeta() {
        return meta;
    }

    public void setMeta(Map<String, String> meta) {
        this.meta = meta;
    }
}
