package net.minestom.server.instance;

import net.minestom.server.coordinate.Vec;
import net.minestom.server.instance.batch.ChunkBatch;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.generator.GenerationRequest;
import net.minestom.server.instance.generator.GenerationUnit;
import net.minestom.server.instance.generator.SpecializedGenerator;
import net.minestom.server.instance.generator.UnitProperty;
import org.jetbrains.annotations.NotNull;

/**
 * Provides full compatibility for the deprecated {@link ChunkGenerator}
 */
class ChunkGeneratorCompatibilityLayer implements SpecializedGenerator<GenerationUnit.Chunk> {
    private final ChunkGenerator chunkGenerator;

    public ChunkGeneratorCompatibilityLayer(ChunkGenerator chunkGenerator) {
        this.chunkGenerator = chunkGenerator;
    }

    public ChunkGenerator getChunkGenerator() {
        return chunkGenerator;
    }

    public void test(@NotNull GenerationRequest request, @NotNull GenerationUnit unit) {
        if (unit instanceof GenerationUnit.Chunk chunk) {
            // A full chunk has been requested, can simply fill
            chunk.modifier().fill(new Vec(0, 0, 0), new Vec(16, 50, 0), Block.STONE);
        } else {
            // Fallback to section generation
            for (GenerationUnit.Section section : request.sections()) {
                final UnitProperty.Section property = request.sectionProperty(section);
                final int height = property.absoluteHeight();
                if (height < 0 || height > 50) continue;
                for (int x = 0; x < 16; x++) {
                    for (int z = 0; z < 16; z++) {
                        for (int y = 0; y < 16; y++) {
                            if (height + y > 50) continue;
                            section.modifier().setBlock(x, y, z, Block.STONE);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void generate(@NotNull GenerationRequest request, GenerationUnit.@NotNull Chunk unit) {
        ChunkBatch batch = new ChunkBatch() {
            @Override
            public void setBlock(int x, int y, int z, @NotNull Block block) {
                unit.modifier().setBlock(x, y, z, block);
            }
        };
        chunkGenerator.generateChunkData(batch, 0, 0);
    }

    @Override
    public @NotNull Class<GenerationUnit.Chunk> requiredSubtype() {
        return GenerationUnit.Chunk.class;
    }
}
