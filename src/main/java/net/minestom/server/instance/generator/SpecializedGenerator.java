package net.minestom.server.instance.generator;

import org.jetbrains.annotations.NotNull;

public interface SpecializedGenerator<T extends GenerationRequest> {
    void generate(@NotNull T request);

    @NotNull Class<T> requiredSubtype();
}
