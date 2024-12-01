package generaloss.mc24.server.world;

import generaloss.mc24.server.Directory;
import generaloss.mc24.server.block.BlockProperty;
import generaloss.mc24.server.block.BlockState;
import generaloss.mc24.server.chunk.Chunk;
import generaloss.mc24.server.chunk.ChunkCache;
import jpize.util.math.vector.Vec3i;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class BlockLightEngine <W extends World<C>, C extends Chunk<? extends W>> {

    private record Entry(int x, int y, int z, int channel, int level) { }

    public static final int MAX_LEVEL = 15;

    private final Queue<Entry> increaseQueue;
    private final ChunkCache<W, C> chunkCache;
    private final List<BlockLightIncreasedCallback<W, C>> blockLightIncreasedCallbacks;

    public BlockLightEngine(W world) {
        this.increaseQueue = new LinkedList<>();
        this.chunkCache = new ChunkCache<>(world);
        this.blockLightIncreasedCallbacks = new ArrayList<>();
    }

    public ChunkCache<W, C> chunkCache() {
        return chunkCache;
    }

    public void increase(C chunk, int x, int y, int z, int r, int g, int b) {
        if(chunk == null)
            return;
        if(r > 0) increaseQueue.add(new Entry(x, y, z, 0, r));
        if(g > 0) increaseQueue.add(new Entry(x, y, z, 1, g));
        if(b > 0) increaseQueue.add(new Entry(x, y, z, 2, b));
        if(!increaseQueue.isEmpty())
            chunkCache.cacheNeighborsFor(chunk);
        this.processIncrease();
        this.invokeIncreasedCallbacks(chunk, x, y, z, r, g, b);
    }

    public void processIncrease() {
        while(!increaseQueue.isEmpty()) {
            final Entry entry = increaseQueue.poll();
            final int channel = entry.channel;
            final int level = entry.level;

            for(int i = 0; i < 6; i++){
                final Directory dir = Directory.values()[i];
                final Vec3i normal = dir.getNormal();

                final int nx = (entry.x + normal.x);
                final int ny = (entry.y + normal.y);
                final int nz = (entry.z + normal.z);

                final int neighborLightLevel = chunkCache.getBlockLightLevel(nx, ny, nz, channel);

                if(neighborLightLevel >= level - 1)
                    continue;

                final BlockState neighborBlockState = chunkCache.getBlockState(nx, ny, nz);
                if(neighborBlockState == null)
                    continue;

                final int neighborBlockOpacity = neighborBlockState.properties().getInt(BlockProperty.OPACITY);
                final int targetLightLevel = (level - Math.max(1, neighborBlockOpacity));

                if(targetLightLevel > neighborLightLevel){
                    chunkCache.setBlockLightLevel(nx, ny, nz, channel, targetLightLevel);
                    increaseQueue.add(new Entry(nx, ny, nz, channel, targetLightLevel));
                }
            }
        }
    }


    public void registerIncreasedCallback(BlockLightIncreasedCallback<W, C> callback) {
        blockLightIncreasedCallbacks.add(callback);
    }

    public void unregisterIncreasedCallback(BlockLightIncreasedCallback<W, C> callback) {
        blockLightIncreasedCallbacks.remove(callback);
    }

    private void invokeIncreasedCallbacks(C chunk, int x, int y, int z, int r, int g, int b) {
        for(BlockLightIncreasedCallback<W, C> callback: blockLightIncreasedCallbacks)
            callback.invoke(chunk, x, y, z, r, g, b);
    }

}
