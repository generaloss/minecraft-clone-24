package generaloss.mc24.server.column;

import generaloss.mc24.server.chunk.Chunk;

import java.util.Arrays;

public class ColumnIntArray {

    private final int[] array;

    public ColumnIntArray(int[] array) {
        this.array = array;
    }

    public ColumnIntArray() {
        this.array = new int[Chunk.AREA];
    }

    public ColumnIntArray(int initValue) {
        this();
        Arrays.fill(array, initValue);
    }

    public int[] array() {
        return array;
    }


    private int indexBy(int x, int z) {
        return (x * Chunk.SIZE + z);
    }

    public void set(int x, int z, int value) {
        array[this.indexBy(x, z)] = (byte) value;
    }

    public int get(int x, int z) {
        return array[this.indexBy(x, z)];
    }

    public boolean isOutOfBounds(int x, int z) {
        return (x < 0 || z < 0 || x > Chunk.SIZE_BOUND || z > Chunk.SIZE_BOUND);
    }

}
