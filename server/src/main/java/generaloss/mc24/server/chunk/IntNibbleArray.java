package generaloss.mc24.server.chunk;

public class IntNibbleArray {

    private final int[] array;

    public IntNibbleArray() {
        this.array = new int[Chunk.VOLUME];
    }

    public int[] array() {
        return array;
    }


    private int indexBy(int x, int y, int z) {
        return (x * Chunk.AREA + y * Chunk.SIZE + z);
    }

    public void set(int x, int y, int z, int ID) {
        array[this.indexBy(x, y, z)] = ID;
    }

    public int get(int x, int y, int z) {
        return array[this.indexBy(x, y, z)];
    }

    public boolean isOutOfBounds(int x, int y, int z) {
        return (x < 0 || y < 0 || z < 0 || x > Chunk.SIZE_BOUND || y > Chunk.SIZE_BOUND || z > Chunk.SIZE_BOUND);
    }

}
