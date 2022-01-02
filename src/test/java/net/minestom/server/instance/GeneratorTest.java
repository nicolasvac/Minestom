package net.minestom.server.instance;

import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.generator.GenerationRequest;
import net.minestom.server.instance.generator.GenerationUnit;
import net.minestom.server.instance.generator.Generator;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
    public void placement() {
        Section[] sections = new Section[16];
        for (int i = 0; i < sections.length; i++) {
            sections[i] = new Section();
        }
        GenerationUnit.Chunk chunkUnit = GeneratorImpl.createChunk(0, 5,
                List.of(new GeneratorImpl.ChunkEntry(List.of(sections), 0, 0)));

        Generator generator = (request, unit) -> {
            for (var test : unit.units()) {
                test.modifier().setBlock(0, 0, 0, Block.STONE);
                test.modifier().setBlock(1, 1, 0, Block.STONE);
            }
        };

        generator.generate(DUMMY_REQUEST, chunkUnit);
        assertEquals(Block.STONE.stateId(), sections[0].blockPalette().get(0, 0, 0));
        assertEquals(Block.STONE.stateId(), sections[0].blockPalette().get(1, 1, 0));
        assertEquals(0, sections[0].blockPalette().get(0, 1, 0));
    }
}
