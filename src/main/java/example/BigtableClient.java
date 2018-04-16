package example;

import com.google.common.collect.ImmutableSet;

import java.net.URI;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class BigtableClient {

    public CompletableFuture<Set<Entity>> lookup(final Set<URI> uris, final String kind) {
        return CompletableFuture.completedFuture(ImmutableSet.of(new Entity()));
    }
}