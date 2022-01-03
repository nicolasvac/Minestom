package net.minestom.server.instance.generator;

import org.jetbrains.annotations.NotNull;

import java.util.List;

@FunctionalInterface
public interface Generator {
    void generate(@NotNull GenerationRequest request);

    default void generateAll(@NotNull List<GenerationRequest> requests) {
        requests.forEach(this::generate);
    }

    static <T extends GenerationRequest> @NotNull Generator specialize(@NotNull SpecializedGenerator<T> generator) {
        final var requiredSubtype = generator.requiredSubtype();
        return (request) -> {
            if (request instanceof GenerationRequest.Chunks chunks && requiredSubtype.isInstance(chunks)) {
                //noinspection unchecked
                generator.generate((T) request);
                return;
            }
            throw new UnsupportedOperationException("Not implemented yet: " + request + " " + requiredSubtype);
        };
    }
}
