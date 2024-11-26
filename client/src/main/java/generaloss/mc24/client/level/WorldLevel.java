package generaloss.mc24.client.level;

import generaloss.mc24.client.Main;
import generaloss.mc24.client.chunk.ChunkTesselator;
import generaloss.mc24.server.block.Block;
import generaloss.mc24.server.chunk.Chunk;
import generaloss.mc24.server.chunk.ChunkPos;
import generaloss.mc24.server.registry.Registry;
import generaloss.mc24.server.world.World;
import jpize.util.Disposable;
import jpize.util.math.FastNoise;
import jpize.util.math.Maths;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class WorldLevel extends World implements Disposable {

    private final Main context;
    private final ChunkTesselator tesselator;
    private final LevelRenderer renderer;
    private final Map<ChunkPos, LevelChunk> chunks;

    public WorldLevel(Main context) {
        this.context = context;
        this.tesselator = new ChunkTesselator(context, this);
        this.renderer = new LevelRenderer(context, this);
        this.chunks = new HashMap<>();
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
        chunks.put(chunk.position(), chunk);
    }


    FastNoise noise = new FastNoise(Maths.random(0, 99)).setFrequency(1 / 64F);

    private void load(int chunkX, int chunkY, int chunkZ) {
        final ChunkPos position = new ChunkPos(chunkX, chunkY, chunkZ);
        final LevelChunk chunk = new LevelChunk(this, position, context.registries());

        final Registry<String, Block> blockRegistry = context.registries().block();
        chunk.forEach((x, y, z) -> {
            final int X = chunkX * Chunk.SIZE + x;
            final int Y = chunkY * Chunk.SIZE + y;
            final int Z = chunkZ * Chunk.SIZE + z;
            if(noise.get(X, Y, Z) > 0F)
                chunk.setBlockState(x, y, z, blockRegistry.get("stone").getDefaultState());
        });
        chunk.forEach((x, y, z) -> {
            if(!chunk.getBlockState(x, y, z).isBlockID("air") && (chunk.getBlockState(x, y + 1, z) == null || chunk.getBlockState(x, y + 1, z).isBlockID("air")))
                chunk.setBlockState(x, y, z, blockRegistry.get("grass_block").getDefaultState());
        });

        this.loadChunk(chunk);
        tesselator.tesselate(chunk);
    }

    public void loadChunks() {
        System.out.println("Loading chunks...");
        for(int y = -2; y < 2; y++){
            this.load(0, y, 0);
            for(int r = 0; r < 10; r++){
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
