package net.minestom.server.instance.generator;

import org.jetbrains.annotations.NotNull;

public interface Generator {
    @NotNull GenerationResponse generate(@NotNull GenerationRequest request);
}
