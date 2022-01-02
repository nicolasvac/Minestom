package net.minestom.server.instance;

import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.generator.GenerationRequest;
import net.minestom.server.instance.generator.GenerationUnit;
import net.minestom.server.instance.generator.Generator;
import net.minestom.server.world.DimensionType;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertSame;

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
        Instance instance = createInstance();
        Chunk chunk = new DynamicChunk(instance, 0, 0);
        GenerationUnit.Chunk chunkUnit = GeneratorImpl.createChunk(instance.getSectionMinY(), instance.getSectionMaxY(),
                List.of(chunk));

        Generator generator = (request, unit) -> {
            for (var test : unit.units()) {
                test.modifier().setBlock(0, 0, 0, Block.STONE);
                test.modifier().setBlock(1, 1, 0, Block.STONE);
            }
        };

        generator.generate(DUMMY_REQUEST, chunkUnit);
        assertSame(Block.STONE, chunk.getBlock(0, 0, 0));
        assertSame(Block.STONE, chunk.getBlock(1, 1, 0));
    }

    static Instance createInstance() {
        return new InstanceContainer(UUID.randomUUID(), DimensionType.OVERWORLD);
    }
}
