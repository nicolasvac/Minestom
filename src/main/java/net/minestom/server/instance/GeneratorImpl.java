package net.minestom.server.instance;

import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Vec;
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
import java.util.concurrent.atomic.AtomicInteger;

final class GeneratorImpl {

    static GenerationRequest createRequest(Instance instance) {
        return () -> instance;
    }

    static List<UnitProperty.Chunk> createChunkProperties(Instance instance, List<Chunk> chunks) {
        final int sizeY = (instance.getSectionMinY() + instance.getSectionMaxY()) * 16;
        final int minY = instance.getSectionMinY() * 16;

        Map<Chunk, List<UnitProperty.Section>> chunkSectionsMap = new HashMap<>(chunks.size());
        for (Chunk chunk : chunks) {
            record SectionImpl(int sectionX, int sectionY, int sectionZ, UnitModifier modifier)
                    implements UnitProperty.Section {
            }
            AtomicInteger sectionY = new AtomicInteger(minY);
            var sectionProperties = chunk.getSections().stream().map(section -> {
                final UnitModifier modifier = new UnitModifier() {
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
                return new SectionImpl(chunk.getChunkX(), sectionY.getAndIncrement(), chunk.getChunkZ(), modifier);
            }).toList();
            chunkSectionsMap.put(chunk, (List) sectionProperties);
        }
        record Impl(int chunkX, int chunkZ, int minY, List<Section> sections,
                    Point size, UnitModifier modifier)
                implements UnitProperty.Chunk {
        }

        final var result = chunks.stream().map(chunk -> {
            final var sections = chunkSectionsMap.get(chunk);
            final UnitModifier modifier = new UnitModifier() {
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

                    final UnitProperty.Section section = sections.get(sectionY);
                    section.modifier().setBlock(localX, localY, localZ, block);
                }
            };
            return new Impl(chunk.getChunkX(), chunk.getChunkZ(), minY, sections, new Vec(16, sizeY - minY, 16), modifier);
        }).toList();
        return (List) result;
    }

    static GenerationUnit.Chunk createChunk(Instance instance, List<Chunk> chunks) {
        final List<UnitProperty.Chunk> c = createChunkProperties(instance, chunks);
        final List<UnitProperty.Section> s = new ArrayList<>();
        for (UnitProperty.Chunk chunk : c) s.addAll(chunk.sections());
        record Impl(List<UnitProperty.Section> sections, List<UnitProperty.Chunk> chunks)
                implements GenerationUnit.Chunk {
        }
        return new Impl(s, c);
    }
}
