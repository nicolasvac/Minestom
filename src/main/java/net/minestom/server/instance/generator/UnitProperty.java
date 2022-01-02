package net.minestom.server.instance.generator;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

import java.util.List;

public interface UnitProperty {
    @NotNull UnitModifier modifier();

    int sizeX();

    int sizeY();

    int sizeZ();

    interface Section extends UnitProperty {
        int absoluteHeight();

        @Override
        default int sizeX() {
            return 16;
        }

        @Override
        default int sizeY() {
            return 16;
        }

        @Override
        default int sizeZ() {
            return 16;
        }
    }

    interface Chunk extends UnitProperty {
        int chunkX();

        int chunkZ();

        @NotNull List<Section> sections();

        default @UnknownNullability Section section(int offset) {
            return sections().get(offset);
        }

        @Override
        default int sizeX() {
            return 16;
        }

        @Override
        int sizeY();

        @Override
        default int sizeZ() {
            return 16;
        }
    }

    interface Region extends UnitProperty {
        int regionX();

        int regionZ();

        @Override
        default int sizeX() {
            return 512;
        }

        @Override
        int sizeY();

        @Override
        default int sizeZ() {
            return 512;
        }
    }
}
