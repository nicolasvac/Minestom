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
        record Impl(int absoluteHeight, UnitModifier modifier)
                implements UnitProperty.Section {
        }
        var result = chunkSections.stream().map(section -> {
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
            return new Impl(0, modifier);
        }).toList();
        return (List) result;
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
        record Impl(int chunkX, int chunkZ, List<Section> sections, int sizeY, UnitModifier modifier)
                implements UnitProperty.Chunk {
        }

        final var result = chunks.stream().map(chunk -> {
            final var sections = chunkSectionsMap.get(chunk);
            final int sizeY = (instance.getSectionMinY() - instance.getSectionMaxY()) * 16;
            final int minY = instance.getSectionMinY() * 16;
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
            return new Impl(chunk.getChunkX(), chunk.getChunkZ(), sections, sizeY, modifier);
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
