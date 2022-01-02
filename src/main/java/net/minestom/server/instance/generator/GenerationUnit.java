package net.minestom.server.instance.generator;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface GenerationUnit {
    @NotNull List<UnitProperty> units();

    interface Section extends GenerationUnit {
        @NotNull List<UnitProperty.Section> sections();
    }

    interface Chunk extends GenerationUnit {
        @NotNull List<UnitProperty.Chunk> chunks();
    }
}
