package net.minestom.server.instance.generator;

import net.minestom.server.coordinate.Point;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

import java.util.List;

public interface GenerationUnit {
    @NotNull UnitModifier modifier();

    @NotNull Point size();

    @NotNull Point absoluteStart();

    @NotNull Point absoluteEnd();

    interface Section extends GenerationUnit {
        int sectionX();

        int sectionY();

        int sectionZ();
    }

    interface Chunk extends GenerationUnit {
        int chunkX();

        int chunkZ();

        @NotNull List<Section> sections();

        default @UnknownNullability Section section(int offset) {
            return sections().get(offset);
        }
    }
}
