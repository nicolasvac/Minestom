package net.minestom.server.instance.generator;

import net.minestom.server.instance.Instance;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface GenerationRequest {
    @NotNull Instance instance();

    void returnAsync(@NotNull CompletableFuture<?> future);

    @NotNull List<@NotNull GenerationUnit> units();

    interface Sections extends GenerationRequest {
        @NotNull List<GenerationUnit.Section> sections();

        @Override
        default @NotNull List<GenerationUnit> units() {
            //noinspection unchecked,rawtypes
            return (List) sections();
        }
    }

    interface Chunks extends GenerationRequest.Sections {
        @NotNull List<GenerationUnit.Chunk> chunks();

        @Override
        default @NotNull List<GenerationUnit> units() {
            //noinspection unchecked,rawtypes
            return (List) chunks();
        }
    }
}
