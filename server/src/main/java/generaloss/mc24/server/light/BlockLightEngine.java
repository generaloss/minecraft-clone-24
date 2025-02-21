package generaloss.mc24.server.light;

import generaloss.mc24.server.block.BlockProperty;
import generaloss.mc24.server.block.BlockState;
import generaloss.mc24.server.chunk.Chunk;
import generaloss.mc24.server.chunk.ChunkCache;
import generaloss.mc24.server.common.DirectionConsumer;
import generaloss.mc24.server.event.Events;
import generaloss.mc24.server.world.World;
import jpize.util.math.vector.Vec3i;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class BlockLightEngine<C extends Chunk> extends LightEngine {

    private final ChunkCache<C> chunkCache;
    private final Vec3i tmp_vec;
    private final Queue<BlockLightEntry> increaseQueue;
    private final Queue<BlockLightEntry> decreaseQueue;

    public BlockLightEngine(World<C> world) {
        this.chunkCache = new ChunkCache<>(world);
        this.increaseQueue = new ConcurrentLinkedQueue<>();
        this.decreaseQueue = new ConcurrentLinkedQueue<>();
        this.tmp_vec = new Vec3i();
    }

    public ChunkCache<C> chunkCache() {
        return chunkCache;
    }


    private void addIncreaseEntry(int x, int y, int z, int channel, int level) {
        if(level < 1)
            return;
        increaseQueue.add(new BlockLightEntry(x, y, z, channel, Math.min(MAX_LEVEL, level)));
    }

    private void addIncreaseEntryRGB(int x, int y, int z, int levelR, int levelG, int levelB) {
        this.addIncreaseEntry(x, y, z, 0, levelR);
        this.addIncreaseEntry(x, y, z, 1, levelG);
        this.addIncreaseEntry(x, y, z, 2, levelB);
    }

    public void increase(Chunk chunk, int x, int y, int z, int levelR, int levelG, int levelB) {
        chunkCache.initFor((C) chunk);

        this.addIncreaseEntryRGB(x, y, z, levelR, levelG, levelB);
        this.processIncrease();
        Events.invokeLightIncreased(chunk, x, y, z, levelR, levelG, levelB);
    }

    public void processIncrease() {
        while(!increaseQueue.isEmpty()) {
            final BlockLightEntry entry = increaseQueue.poll();

            final int x = entry.x();
            final int y = entry.y();
            final int z = entry.z();
            final int channel = entry.channel();

            final int prevLevel = chunkCache.getBlockLightLevel(x, y, z, channel);

            final int level = entry.level();
            if(level <= prevLevel)
                continue;

            chunkCache.setBlockLightLevel(x, y, z, channel, level);

            this.addDirectionalEntries(increaseQueue, x, y, z, channel, level);
        }
    }

    private void addDirectionalEntries(Queue<BlockLightEntry> queue, int x, int y, int z, int channel, int level) {
        DirectionConsumer.forEach(x, y, z, (neighborX, neighborY, neighborZ) -> {

            final BlockState blockstate = chunkCache.getBlockState(neighborX, neighborY, neighborZ);
            final int blockOpacity = blockstate.getBlockProperties().get(BlockProperty.OPACITY);
            final int neighborLevel = (level - Math.max(1, blockOpacity));

            queue.add(new BlockLightEntry(neighborX, neighborY, neighborZ, channel, neighborLevel));
        });
    }


    private void addDecreaseEntry(int x, int y, int z, int channel, int levelFrom) {
        if(levelFrom < 1)
            return;
        decreaseQueue.add(new BlockLightEntry(x, y, z, channel, Math.min(MAX_LEVEL, levelFrom)));
    }

    private void addDecreaseEntryRGB(int x, int y, int z, int levelFromR, int levelFromG, int levelFromB) {
        this.addDecreaseEntry(x, y, z, 0, levelFromR);
        this.addDecreaseEntry(x, y, z, 1, levelFromG);
        this.addDecreaseEntry(x, y, z, 2, levelFromB);
    }

    public void decrease(Chunk chunk, int x, int y, int z, int levelFromR, int levelFromG, int levelFromB) {
        chunkCache.initFor((C) chunk);

        this.addDecreaseEntryRGB(x, y, z, levelFromR, levelFromG, levelFromB);
        this.processDecrease();
        Events.invokeLightIncreased(chunk, x, y, z, levelFromR, levelFromG, levelFromB); //! decrease
    }

    public void processDecrease() {
        while(!decreaseQueue.isEmpty()) {
            final BlockLightEntry entry = decreaseQueue.poll();

            final int level = entry.level();
            if(level < 0)
                continue;

            final int x = entry.x();
            final int y = entry.y();
            final int z = entry.z();
            final int channel = entry.channel();

            final int blockLevel = chunkCache.getBlockLightLevel(x, y, z, channel);
            if(level > blockLevel)
                continue;
            if(blockLevel >= level + 1) {
                this.addDirectionalEntries(increaseQueue, x, y, z, channel, blockLevel);
                continue;
            }

            final BlockState blockState = chunkCache.getBlockState(x, y, z);
            final int[] glowing = blockState.getBlockProperties().get(BlockProperty.GLOWING);
            final int glowingLevel = glowing[channel];
            if(glowingLevel <= level)
                this.addIncreaseEntry(x, y, z, channel, glowingLevel);

            chunkCache.setBlockLightLevel(x, y, z, channel, 0);
            this.addDirectionalEntries(decreaseQueue, x, y, z, channel, level);
        }

        this.processIncrease();
    }


    public void fillGapWithNeighborMaxLight(Chunk chunk, int x, int y, int z) {
        if(chunk == null)
            return;
        chunkCache.initFor((C) chunk);

        tmp_vec.zero();
        DirectionConsumer.forEach(x, y, z, (neighborX, neighborY, neighborZ) -> {
            final int neighborLevelR = chunkCache.getBlockLightLevel(neighborX, neighborY, neighborZ, 0);
            final int neighborLevelG = chunkCache.getBlockLightLevel(neighborX, neighborY, neighborZ, 1);
            final int neighborLevelB = chunkCache.getBlockLightLevel(neighborX, neighborY, neighborZ, 2);

            tmp_vec.setMaxComps(neighborLevelR, neighborLevelG, neighborLevelB);
        });

        // minus opacity
        final BlockState blockstate = chunkCache.getBlockState(x, y, z);
        final int blockOpacity = blockstate.getBlockProperties().get(BlockProperty.OPACITY);
        tmp_vec.sub(
                Math.max(1, blockOpacity),
                Math.max(1, blockOpacity),
                Math.max(1, blockOpacity)
        );

        this.addIncreaseEntryRGB(x, y, z, tmp_vec.x, tmp_vec.y, tmp_vec.z);
        this.processIncrease();
        Events.invokeLightIncreased(chunk, x, y, z, tmp_vec.x, tmp_vec.y, tmp_vec.z);
    }

}
