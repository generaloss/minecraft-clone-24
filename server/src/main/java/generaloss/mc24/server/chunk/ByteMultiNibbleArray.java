package generaloss.mc24.server.chunk;

public class ByteMultiNibbleArray {

    private final int layersNumber;
    private final byte[] array;

    public ByteMultiNibbleArray(int layersNumber, byte[] array) {
        this.layersNumber = layersNumber;
        this.array = array;
    }

    public ByteMultiNibbleArray(int layersNumber) {
        this(layersNumber, new byte[layersNumber * Chunk.VOLUME]);
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

    public void set(int layer, int x, int y, int z, int ID) {
        array[this.indexBy(layer, x, y, z)] = (byte) ID;
    }

    public byte get(int layer, int x, int y, int z) {
        return array[this.indexBy(layer, x, y, z)];
    }

    public boolean isOutOfBounds(int layer, int x, int y, int z) {
        return (
            layer < 0 || x < 0 || y < 0 || z < 0 ||
            layer >= layersNumber || x > Chunk.SIZE_BOUND ||
            y > Chunk.SIZE_BOUND || z > Chunk.SIZE_BOUND
        );
    }

}
