package net.minestom.server.instance.generator;

import net.minestom.server.coordinate.Point;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

import java.util.List;

public interface UnitProperty {
    @NotNull UnitModifier modifier();

    @NotNull Point size();

    @NotNull Point absoluteStart();

    @NotNull Point absoluteEnd();

    interface Section extends UnitProperty {
        int sectionX();

        int sectionY();

        int sectionZ();
    }

    interface Chunk extends UnitProperty {
        int chunkX();

        int chunkZ();

        @NotNull List<Section> sections();

        default @UnknownNullability Section section(int offset) {
            return sections().get(offset);
        }
    }
}
