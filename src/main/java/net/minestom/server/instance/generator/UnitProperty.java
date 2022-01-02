package net.minestom.server.instance.generator;

import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Vec;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

import java.util.List;

public interface UnitProperty {
    @NotNull UnitModifier modifier();

    @NotNull Point size();

    interface Section extends UnitProperty {
        int absoluteHeight();

        @Override
        default @NotNull Point size() {
            return new Vec(16, 16, 16);
        }
    }

    interface Chunk extends UnitProperty {
        int chunkX();

        int chunkZ();

        @NotNull List<Section> sections();

        default @UnknownNullability Section section(int offset) {
            return sections().get(offset);
        }
    }

    interface Region extends UnitProperty {
        int regionX();

        int regionZ();
    }
}
