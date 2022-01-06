package net.minestom.server.instance.generator;

import org.jetbrains.annotations.NotNull;

import java.util.List;

@FunctionalInterface
public interface Generator {
    void generate(@NotNull GenerationRequest request);

    default void generateAll(@NotNull List<GenerationRequest> requests) {
        requests.forEach(this::generate);
    }

    static <T extends GenerationUnit> @NotNull Generator specialize(@NotNull Class<T> subtype,
                                                                    @NotNull Generator generator) {
        return (request) -> {
            GenerationUnit unit = request.unit();
            if (subtype.isInstance(unit)) {
                generator.generate(request);
                return;
            }
            throw new UnsupportedOperationException("Not implemented yet: " + request + " " + subtype);
        };
    }
}
