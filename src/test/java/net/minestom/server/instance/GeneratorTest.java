package net.minestom.server.instance;

import net.minestom.server.coordinate.Vec;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.generator.GenerationRequest;
import net.minestom.server.instance.generator.GenerationUnit;
import net.minestom.server.instance.generator.Generator;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class GeneratorTest {

    @Test
    public void chunkSize() {
        final int minSection = -1;
        final int maxSection = 5;
        final int chunkX = 3;
        final int chunkZ = 2;

        final int sectionCount = maxSection - minSection;
        Section[] sections = new Section[sectionCount];
        Arrays.setAll(sections, i -> new Section());
        var chunkUnits = GeneratorImpl.chunk(minSection, maxSection,
                new GeneratorImpl.ChunkEntry(List.of(sections), chunkX, chunkZ));

        Generator generator = request -> {
            GenerationUnit.Chunk chunk = (GenerationUnit.Chunk) request.unit();
            assertEquals(3, chunk.chunkX());
            assertEquals(2, chunk.chunkZ());
            assertEquals(sections.length, chunk.sections().size());
            assertEquals(new Vec(16, sectionCount * 16, 16), chunk.size());
            assertEquals(new Vec(chunkX * 16, minSection * 16, chunkZ * 16), chunk.absoluteStart());
            assertEquals(new Vec(chunkX * 16 + 16, maxSection * 16, chunkZ * 16 + 16), chunk.absoluteEnd());
        };
        generator.generate(request(null, chunkUnits));
    }

    @Test
    public void sectionSize() {
        final int minSection = -5;
        final int maxSection = 5;
        final int chunkX = 3;
        final int chunkZ = 2;

        final int sectionCount = maxSection - minSection;
        Section[] s = new Section[sectionCount];
        Arrays.setAll(s, i -> new Section());
        var chunkUnits = GeneratorImpl.chunk(minSection, maxSection,
                new GeneratorImpl.ChunkEntry(List.of(s), chunkX, chunkZ));

        Generator generator = request -> {
            GenerationUnit.Chunk chunk = (GenerationUnit.Chunk) request.unit();
            List<GenerationUnit.Section> sections = chunk.sections();
            assertEquals(s.length, sections.size());
            for (int i = 0; i < sections.size(); i++) {
                final int sectionY = minSection + i;
                final GenerationUnit.Section section = sections.get(i);
                assertEquals(chunkX, section.sectionX());
                assertEquals(sectionY, section.sectionY());
                assertEquals(chunkZ, section.sectionZ());

                assertEquals(new Vec(16), section.size());
                assertEquals(new Vec(chunkX * 16, sectionY * 16, chunkZ * 16), section.absoluteStart());
                assertEquals(section.absoluteStart().add(16), section.absoluteEnd());
            }
        };

        generator.generate(request(null, chunkUnits));
    }

    @Test
    public void chunkAbsolute() {
        final int minSection = 0;
        final int maxSection = 5;
        final int chunkX = 3;
        final int chunkZ = 2;

        final int sectionCount = maxSection - minSection;
        Section[] sections = new Section[sectionCount];
        Arrays.setAll(sections, i -> new Section());
        var chunkUnits = GeneratorImpl.chunk(minSection, maxSection,
                new GeneratorImpl.ChunkEntry(List.of(sections), chunkX, chunkZ));

        Generator generator = request -> {
            GenerationUnit.Chunk chunk = (GenerationUnit.Chunk) request.unit();
            var modifier = chunk.modifier();
            assertThrows(Exception.class, () -> modifier.setBlock(0, 0, 0, Block.STONE), "Block outside of chunk");
            modifier.setBlock(56, 0, 40, Block.STONE);
            modifier.setBlock(56, 17, 40, Block.STONE);
        };
        generator.generate(request(null, chunkUnits));
        assertEquals(Block.STONE.stateId(), sections[0].blockPalette().get(8, 0, 8));
        assertEquals(Block.STONE.stateId(), sections[1].blockPalette().get(8, 1, 8));
    }

    @Test
    public void sectionFill() {
        Section section = new Section();
        var chunkUnit = GeneratorImpl.section(section, -1, -1, 0);
        Generator generator = request -> {
            var unit = (GenerationUnit.Section) request.unit();
            unit.modifier().fill(Block.STONE);
        };

        generator.generate(request(null, chunkUnit));
        section.blockPalette().getAllPresent((x, y, z, value) ->
                assertEquals(Block.STONE.stateId(), value));
    }

    private GenerationRequest request(Instance instance, GenerationUnit unit) {
        return new GenerationRequest() {
            @Override
            public @NotNull Instance instance() {
                return instance;
            }

            @Override
            public void returnAsync(@NotNull CompletableFuture<?> future) {
                // Empty
            }

            @Override
            public @NotNull GenerationUnit unit() {
                return unit;
            }
        };
    }
}
