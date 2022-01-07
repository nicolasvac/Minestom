package net.minestom.server.instance;

import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.generator.GenerationUnit;
import net.minestom.server.instance.generator.UnitModifier;
import net.minestom.server.utils.chunk.ChunkUtils;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

final class GeneratorImpl {
    private static final Vec SECTION_SIZE = new Vec(16);

    record ChunkEntry(List<Section> sections, int x, int z) {
        public ChunkEntry(Chunk chunk) {
            this(chunk.getSections(), chunk.getChunkX(), chunk.getChunkZ());
        }
    }

    static GenerationUnit.Section section(Section section, int sectionX, int sectionY, int sectionZ) {
        record SectionImpl(int sectionX, int sectionY, int sectionZ,
                           Point size, Point absoluteStart, Point absoluteEnd, UnitModifier modifier)
                implements GenerationUnit.Section {
        }
        final var start = new Vec(sectionX * 16, sectionY * 16, sectionZ * 16);
        final var end = start.add(16);
        final UnitModifier modifier = new ModifierImpl(start, end) {
            @Override
            public void setBlock(int x, int y, int z, @NotNull Block block) {
                final int localX = ChunkUtils.toSectionRelativeCoordinate(x);
                final int localY = ChunkUtils.toSectionRelativeCoordinate(y);
                final int localZ = ChunkUtils.toSectionRelativeCoordinate(z);
                section.blockPalette().set(localX, localY, localZ, block.stateId());
            }

            @Override
            public void fill(@NotNull Block block) {
                section.blockPalette().fill(block.stateId());
            }
        };
        return new SectionImpl(sectionX, sectionY, sectionZ, SECTION_SIZE, start, end, modifier);
    }

    static GenerationUnit.Chunk chunk(int minSection, int maxSection, ChunkEntry chunk) {
        final int minY = minSection * 16;

        AtomicInteger sectionCounterY = new AtomicInteger(minSection);
        List<GenerationUnit.Section> sections = chunk.sections().stream()
                .map(section -> section(section, chunk.x(), sectionCounterY.getAndIncrement(), chunk.z()))
                .toList();
        record Impl(int chunkX, int chunkZ, int minY, List<Section> sections,
                    Point size, Point absoluteStart, Point absoluteEnd, UnitModifier modifier)
                implements GenerationUnit.Chunk {
        }

        final int chunkX = chunk.x();
        final int chunkZ = chunk.z();
        final var size = new Vec(16, (maxSection - minSection) * 16, 16);
        final var start = new Vec(chunkX * 16, minY, chunkZ * 16);
        final var end = new Vec(chunkX * 16 + 16, size.y() + minY, chunkZ * 16 + 16);
        final UnitModifier modifier = new ModifierImpl(start, end) {
            @Override
            public void setBlock(int x, int y, int z, @NotNull Block block) {
                if (ChunkUtils.getChunkCoordinate(x) != chunkX || ChunkUtils.getChunkCoordinate(z) != chunkZ) {
                    throw new IllegalArgumentException("x and z must be in the same chunk");
                }
                y -= minY;
                final int sectionY = ChunkUtils.getChunkCoordinate(y);
                final GenerationUnit.Section section = sections.get(sectionY);
                section.modifier().setBlock(x, y, z, block);
            }

            @Override
            public void fill(@NotNull Block block) {
                for (GenerationUnit.Section section : sections) {
                    section.modifier().fill(block);
                }
            }
        };
        return new Impl(chunkX, chunkZ, minY, sections, size, start, end, modifier);
    }

    static abstract class ModifierImpl implements UnitModifier {
        private final Point start, end;

        public ModifierImpl(Point start, Point end) {
            this.start = start;
            this.end = end;
        }

        @Override
        public void setAll(@NotNull Supplier supplier) {
            for (int x = start.blockX(); x < end.blockX(); x++) {
                for (int y = start.blockY(); y < end.blockY(); y++) {
                    for (int z = start.blockZ(); z < end.blockZ(); z++) {
                        setBlock(x, y, z, supplier.get(x, y, z));
                    }
                }
            }
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
