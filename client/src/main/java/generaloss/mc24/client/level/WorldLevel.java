package generaloss.mc24.client.level;

import generaloss.mc24.client.Main;
import generaloss.mc24.client.level.renderer.ChunkTesselator;
import generaloss.mc24.client.level.renderer.LevelRenderer;
import generaloss.mc24.server.chunk.Chunk;
import generaloss.mc24.server.chunk.ChunkPos;
import jpize.util.Disposable;
import jpize.util.math.FastNoise;
import jpize.util.math.Maths;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class WorldLevel implements Disposable {

    private final ChunkTesselator tesselator;
    private final LevelRenderer renderer;
    private final Map<ChunkPos, LevelChunk> chunks;

    public WorldLevel(Main context) {
        this.tesselator = new ChunkTesselator(context, this);
        this.renderer = new LevelRenderer(context, this);
        this.chunks = new HashMap<>();
        this.loadChunks();
    }

    public ChunkTesselator tesselator() {
        return tesselator;
    }

    public LevelRenderer renderer() {
        return renderer;
    }


    public Collection<LevelChunk> getChunks() {
        return chunks.values();
    }

    public LevelChunk getChunk(ChunkPos pos) {
        return chunks.get(pos);
    }

    public void loadChunk(LevelChunk chunk) {
        chunks.put(chunk.getPosition(), chunk);
    }


    FastNoise noise = new FastNoise(Maths.random(0, 99)).setFrequency(1 / 64F);

    private void load(int chunkX, int chunkY, int chunkZ) {
        final LevelChunk chunk = new LevelChunk(this, new ChunkPos(chunkX, chunkY, chunkZ));

        chunk.forEachBlock((x, y, z) -> {
            final int X = chunkX * Chunk.SIZE + x;
            final int Y = chunkY * Chunk.SIZE + y;
            final int Z = chunkZ * Chunk.SIZE + z;
            if(noise.get(X, Y, Z) > 0F)
                chunk.setBlock(x, y, z, 3);
        });
        chunk.forEachBlock((x, y, z) -> {
            if(chunk.getBlock(x, y, z) != 0 && chunk.getBlock(x, y + 1, z) == 0)
                chunk.setBlock(x, y, z, 1);
        });

        this.loadChunk(chunk);
        tesselator.tesselate(chunk);
    }

    private void loadChunks() {
        System.out.println("Loading chunks...");
        for(int y = -8; y < 0; y++){
            this.load(0, y, 0);
            for(int r = 0; r < 12; r++){
                for(int i = -r; i <= r; i++)
                    this.load(i, y, r);
                for(int i = -r + 1; i <= r; i++)
                    this.load(r, y, i);
                for(int i = -r + 1; i <= r; i++)
                    this.load(i, y, -r);
                for(int i = -r; i <= r - 1; i++)
                    this.load(-r, y, i);
            }
        }
    }


    @Override
    public void dispose() {
        for(LevelChunk chunk: this.getChunks())
            chunk.freeMesh();
        tesselator.dispose();
    }

}
