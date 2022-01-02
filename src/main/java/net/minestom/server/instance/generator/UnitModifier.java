package net.minestom.server.instance.generator;

import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.block.Block;
import org.jetbrains.annotations.NotNull;

public interface UnitModifier extends Block.Setter {
    void fill(@NotNull Block block);

    void fill(@NotNull Point start, @NotNull Point end, @NotNull Block block);
}
