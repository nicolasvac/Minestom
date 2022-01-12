package net.minestom.server.instance.generator;

import net.minestom.server.coordinate.Point;
import org.jetbrains.annotations.NotNull;

public interface GenerationUnit {
    @NotNull UnitModifier modifier();

    @NotNull Point size();

    @NotNull Point absoluteStart();

    @NotNull Point absoluteEnd();

    static GenerationUnit unit(UnitModifier modifier, Point absoluteStart, Point absoluteEnd) {
        final Point size = absoluteEnd.sub(absoluteStart);
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
                return absoluteStart;
            }

            @Override
            public @NotNull Point absoluteEnd() {
                return absoluteEnd;
            }
        };
    }
}
