package net.minestom.server.instance;

import net.minestom.server.instance.batch.ChunkBatch;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.generator.GenerationRequest;
import net.minestom.server.instance.generator.GenerationUnit;
import net.minestom.server.instance.generator.SpecializedGenerator;
import org.jetbrains.annotations.NotNull;

/**
 * Provides full compatibility for the deprecated {@link ChunkGenerator}
 */
class ChunkGeneratorCompatibilityLayer implements SpecializedGenerator<GenerationRequest.Chunks> {
    private final ChunkGenerator chunkGenerator;

    public ChunkGeneratorCompatibilityLayer(ChunkGenerator chunkGenerator) {
        this.chunkGenerator = chunkGenerator;
    }

    public ChunkGenerator getChunkGenerator() {
        return chunkGenerator;
    }

    @Override
    public void generate(@NotNull GenerationRequest.Chunks request) {
        for (GenerationUnit.Chunk chunk : request.chunks()) {
            ChunkBatch batch = new ChunkBatch() {
                @Override
                public void setBlock(int x, int y, int z, @NotNull Block block) {
                    chunk.modifier().setBlock(x, y, z, block);
                }
            };
            chunkGenerator.generateChunkData(batch, chunk.chunkX(), chunk.chunkZ());
        }
    }

    @Override
    public @NotNull Class<GenerationRequest.Chunks> requiredSubtype() {
        return GenerationRequest.Chunks.class;
    }
}
