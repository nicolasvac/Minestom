package net.minestom.server.instance.generator;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface GenerationResponse {
    Optional<CompletableFuture<?>> future();
}
