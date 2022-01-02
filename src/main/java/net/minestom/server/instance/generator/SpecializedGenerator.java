package net.minestom.server.instance.generator;

import org.jetbrains.annotations.NotNull;

public interface SpecializedGenerator<T extends GenerationUnit> {
    void generate(@NotNull GenerationRequest request, @NotNull T unit);

    @NotNull Class<T> requiredSubtype();
}
