package generaloss.mc24.server.chunk;

public class ByteNibbleArray {

    private final byte[] array;

    public ByteNibbleArray(byte[] array) {
        this.array = array;
    }

    public ByteNibbleArray() {
        this(new byte[Chunk.VOLUME]);
    }

    public byte[] array() {
        return array;
    }


    private int indexBy(int x, int y, int z) {
        return (x * Chunk.AREA + y * Chunk.SIZE + z);
    }

    public void set(int x, int y, int z, int ID) {
        array[this.indexBy(x, y, z)] = (byte) ID;
    }

    public byte get(int x, int y, int z) {
        return array[this.indexBy(x, y, z)];
    }

    public boolean isOutOfBounds(int x, int y, int z) {
        return (x < 0 || y < 0 || z < 0 || x > Chunk.SIZE_BOUND || y > Chunk.SIZE_BOUND || z > Chunk.SIZE_BOUND);
    }

}
