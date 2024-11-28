package generaloss.mc24.server.chunk;

public class ByteNibbleArray {

    private final byte[] array;

    public ByteNibbleArray() {
        this.array = new byte[Chunk.VOLUME];
    }

    public byte[] array() {
        return array;
    }


    private int indexBy(int x, int y, int z) {
        return (x * Chunk.AREA + y * Chunk.SIZE + z);
    }

    public boolean set(int x, int y, int z, int ID) {
        if(x < 0 || x >= Chunk.SIZE || y < 0 || y >= Chunk.SIZE || z < 0 || z >= Chunk.SIZE)
            return false;
        array[this.indexBy(x, y, z)] = (byte) ID;
        return true;
    }

    public byte get(int x, int y, int z) {
        if(x < 0 || x >= Chunk.SIZE || y < 0 || y >= Chunk.SIZE || z < 0 || z >= Chunk.SIZE)
            return -1;
        return array[this.indexBy(x, y, z)];
    }

}
