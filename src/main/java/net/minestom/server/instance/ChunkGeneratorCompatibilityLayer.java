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

    //    @Override
//    public List<CompletableFuture<SectionResult>> generate(Instance instance, GenerationRequest request) {
//        var chunkSections = new HashMap<Point, Set<Point>>();
//        switch (request.unit()) {
//            case CHUNK -> {
//                for (Point location : request.locations()) {
//                    chunkSections.put(location, new HashSet<>());
//                    for (int y = instance.getSectionMinY(); y <= instance.getSectionMaxY(); y++) {
//                        chunkSections.get(location).add(new Vec(location.x(), y, location.z()));
//                    }
//                }
//            }
//            case SECTION -> {
//                for (Point location : request.locations()) {
//                    chunkSections.computeIfAbsent(location.withY(0), k -> new HashSet<>()).add(location);
//                }
//            }
//        }
//        var futures = new ArrayList<CompletableFuture<SectionResult>>();
//        for (Map.Entry<Point, Set<Point>> entry : chunkSections.entrySet()) {
//            final CompatibilityChunk chunk = new CompatibilityChunk(instance, (int) entry.getKey().x(), (int) entry.getKey().z());
//            final ChunkGenerationBatch batch = new ChunkGenerationBatch((InstanceContainer) instance, chunk);
//            final ArrayList<CompletableFuture<SectionResult>> chunkFutures = new ArrayList<>();
//            for (int i = 0; i < entry.getValue().size(); i++) {
//                final CompletableFuture<SectionResult> future = new CompletableFuture<>();
//                futures.add(future);
//                chunkFutures.add(future);
//            }
//            batch.generate(chunkGenerator).thenAccept(c -> {
//                int i = 0;
//                for (Point point : entry.getValue()) {
//                    chunkFutures.get(i++).complete(new SectionResult(((CompatibilityChunk)c).getData((int) point.y()), point));
//                }
//            });
//        }
//        return futures;
//    }

    /**
     * Used to provide compatibility for old generators
     */
//    private static class CompatibilityChunk extends Chunk {
//
//
//        private static final RuntimeException UNSUPPORTED_OPERATION = new UnsupportedOperationException("Operation not supported for CompatibilityChunk!");
//        private final List<Section> sections;
//        private final List<SectionData> data;
//
//        public CompatibilityChunk(@NotNull Instance instance, int chunkX, int chunkZ) {
//            super(instance, chunkX, chunkZ, true);
//            var sectionsTemp = new Section[instance.getSectionMaxY() - instance.getSectionMinY()];
//            Arrays.setAll(sectionsTemp, value -> new Section());
//            this.sections = List.of(sectionsTemp);
//            var dataTemp = new SectionData[instance.getSectionMaxY() - instance.getSectionMinY()];
//            Arrays.setAll(dataTemp, value -> new SectionData(new SectionBlockCache(), Palette.biomes()));
//            this.data = List.of(dataTemp);
//        }
//
//        private GeneratedData getData(int y) {
//            final Section section = sections.get(y - instance.getSectionMinY());
//            final SectionData sectionData = data.get(y - instance.getSectionMinY());
//            if (section.getSkyLight().length == 0 && section.getBlockLight().length == 0) {
//                return new LegacySectionData(sectionData.blockCache(), sectionData.biomePalette(), section.getBlockLight(), section.getSkyLight());
//            } else {
//                return sectionData;
//            }
//        }
//
//        @Override
//        public void setBlock(int x, int y, int z, @NotNull Block block) {
//            data.get(ChunkUtils.getChunkCoordinate(y) - instance.getSectionMinY()).blockCache().setBlock(x, y, z, block);
//        }
//
//        @Override
//        public @NotNull List<Section> getSections() {
//            return sections;
//        }
//
//        @Override
//        public @NotNull Section getSection(int section) {
//            return sections.get(section - instance.getSectionMinY());
//        }
//
//        @Override
//        public @UnknownNullability Block getBlock(int x, int y, int z, @NotNull Condition condition) {
//            return data.get(ChunkUtils.getChunkCoordinate(y) - instance.getSectionMinY()).blockCache()
//                    .getBlock(ChunkUtils.toSectionRelativeCoordinate(x), ChunkUtils.toSectionRelativeCoordinate(y),
//                            ChunkUtils.toSectionRelativeCoordinate(z), condition);
//        }
//
//        @Override
//        public void setBiome(int x, int y, int z, @NotNull Biome biome) {
//            data.get(ChunkUtils.getChunkCoordinate(y) - instance.getSectionMinY()).biomePalette().set(ChunkUtils.toSectionRelativeCoordinate(x),
//                    ChunkUtils.toSectionRelativeCoordinate(y), ChunkUtils.toSectionRelativeCoordinate(z), biome.id());
//        }
//
//        @Override
//        public @NotNull Biome getBiome(int x, int y, int z) {
//            return MinecraftServer.getBiomeManager().getById(data.get(ChunkUtils.getChunkCoordinate(y) - instance.getSectionMinY()).biomePalette()
//                    .get(ChunkUtils.toSectionRelativeCoordinate(x), ChunkUtils.toSectionRelativeCoordinate(y),
//                            ChunkUtils.toSectionRelativeCoordinate(z)));
//        }
//
//        //region Unsupported operations
//
//        @Override
//        public boolean addViewer(@NotNull Player player) {
//            throw UNSUPPORTED_OPERATION;
//        }
//
//        @Override
//        public boolean removeViewer(@NotNull Player player) {
//            throw UNSUPPORTED_OPERATION;
//        }
//
//        @Override
//        public @NotNull Set<Player> getViewers() {
//            throw UNSUPPORTED_OPERATION;
//        }
//
//        @Override
//        public void setSection(GeneratedData sectionData, int y) {
//            throw UNSUPPORTED_OPERATION;
//        }
//
//        @Override
//        public void tick(long time) {
//            throw UNSUPPORTED_OPERATION;
//        }
//
//        @Override
//        public long getLastChangeTime() {
//            throw UNSUPPORTED_OPERATION;
//        }
//
//        @Override
//        public void sendChunk(@NotNull Player player) {
//            throw UNSUPPORTED_OPERATION;
//        }
//
//        @Override
//        public void sendChunk() {
//            throw UNSUPPORTED_OPERATION;
//        }
//
//        @Override
//        public @NotNull Chunk copy(@NotNull Instance instance, int chunkX, int chunkZ) {
//            throw UNSUPPORTED_OPERATION;
//        }
//
//        @Override
//        public void reset() {
//            throw UNSUPPORTED_OPERATION;
//        }
//
//        //endregion
//    }
}
