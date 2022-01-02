package net.minestom.server.instance.generator;

public interface UnitProperty {

    int sizeX();

    int sizeY();

    int sizeZ();

    interface Section extends UnitProperty {
        int absoluteHeight();

        @Override
        default int sizeX() {
            return 16;
        }

        @Override
        default int sizeY() {
            return 16;
        }

        @Override
        default int sizeZ() {
            return 16;
        }
    }

    interface Chunk extends UnitProperty {
        int chunkX();

        int chunkZ();

        @Override
        default int sizeX() {
            return 16;
        }

        @Override
        int sizeY();

        @Override
        default int sizeZ() {
            return 16;
        }
    }

    interface Region extends UnitProperty {
        int regionX();

        int regionZ();

        @Override
        default int sizeX() {
            return 512;
        }

        @Override
        int sizeY();

        @Override
        default int sizeZ() {
            return 512;
        }
    }
}
