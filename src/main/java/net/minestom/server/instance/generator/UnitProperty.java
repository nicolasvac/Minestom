package net.minestom.server.instance.generator;

import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Vec;
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

        @Override
        default @NotNull Point size() {
            return new Vec(16, 16, 16);
        }

        @Override
        default @NotNull Point absoluteStart() {
            return new Vec(sectionX() * 16, sectionY() * 16, sectionZ() * 16);
        }

        @Override
        default @NotNull Point absoluteEnd() {
            return new Vec(sectionX() * 16 + 16, sectionY() * 16 + 16, sectionZ() * 16 + 16);
        }
    }

    interface Chunk extends UnitProperty {
        int chunkX();

        int chunkZ();

        int minY();

        @NotNull List<Section> sections();

        default @UnknownNullability Section section(int offset) {
            return sections().get(offset);
        }

        @Override
        default @NotNull Point absoluteStart() {
            return new Vec(chunkX() * 16, minY(), chunkZ() * 16);
        }

        @Override
        default @NotNull Point absoluteEnd() {
            return new Vec(chunkX() * 16 + 16, size().y() + minY(), chunkZ() * 16 + 16);
        }
    }

    interface Region extends UnitProperty {
        int regionX();

        int regionZ();
    }
}
