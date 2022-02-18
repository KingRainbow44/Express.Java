package tech.xigam.express;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Routes the URL to the correct request.
 */
public final class Router {
    public final Map<String, Consumer<Request>>
            get = new HashMap<>(), post = new HashMap<>(),
            put = new HashMap<>(), delete = new HashMap<>(),
            patch = new HashMap<>();

    public Router get(String path, Consumer<Request> handler) {
        this.get.put(path, handler);
        return this;
    }

    public Router post(String path, Consumer<Request> handler) {
        this.post.put(path, handler);
        return this;
    }

    public Router put(String path, Consumer<Request> handler) {
        this.put.put(path, handler);
        return this;
    }

    public Router delete(String path, Consumer<Request> handler) {
        this.delete.put(path, handler);
        return this;
    }

    public Router patch(String path, Consumer<Request> handler) {
        this.patch.put(path, handler);
        return this;
    }
}
