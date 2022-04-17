package tech.xigam.express;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This is a class that contains related data of an HTTP request.
 */
public final class Request {
    public final HttpExchange httpExchange;
    public final String requestType;
    public final String requestUrl;
    public final String requestBody;
    public final Map<String, String> requestArguments;
    
    private int responseCode = 200;
    private Map<String, String[]> responseHeaders = new HashMap<>();

    public Request(
            HttpExchange httpExchange, String requestType, String requestUrl,
            String requestBody, Map<String, String> requestArguments
    ) {
        this.httpExchange = httpExchange;
        this.requestType = requestType;
        this.requestUrl = requestUrl;
        this.requestBody = requestBody;
        this.requestArguments = requestArguments;
        
        // Close request body.
        try {
            httpExchange.getRequestBody().close();
        } catch (Exception ignored) { }
    }

    public Request code(int statusCode) {
        this.responseCode = statusCode;
        return this;
    }
    
    public Request addHeader(String name, String... value) {
        this.responseHeaders.put(name, value);
        return this;
    }

    public void respond(String response) {
        try {
            OutputStream output = this.httpExchange.getResponseBody();
            this.httpExchange.sendResponseHeaders(this.responseCode, response.length());
            output.write(response.getBytes());
            output.flush();
            output.close();
        } catch (IOException ignored) {
        }
    }

    /*
     * Utility methods.
     */

    /**
     * Fetch the URL segments.
     *
     * @return The segments (without the https://) of the request URL.
     */
    public List<String> urlSegments() {
        // [https:]/[]/[stackoverflow.com]/[a]/[5471032]/[1068170]
        var initial = new ArrayList<>(List.of(this.requestUrl.split("/")));
        initial.remove(0);
        initial.remove(1);
        return initial;
    }

    /**
     * Returns the request argument with the given name.
     * @param key The name of the argument.
     * @return The value of the argument.
     */
    public String getArgument(String key) {
        return this.requestArguments.get(key);
    }

    /**
     * Returns the request's body data.
     * @return The request body as a string.
     */
    public String getRequestBody() {
        return this.requestBody;
    }
}
