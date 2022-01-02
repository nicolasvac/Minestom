package net.minestom.server.instance.generator;

public interface UnitProperty {
    interface Section {
        int absoluteHeight();
    }

    interface Chunk {
        int chunkX();

        int chunkZ();
    }
}
