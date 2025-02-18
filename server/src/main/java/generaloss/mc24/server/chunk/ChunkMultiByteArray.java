package generaloss.mc24.server.chunk;

import java.util.Arrays;

public class ChunkMultiByteArray {

    private final int layersNumber;
    private final byte[] array;

    public ChunkMultiByteArray(int layersNumber, byte[] array) {
        this.layersNumber = layersNumber;
        this.array = array;
    }

    public ChunkMultiByteArray(int layersNumber) {
        this(layersNumber, new byte[layersNumber * Chunk.VOLUME]);
    }

    public ChunkMultiByteArray(int layersNumber, byte initValue) {
        this(layersNumber);
        Arrays.fill(array, initValue);
    }

    public int getLayersNumber() {
        return layersNumber;
    }

    public byte[] array() {
        return array;
    }


    private int indexBy(int layer, int x, int y, int z) {
        return (layer * Chunk.VOLUME + x * Chunk.AREA + y * Chunk.SIZE + z);
    }

    public void set(int layer, int x, int y, int z, int value) {
        array[this.indexBy(layer, x, y, z)] = (byte) value;
    }

    public byte get(int layer, int x, int y, int z) {
        return array[this.indexBy(layer, x, y, z)];
    }

    public boolean isOutOfBounds(int x, int y, int z) {
        return (
            x < 0 || y < 0 || z < 0 ||
            x > Chunk.SIZE_BOUND ||
            y > Chunk.SIZE_BOUND || z > Chunk.SIZE_BOUND
        );
    }

    public boolean isOutOfBounds(int layer, int x, int y, int z) {
        return (layer < 0 || layer >= layersNumber || this.isOutOfBounds(x, y, z));
    }

}
