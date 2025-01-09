package generaloss.mc24.server.world;

import generaloss.mc24.server.Direction;
import generaloss.mc24.server.block.BlockState;
import generaloss.mc24.server.chunk.Chunk;
import generaloss.mc24.server.chunk.ChunkCache;
import jpize.util.math.vector.Vec3i;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;

public class BlockLightEngine <W extends World<C>, C extends Chunk<? extends W>> {

    private record Entry(int x, int y, int z, int channel, int level) { }

    public static final int MAX_LEVEL = Chunk.SIZE_BOUND;

    private final Queue<Entry> queue;
    private final ChunkCache<W, C> chunkCache;
    private final List<BlockLightIncreasedCallback<W, C>> blockLightIncreasedCallbacks;

    public BlockLightEngine(W world) {
        this.queue = new ConcurrentLinkedQueue<>();
        this.chunkCache = new ChunkCache<>(world);
        this.blockLightIncreasedCallbacks = new CopyOnWriteArrayList<>();
    }

    public ChunkCache<W, C> chunkCache() {
        return chunkCache;
    }

    public void increase(Chunk<?> chunk, int x, int y, int z, int r, int g, int b) {
        if(chunk == null || (r == 0 && g == 0 && b == 0))
            return;

        queue.add(new Entry(x, y, z, 0, r));
        queue.add(new Entry(x, y, z, 1, g));
        queue.add(new Entry(x, y, z, 2, b));

        chunkCache.cacheNeighborsFor((C) chunk);
        this.processIncrease();
        this.invokeIncreasedCallbacks((C) chunk, x, y, z, r, g, b);
    }

    public void processIncrease() {
        while(!queue.isEmpty()) {
            final Entry entry = queue.poll();
            final int x = entry.x;
            final int y = entry.y;
            final int z = entry.z;
            final int channel = entry.channel;
            final int level = entry.level;

            final int prevLevel = chunkCache.getBlockLightLevel(x, y, z, channel);
            if(level <= prevLevel)
                continue;

            chunkCache.setBlockLightLevel(x, y, z, channel, level);

            for(int i = 0; i < 6; i++) {
                final Direction dir = Direction.values()[i];
                final Vec3i normal = dir.getNormal();

                final int nx = (x + normal.x);
                final int ny = (y + normal.y);
                final int nz = (z + normal.z);

                final BlockState neighborBlockstate = chunkCache.getBlockState(nx, ny, nz);
                final int neighborBlockOpacity = neighborBlockstate.blockProperties().getInt("opacity");
                final int targetNeighborLevel = (level - Math.max(1, neighborBlockOpacity));

                queue.add(new Entry(nx, ny, nz, channel, targetNeighborLevel));
            }
        }
    }


    public void decrease(Chunk<?> chunk, int x, int y, int z, int r, int g, int b) {
        if(chunk == null || (r == MAX_LEVEL && g == MAX_LEVEL && b == MAX_LEVEL))
            return;

        queue.add(new Entry(x, y, z, 0, r));
        queue.add(new Entry(x, y, z, 1, g));
        queue.add(new Entry(x, y, z, 2, b));

        chunkCache.cacheNeighborsFor((C) chunk);
        this.processDecrease();
        this.invokeIncreasedCallbacks((C) chunk, x, y, z, r, g, b); //! decrease
    }

    public void processDecrease() {
        while(!queue.isEmpty()) {
            final Entry entry = queue.poll();
            final int x = entry.x;
            final int y = entry.y;
            final int z = entry.z;
            final int channel = entry.channel;
            final int level = entry.level;

            final int prevLevel = chunkCache.getBlockLightLevel(x, y, z, channel);
            if(level <= prevLevel)
                continue;

            chunkCache.setBlockLightLevel(x, y, z, channel, level);

            for(int i = 0; i < 6; i++) {
                final Direction dir = Direction.values()[i];
                final Vec3i normal = dir.getNormal();

                final int nx = (x + normal.x);
                final int ny = (y + normal.y);
                final int nz = (z + normal.z);

                final BlockState neighborBlockstate = chunkCache.getBlockState(x, y, z);
                final int neighborBlockOpacity = neighborBlockstate.blockProperties().getInt("opacity");
                final int targetNeighborLevel = (level - Math.max(1, neighborBlockOpacity));

                queue.add(new Entry(nx, ny, nz, channel, targetNeighborLevel));
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
