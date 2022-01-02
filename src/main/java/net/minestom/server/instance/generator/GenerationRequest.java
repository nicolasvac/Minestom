package net.minestom.server.instance.generator;

import net.minestom.server.instance.Instance;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public interface GenerationRequest {
    @NotNull Instance instance();

    void returnAsync(@NotNull CompletableFuture<?> future);
}
