package net.minestom.server.instance.generator;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface Generator {
    @NotNull GenerationResponse generate(@NotNull GenerationRequest request);

    default @NotNull List<GenerationResponse> generateAll(@NotNull List<GenerationRequest> requests) {
        return requests.stream().map(this::generate).toList();
    }
}
