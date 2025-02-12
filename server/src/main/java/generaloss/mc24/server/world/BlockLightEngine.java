package generaloss.mc24.server.world;

import generaloss.mc24.server.Direction;
import generaloss.mc24.server.block.BlockState;
import generaloss.mc24.server.chunk.Chunk;
import generaloss.mc24.server.chunk.ChunkCache;
import generaloss.mc24.server.event.Events;
import jpize.util.math.vector.Vec3i;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class BlockLightEngine<W extends World<C>, C extends Chunk<? extends W>> {

    public static final int MAX_LEVEL = Chunk.SIZE_BOUND;

    private record Entry(int x, int y, int z, int channel, int level) { }


    private final ChunkCache<W, C> chunkCache;
    private final Queue<Entry> increaseQueue;
    private final Queue<Entry> decreaseQueue;
    private final Vec3i tmp_vec;

    public BlockLightEngine(W world) {
        this.chunkCache = new ChunkCache<>(world);
        this.increaseQueue = new ConcurrentLinkedQueue<>();
        this.decreaseQueue = new ConcurrentLinkedQueue<>();
        this.tmp_vec = new Vec3i();
    }

    public ChunkCache<W, C> chunkCache() {
        return chunkCache;
    }


    private void forEachDirection(int x, int y, int z, DirConsumer consumer) {
        for(int i = 0; i < 6; i++){
            final Vec3i normal = Direction.values()[i].getNormal();

            final int neighborX = (x + normal.x);
            final int neighborY = (y + normal.y);
            final int neighborZ = (z + normal.z);

            consumer.accept(neighborX, neighborY, neighborZ);
        }
    }

    private void addDirectionalEntries(Queue<Entry> queue, int x, int y, int z, int channel, int level) {
        this.forEachDirection(x, y, z, (neighborX, neighborY, neighborZ) -> {

            final BlockState blockstate = chunkCache.getBlockState(neighborX, neighborY, neighborZ);
            final int blockOpacity = blockstate.getBlockProperties().getInt("opacity");
            final int neighborLevel = (level - Math.max(1, blockOpacity));

            queue.add(new Entry(neighborX, neighborY, neighborZ, channel, neighborLevel));
        });
    }


    private void addIncreaseEntry(int x, int y, int z, int channel, int level) {
        if(level < 1)
            return;
        increaseQueue.add(new Entry(x, y, z, channel, Math.min(MAX_LEVEL, level)));
    }

    private void addIncreaseEntry(int x, int y, int z, int levelR, int levelG, int levelB) {
        this.addIncreaseEntry(x, y, z, 0, levelR);
        this.addIncreaseEntry(x, y, z, 1, levelG);
        this.addIncreaseEntry(x, y, z, 2, levelB);
    }

    public void increase(Chunk<?> chunk, int x, int y, int z, int levelR, int levelG, int levelB) {
        if(chunk == null)
            return;
        chunkCache.cacheNeighborsFor((C) chunk);

        this.addIncreaseEntry(x, y, z, levelR, levelG, levelB);
        this.processIncrease();
        Events.invokeLightIncreased(chunk, x, y, z, levelR, levelG, levelB);
    }

    public void fillGapWithNeighborMaxLight(Chunk<?> chunk, int x, int y, int z) {
        if(chunk == null)
            return;
        chunkCache.cacheNeighborsFor((C) chunk);

        tmp_vec.zero();
        this.forEachDirection(x, y, z, (neighborX, neighborY, neighborZ) -> {
            final int neighborLevelR = chunkCache.getBlockLightLevel(neighborX, neighborY, neighborZ, 0);
            final int neighborLevelG = chunkCache.getBlockLightLevel(neighborX, neighborY, neighborZ, 1);
            final int neighborLevelB = chunkCache.getBlockLightLevel(neighborX, neighborY, neighborZ, 2);

            tmp_vec.setMaxComps(neighborLevelR, neighborLevelG, neighborLevelB);
        });

        this.addIncreaseEntry(x, y, z, tmp_vec.x - 1, tmp_vec.y - 1, tmp_vec.z - 1);
        this.processIncrease();
        Events.invokeLightIncreased(chunk, x, y, z, tmp_vec.x, tmp_vec.y, tmp_vec.z);
    }

    public void processIncrease() {
        while(!increaseQueue.isEmpty()) {
            final Entry entry = increaseQueue.poll();
            final int x = entry.x;
            final int y = entry.y;
            final int z = entry.z;
            final int channel = entry.channel;
            final int level = entry.level;

            final int prevLevel = chunkCache.getBlockLightLevel(x, y, z, channel);
            if(level <= prevLevel)
                continue;

            chunkCache.setBlockLightLevel(x, y, z, channel, level);

            this.addDirectionalEntries(increaseQueue, x, y, z, channel, level);
        }
    }


    private void addDecreaseEntry(int x, int y, int z, int channel, int levelFrom) {
        if(levelFrom < 1)
            return;
        decreaseQueue.add(new Entry(x, y, z, channel, Math.min(MAX_LEVEL, levelFrom)));
    }

    private void addDecreaseEntry(int x, int y, int z, int levelFromR, int levelFromG, int levelFromB) {
        this.addDecreaseEntry(x, y, z, 0, levelFromR);
        this.addDecreaseEntry(x, y, z, 1, levelFromG);
        this.addDecreaseEntry(x, y, z, 2, levelFromB);
    }

    public void decrease(Chunk<?> chunk, int x, int y, int z, int levelFromR, int levelFromG, int levelFromB) {
        if(chunk == null)
            return;
        chunkCache.cacheNeighborsFor((C) chunk);

        this.addDecreaseEntry(x, y, z, levelFromR, levelFromG, levelFromB);
        this.processDecrease();
        Events.invokeLightIncreased(chunk, x, y, z, levelFromR, levelFromG, levelFromB); //! decrease
    }

    public void processDecrease() {
        while(!decreaseQueue.isEmpty()) {
            final Entry entry = decreaseQueue.poll();
            final int x = entry.x;
            final int y = entry.y;
            final int z = entry.z;
            final int channel = entry.channel;

            final int level = entry.level;
            if(level < 0)
                continue;

            final int blockLevel = chunkCache.getBlockLightLevel(x, y, z, channel);
            if(level > blockLevel)
                continue;
            if(blockLevel >= level + 1) {
                this.addDirectionalEntries(increaseQueue, x, y, z, channel, blockLevel);
                continue;
            }

            final BlockState blockState = chunkCache.getBlockState(x, y, z);
            final int glowing = blockState.getBlockProperties().getIntArray("glowing")[channel];
            if(glowing <= level)
                this.addIncreaseEntry(x, y, z, channel, glowing);

            chunkCache.setBlockLightLevel(x, y, z, channel, 0);
            this.addDirectionalEntries(decreaseQueue, x, y, z, channel, level);
        }

        this.processIncrease();
    }

}
