package net.minestom.server.instance;

import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.generator.GenerationRequest;
import net.minestom.server.instance.generator.GenerationUnit;
import net.minestom.server.instance.generator.UnitModifier;
import net.minestom.server.instance.generator.UnitProperty;
import net.minestom.server.utils.chunk.ChunkUtils;
import org.jetbrains.annotations.NotNull;

import java.util.List;

final class GeneratorImpl {

    static GenerationRequest createRequest(Instance instance) {
        return new GenerationRequest() {
            @Override
            public @NotNull Instance instance() {
                return instance;
            }

            @Override
            public @NotNull List<GenerationUnit.Section> sections() {
                return null;
            }

            @Override
            public @NotNull UnitProperty property(@NotNull GenerationUnit unit) {
                return null;
            }
        };
    }

    static GenerationUnit.Section createSection(Section chunkSection) {
        return new GenerationUnit.Section() {
            @Override
            public @NotNull UnitModifier modifier() {
                return new UnitModifier() {
                    @Override
                    public void fill(@NotNull Block block) {
                        throw new UnsupportedOperationException();
                    }

                    @Override
                    public void fill(@NotNull Point start, @NotNull Point end, @NotNull Block block) {
                        throw new UnsupportedOperationException();
                    }

                    @Override
                    public void setBlock(int x, int y, int z, @NotNull Block block) {
                        final int localX = ChunkUtils.toSectionRelativeCoordinate(x);
                        final int localY = ChunkUtils.toSectionRelativeCoordinate(y);
                        final int localZ = ChunkUtils.toSectionRelativeCoordinate(z);
                        chunkSection.blockPalette().set(localX, localY, localZ, block.stateId());
                    }
                };
            }
        };
    }

    static GenerationUnit.Chunk createChunk(List<GenerationUnit.Section> sections) {
        return new GenerationUnit.Chunk() {
            @Override
            public @NotNull List<Section> sections() {
                return sections;
            }

            @Override
            public @NotNull UnitModifier modifier() {
                return new UnitModifier() {
                    @Override
                    public void fill(@NotNull Block block) {
                        throw new UnsupportedOperationException();
                    }

                    @Override
                    public void fill(@NotNull Point start, @NotNull Point end, @NotNull Block block) {
                        throw new UnsupportedOperationException();
                    }

                    @Override
                    public void setBlock(int x, int y, int z, @NotNull Block block) {
                        final int sectionY = ChunkUtils.getChunkCoordinate(y);
                        final int localX = ChunkUtils.toSectionRelativeCoordinate(x);
                        final int localY = ChunkUtils.toSectionRelativeCoordinate(y);
                        final int localZ = ChunkUtils.toSectionRelativeCoordinate(z);

                        final Section section = sections().get(sectionY);
                        section.modifier().setBlock(localX, localY, localZ, block);
                    }
                };
            }
        };
    }

}
