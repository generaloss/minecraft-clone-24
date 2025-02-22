package generaloss.mc24.server.chunk;

import java.util.Arrays;

public class ChunkByteArray {

    private final byte[] array;

    public ChunkByteArray(byte[] array) {
        this.array = array;
    }

    public ChunkByteArray() {
        this(new byte[Chunk.VOLUME]);
    }

    public ChunkByteArray(byte initValue) {
        this();
        Arrays.fill(array, initValue);
    }

    public byte[] array() {
        return array;
    }


    private int indexBy(int x, int y, int z) {
        return (x * Chunk.AREA + y * Chunk.SIZE + z);
    }

    public void set(int x, int y, int z, int value) {
        try{
            array[this.indexBy(x, y, z)] = (byte) value;
        }catch(IndexOutOfBoundsException e){
            System.err.println("Index out of bounds on: " + x + ", " + y + ", " + z);
        }
    }

    public byte get(int x, int y, int z) {
        try{
            return array[this.indexBy(x, y, z)];
        }catch(IndexOutOfBoundsException e){
            System.err.println("Index out of bounds on: " + x + ", " + y + ", " + z);
            return 0;
        }
    }

    public boolean isOutOfBounds(int x, int y, int z) {
        return (x < 0 || y < 0 || z < 0 || x > Chunk.SIZE_BOUND || y > Chunk.SIZE_BOUND || z > Chunk.SIZE_BOUND);
    }

}
