package generaloss.mc24.client.level;

import generaloss.mc24.client.level.mesh.ChunkMesh;
import generaloss.mc24.server.chunk.Chunk;
import generaloss.mc24.server.chunk.ChunkPos;

public class LevelChunk extends Chunk {

    private final WorldLevel level;
    private final ChunkPos position;
    private final int[] blocks;
    private ChunkMesh mesh;

    public LevelChunk(WorldLevel level, ChunkPos position) {
        this.level = level;
        this.position = position;
        this.blocks = new int[VOLUME];
    }

    public WorldLevel level() {
        return level;
    }

    public ChunkPos getPosition() {
        return position;
    }

    public ChunkMesh mesh() {
        return mesh;
    }


    public void setMesh(ChunkMesh mesh) {
        this.mesh = mesh;
    }

    public void freeMesh() {
        if(mesh == null)
            return;
        mesh.free();
        mesh = null;
    }


    private int indexBy(int x, int y, int z) {
        return (x * AREA + y * SIZE + z);
    }

    public void setBlock(int x, int y, int z, int block) {
        blocks[this.indexBy(x, y, z)] = block;
    }

    public int getBlock(int x, int y, int z) {
        if(y > SIZE - 1)
            return 0;
        return blocks[this.indexBy(x, y, z)];
    }


    public interface BlockConsumer {
        void accept(int x, int y, int z);
    }

    public void forEachBlock(BlockConsumer consumer) {
        for (int y = 0; y < SIZE; y++)
            for (int x = 0; x < SIZE; x++)
                for (int z = 0; z < SIZE; z++)
                    consumer.accept(x, y, z);
    }

}
