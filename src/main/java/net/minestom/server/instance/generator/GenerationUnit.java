package net.minestom.server.instance.generator;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface GenerationUnit {
    @NotNull UnitModifier modifier();

    interface Section extends GenerationUnit {
        int absoluteHeight();
    }

    interface Chunk extends GenerationUnit {
        int chunkX();

        int chunkZ();

        @NotNull List<Section> sections();

        default Section section(int offset) {
            return sections().get(offset);
        }
    }

    interface Region extends GenerationUnit {
        int regionX();

        int regionZ();

        @NotNull List<Chunk> chunks();

        @NotNull Chunk chunk(int chunkX, int chunkZ);
    }
}
