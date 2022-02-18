package tech.xigam.express;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public final class Route implements HttpHandler {
    private final RouteType routeType;
    private final Consumer<Request> handler;
    private final Express express;

    public Route(RouteType routeType, Consumer<Request> handler, Express express) {
        this.routeType = routeType;
        this.handler = handler;
        this.express = express;
    }

    @Override
    public void handle(HttpExchange exchange) {
        var requestMethod = exchange.getRequestMethod();
        var requestUrl = exchange.getRequestURI().toString();
        var requestArguments = this.parseArguments(requestUrl);
        Request request = new Request(
                exchange, requestMethod, requestUrl,
                requestArguments
        );

        if (!requestMethod.matches(this.routeType.name())) {
            this.express.notFound.accept(request);
            return;
        }

        this.handler.accept(request);
    }

    private Map<String, String> parseArguments(String url) {
        Map<String, String> arguments = new HashMap<>();
        try {
            var args = url.split("\\?")[1];
            var argumentPairs = args.split("&");

            // Parse the first argument.
            var fPair = args.split("=");
            arguments.put(fPair[0].strip(), URLDecoder.decode(fPair[1].strip(), StandardCharsets.UTF_8));

            // Parse the rest of the arguments.
            for (var rawPair : argumentPairs) {
                var pair = rawPair.split("=");
                arguments.put(pair[0].strip(), URLDecoder.decode(pair[1].strip(), StandardCharsets.UTF_8));
            }
        } catch (IndexOutOfBoundsException ignored) {
        }

        return arguments;
    }

    public enum RouteType {
        GET, POST,
        PUT, DELETE,
        PATCH
    }
}
