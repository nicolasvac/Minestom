package net.minestom.server.instance.generator;

import net.minestom.server.utils.async.AsyncUtils;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public interface GenerationResponse {
    default @NotNull CompletableFuture<?> future() {
        return AsyncUtils.VOID_FUTURE;
    }
}
