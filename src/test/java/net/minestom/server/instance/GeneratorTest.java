package net.minestom.server.instance;

import net.minestom.server.coordinate.Vec;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.generator.GenerationRequest;
import net.minestom.server.instance.generator.GenerationUnit;
import net.minestom.server.instance.generator.Generator;
import net.minestom.server.instance.generator.UnitProperty;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

public class GeneratorTest {
    static final GenerationRequest DUMMY_REQUEST = new GenerationRequest() {
        @Override
        public @NotNull Instance instance() {
            return null;
        }

        @Override
        public void returnAsync(@NotNull CompletableFuture<?> future) {
        }
    };

    @Test
    public void chunkPlacement() {
        final int minSection = -1;
        final int maxSection = 5;

        final int chunkX = 3;
        final int chunkZ = 2;

        final int sectionCount = maxSection - minSection;
        Section[] sections = new Section[sectionCount];
        for (int i = 0; i < sections.length; i++) {
            sections[i] = new Section();
        }
        GenerationUnit.Chunk chunkUnit = GeneratorImpl.createChunk(minSection, maxSection,
                List.of(new GeneratorImpl.ChunkEntry(List.of(sections), chunkX, chunkZ)));

        Generator generator = (request, unit) -> {
            var list = unit.units();
            assertEquals(1, list.size(), "Single chunk has been requested");

            var chunk = list.get(0);
            assertInstanceOf(UnitProperty.Chunk.class, chunk, "Unit is a chunk");

            UnitProperty.Chunk property = (UnitProperty.Chunk) chunk;
            // Chunk properties
            {
                assertEquals(3, property.chunkX());
                assertEquals(2, property.chunkZ());
                assertEquals(sections.length, property.sections().size());
                assertEquals(new Vec(16, sectionCount * 16, 16), property.size());
                assertEquals(new Vec(chunkX * 16, minSection * 16, chunkZ * 16), property.absoluteStart());
                assertEquals(new Vec(chunkX * 16 + 16, maxSection * 16, chunkZ * 16 + 16), property.absoluteEnd());
            }

            var modifier = property.modifier();
            modifier.setBlock(0, 0, 0, Block.STONE);
            modifier.setBlock(1, 1, 0, Block.STONE);
        };

        generator.generate(DUMMY_REQUEST, chunkUnit);
        assertEquals(Block.STONE.stateId(), sections[1].blockPalette().get(0, 0, 0));
        assertEquals(Block.STONE.stateId(), sections[1].blockPalette().get(1, 1, 0));
        assertEquals(0, sections[1].blockPalette().get(0, 1, 0));
    }
}
