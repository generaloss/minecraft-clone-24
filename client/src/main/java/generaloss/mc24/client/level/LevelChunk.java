package generaloss.mc24.client.level;

public class LevelChunk {

    public static final int WIDTH = 16;
    public static final int AREA = WIDTH * WIDTH;
    public static final int HEIGHT = 256;

    private final WorldLevel level;
    private final int[] blocks;

    public LevelChunk(WorldLevel level) {
        this.level = level;
        this.blocks = new int[AREA * HEIGHT];
    }

    public WorldLevel level() {
        return level;
    }


    private int indexBy(int x, int y, int z) {
        return (x * WIDTH * HEIGHT + y * WIDTH + z);
    }

    public void setBlock(int x, int y, int z, int block) {
        blocks[this.indexBy(x, y, z)] = block;
    }

    public int getBlock(int x, int y, int z) {
        return blocks[this.indexBy(x, y, z)];
    }


    public interface BlockConsumer {
        void accept(int x, int y, int z);
    }

    public void forEachBlock(BlockConsumer consumer) {
        for (int y = 0; y < HEIGHT; y++)
            for (int x = 0; x < WIDTH; x++)
                for (int z = 0; z < WIDTH; z++)
                    consumer.accept(x, y, z);
    }

}
