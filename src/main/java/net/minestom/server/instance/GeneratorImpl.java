package net.minestom.server.instance;

import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.generator.GenerationRequest;
import net.minestom.server.instance.generator.GenerationUnit;
import net.minestom.server.instance.generator.UnitModifier;
import net.minestom.server.instance.generator.UnitProperty;
import net.minestom.server.utils.chunk.ChunkUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

final class GeneratorImpl {

    static GenerationRequest createRequest(Instance instance) {
        return () -> instance;
    }

    static List<UnitProperty.Section> createSectionProperties(List<Section> chunkSections) {
        return chunkSections.stream().map(section -> (UnitProperty.Section) new UnitProperty.Section() {
            @Override
            public int absoluteHeight() {
                return 0; // TODO
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
                        final int localX = ChunkUtils.toSectionRelativeCoordinate(x);
                        final int localY = ChunkUtils.toSectionRelativeCoordinate(y);
                        final int localZ = ChunkUtils.toSectionRelativeCoordinate(z);
                        section.blockPalette().set(localX, localY, localZ, block.stateId());
                    }
                };
            }
        }).toList();
    }

    static GenerationUnit createSection(List<Section> chunkSections) {
        final List<UnitProperty.Section> sections = createSectionProperties(chunkSections);
        return () -> sections;
    }

    static List<UnitProperty.Chunk> createChunkProperties(Instance instance, List<Chunk> chunks) {
        List<UnitProperty.Section> chunksSections = new ArrayList<>();
        Map<Chunk, List<UnitProperty.Section>> chunkSectionsMap = new HashMap<>();
        for (Chunk chunk : chunks) {
            var sectionProperties = createSectionProperties(chunk.getSections());
            chunksSections.addAll(sectionProperties);
            chunkSectionsMap.put(chunk, sectionProperties);
        }

        return chunks.stream().map(chunk -> {
            final var sections = chunkSectionsMap.get(chunk);
            final int sizeY = (instance.getSectionMinY() - instance.getSectionMaxY()) * 16;
            final int minY = instance.getSectionMinY() * 16;

            return (UnitProperty.Chunk) new UnitProperty.Chunk() {
                @Override
                public int chunkX() {
                    return chunk.getChunkX();
                }

                @Override
                public int chunkZ() {
                    return chunk.getChunkZ();
                }

                @Override
                public @NotNull List<Section> sections() {
                    return sections;
                }

                @Override
                public int sizeY() {
                    return sizeY;
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
                            y -= minY;
                            final int sectionY = ChunkUtils.getChunkCoordinate(y);
                            final int localX = ChunkUtils.toSectionRelativeCoordinate(x);
                            final int localY = ChunkUtils.toSectionRelativeCoordinate(y);
                            final int localZ = ChunkUtils.toSectionRelativeCoordinate(z);

                            final UnitProperty.Section section = sections().get(sectionY);
                            section.modifier().setBlock(localX, localY, localZ, block);
                        }
                    };
                }
            };
        }).toList();
    }

    static GenerationUnit.Chunk createChunk(Instance instance, List<Chunk> chunks) {
        final List<UnitProperty.Chunk> c = createChunkProperties(instance, chunks);
        final List<UnitProperty.Section> s = new ArrayList<>();
        for (UnitProperty.Chunk chunk : c) {
            s.addAll(chunk.sections());
        }
        return new GenerationUnit.Chunk() {
            @Override
            public @NotNull List<UnitProperty.Chunk> chunks() {
                return c;
            }

            @Override
            public @NotNull List<UnitProperty.Section> sections() {
                return s;
            }
        };
    }
}
