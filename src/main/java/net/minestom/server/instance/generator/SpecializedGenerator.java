package net.minestom.server.instance.generator;

import org.jetbrains.annotations.NotNull;

public interface SpecializedGenerator<T extends GenerationUnit> extends Generator {
    @NotNull Class<T> requiredSubtype();
}
