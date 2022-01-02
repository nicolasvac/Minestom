package net.minestom.server.instance.generator;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public sealed interface GenerationUnit permits GenerationUnit.Section {
    @NotNull UnitModifier modifier();

    non-sealed interface Section extends GenerationUnit {
    }

    interface Chunk extends Section {
        @NotNull List<Section> sections();

        default Section section(int offset) {
            return sections().get(offset);
        }
    }

    interface Region extends Chunk {
        @NotNull List<Chunk> chunks();

        @NotNull Chunk chunk(int chunkX, int chunkZ);
    }
}
