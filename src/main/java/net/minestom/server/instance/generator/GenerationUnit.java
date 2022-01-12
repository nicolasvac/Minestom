package net.minestom.server.instance.generator;

import net.minestom.server.coordinate.Point;
import org.jetbrains.annotations.NotNull;

public interface GenerationUnit {
    @NotNull UnitModifier modifier();

    @NotNull Point size();

    @NotNull Point absoluteStart();

    @NotNull Point absoluteEnd();

    static GenerationUnit unit(UnitModifier modifier, Point start, Point end) {
        if (start.x() > end.x() || start.y() > end.y() || start.z() > end.z()) {
            throw new IllegalArgumentException("absoluteStart must be before absoluteEnd");
        }
        if (start.x() % 16 != 0 || start.y() % 16 != 0 || start.z() % 16 != 0) {
            throw new IllegalArgumentException("absoluteStart must be a multiple of 16");
        }
        if (end.x() % 16 != 0 || end.y() % 16 != 0 || end.z() % 16 != 0) {
            throw new IllegalArgumentException("absoluteEnd must be a multiple of 16");
        }
        final Point size = end.sub(start);
        return new GenerationUnit() {
            @Override
            public @NotNull UnitModifier modifier() {
                return modifier;
            }

            @Override
            public @NotNull Point size() {
                return size;
            }

            @Override
            public @NotNull Point absoluteStart() {
                return start;
            }

            @Override
            public @NotNull Point absoluteEnd() {
                return end;
            }
        };
    }
}
