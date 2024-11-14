package generaloss.mc24.client.level;

public class Chunk {

    public static final int WIDTH = 16;
    public static final int AREA = WIDTH * WIDTH;
    public static final int HEIGHT = 256;

    private final int[] blocks;

    public Chunk() {
        this.blocks = new int[AREA * HEIGHT];
    }

    public void setBlock(int x, int y, int z, int block) {
        blocks[this.getIndex(x, y, z)] = block;
    }

    public int getBlock(int x, int y, int z) {
        return blocks[this.getIndex(x, y, z)];
    }

    private int getIndex(int x, int y, int z) {
        return (x * WIDTH * HEIGHT + y * WIDTH + z);
    }

}
