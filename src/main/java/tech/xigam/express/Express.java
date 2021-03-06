package tech.xigam.express;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpsConfigurator;
import com.sun.net.httpserver.HttpsServer;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.function.Consumer;

/**
 * A simple HTTP request handler for Java.
 * Methods based on Express.JS
 */
public final class Express {
    private final InetSocketAddress address;
    public Consumer<Request> notFound;
    private Router router;

    public Express(InetSocketAddress address) {
        this.address = address;
    }

    public static Express create(int port) {
        return new Express(new InetSocketAddress(port));
    }

    public static Express create(int port, String address) {
        return new Express(new InetSocketAddress(address, port));
    }

    public Express notFound(Consumer<Request> handler) {
        this.notFound = handler;
        return this;
    }

    /**
     * Set the router for this express instance.
     */
    public Express router(Router router) {
        this.router = router;
        return this;
    }

    /**
     * Starts the HTTP server.
     * This should be the final method called.
     */
    public void listen() throws IOException {
        // Create server.
        HttpServer httpServer = HttpServer.create(address, 0);

        // Hook into the server.
        this.hook(httpServer);
        // Start the server.
        httpServer.start();
    }
    
    public void listenSecure(SSLContext sslContext) throws IOException {
        // Create server.
        HttpsServer httpsServer = HttpsServer.create(address, 0);
        // Configure SSL encryption.
        httpsServer.setHttpsConfigurator(new HttpsConfigurator(sslContext));
        
        // Hook into the server.
        this.hook(httpsServer);
        // Start the server.
        httpsServer.start();
    }

    /**
     * Hooks into an existing HTTP server.
     * This should be the final method called.
     */
    public void hook(HttpServer httpServer) {
        // Add routes.
        for (var routes : this.router.get.entrySet())
            httpServer.createContext(routes.getKey(), new Route(Route.RouteType.GET, routes.getValue(), this));
        for (var routes : this.router.post.entrySet())
            httpServer.createContext(routes.getKey(), new Route(Route.RouteType.POST, routes.getValue(), this));
        for (var routes : this.router.put.entrySet())
            httpServer.createContext(routes.getKey(), new Route(Route.RouteType.PUT, routes.getValue(), this));
        for (var routes : this.router.delete.entrySet())
            httpServer.createContext(routes.getKey(), new Route(Route.RouteType.DELETE, routes.getValue(), this));
        for (var routes : this.router.patch.entrySet())
            httpServer.createContext(routes.getKey(), new Route(Route.RouteType.PATCH, routes.getValue(), this));
    }
}
