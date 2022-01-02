package net.minestom.server.instance.generator;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

@FunctionalInterface
public interface Generator {
    void generate(@NotNull GenerationRequest request, @NotNull GenerationUnit unit);

    default void generateAll(@NotNull Map<GenerationRequest, GenerationUnit> requests) {
        requests.forEach(this::generate);
    }

    static <T extends GenerationUnit> @NotNull Generator specialize(@NotNull SpecializedGenerator<T> generator) {
        final var requiredSubtype = generator.requiredSubtype();
        return (request, unit) -> {
            if (unit instanceof GenerationUnit.Chunk chunk && requiredSubtype.isInstance(chunk)) {
                generator.generate(request, requiredSubtype.cast(chunk));
                return;
            }
            throw new UnsupportedOperationException("Not implemented yet: " + unit + " " + requiredSubtype);
        };
    }
}
