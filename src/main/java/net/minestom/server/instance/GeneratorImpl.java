package net.minestom.server.instance;

import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.generator.GenerationRequest;
import net.minestom.server.instance.generator.GenerationUnit;
import net.minestom.server.instance.generator.UnitModifier;
import net.minestom.server.utils.chunk.ChunkUtils;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

final class GeneratorImpl {
    record ChunkEntry(List<Section> sections, int x, int z) {
        public ChunkEntry(Chunk chunk) {
            this(chunk.getSections(), chunk.getChunkX(), chunk.getChunkZ());
        }
    }

    static List<GenerationUnit.Chunk> createChunkProperties(int minSection, int maxSection, List<ChunkEntry> chunks) {
        final int sizeY = (minSection + maxSection) * 16;
        final int minY = minSection * 16;

        Map<ChunkEntry, List<GenerationUnit.Section>> chunkSectionsMap = new HashMap<>(chunks.size());
        for (ChunkEntry chunk : chunks) {
            record SectionImpl(int sectionX, int sectionY, int sectionZ,
                               Point size, Point absoluteStart, Point absoluteEnd, UnitModifier modifier)
                    implements GenerationUnit.Section {
            }
            AtomicInteger sectionCounterY = new AtomicInteger(minSection);
            var sectionProperties = chunk.sections().stream().map(section -> {
                final int sectionX = chunk.x();
                final int sectionY = sectionCounterY.getAndIncrement();
                final int sectionZ = chunk.z();
                final var size = new Vec(16, 16, 16);
                final var start = new Vec(sectionX * 16, sectionY * 16, sectionZ * 16);
                final var end = new Vec(sectionX * 16 + 16, sectionY * 16 + 16, sectionZ * 16 + 16);
                final UnitModifier modifier = new ModifierImpl(start, end) {
                    @Override
                    public void setBlock(int x, int y, int z, @NotNull Block block) {
                        final int localX = ChunkUtils.toSectionRelativeCoordinate(x);
                        final int localY = ChunkUtils.toSectionRelativeCoordinate(y);
                        final int localZ = ChunkUtils.toSectionRelativeCoordinate(z);
                        section.blockPalette().set(localX, localY, localZ, block.stateId());
                    }
                };
                return new SectionImpl(sectionX, sectionY, sectionZ, size, start, end, modifier);
            }).toList();
            chunkSectionsMap.put(chunk, (List) sectionProperties);
        }
        record Impl(int chunkX, int chunkZ, int minY, List<Section> sections,
                    Point size, Point absoluteStart, Point absoluteEnd, UnitModifier modifier)
                implements GenerationUnit.Chunk {
        }

        final var result = chunks.stream().map(chunk -> {
            final var sections = chunkSectionsMap.get(chunk);
            final int chunkX = chunk.x();
            final int chunkZ = chunk.z();
            final var size = new Vec(16, sizeY - minY + 16, 16);
            final var start = new Vec(chunkX * 16, minY, chunkZ * 16);
            final var end = new Vec(chunkX * 16 + 16, size.y() + minY, chunkZ * 16 + 16);
            final UnitModifier modifier = new ModifierImpl(start, end) {
                @Override
                public void setBlock(int x, int y, int z, @NotNull Block block) {
                    y -= minY;
                    final int sectionY = ChunkUtils.getChunkCoordinate(y);
                    final GenerationUnit.Section section = sections.get(sectionY);
                    section.modifier().setBlock(x, y, z, block);
                }
            };
            return new Impl(chunkX, chunkZ, minY, sections, size, start, end, modifier);
        }).toList();
        return (List) result;
    }

    static List<GenerationUnit.Section> sectionUnits(List<GenerationUnit.Chunk> chunks) {
        return chunks.stream().flatMap(chunk -> chunk.sections().stream()).toList();
    }

    static GenerationRequest.Chunks chunksRequest(Instance instance,
                                                  List<GenerationUnit.Chunk> chunkUnits){
        final List<GenerationUnit.Section> sectionUnits = GeneratorImpl.sectionUnits(chunkUnits);
        return new GenerationRequest.Chunks() {
            @Override
            public @NotNull List<GenerationUnit.Chunk> chunks() {
                return chunkUnits;
            }

            @Override
            public @NotNull List<GenerationUnit.Section> sections() {
                return sectionUnits;
            }

            @Override
            public @NotNull Instance instance() {
                return instance;
            }

            @Override
            public void returnAsync(@NotNull CompletableFuture<?> future) {
                // Empty
            }
        };
    }

    static abstract class ModifierImpl implements UnitModifier {
        private final Point start, end;

        public ModifierImpl(Point start, Point end) {
            this.start = start;
            this.end = end;
        }

        @Override
        public void fill(@NotNull Block block) {
            fill(start, end, block);
        }

        @Override
        public void fill(@NotNull Point start, @NotNull Point end, @NotNull Block block) {
            final int endX = end.blockX();
            final int endY = end.blockY();
            final int endZ = end.blockZ();
            for (int x = start.blockX(); x < endX; x++) {
                for (int y = start.blockY(); y < endY; y++) {
                    for (int z = start.blockZ(); z < endZ; z++) {
                        setBlock(x, y, z, block);
                    }
                }
            }
        }
    }
}
