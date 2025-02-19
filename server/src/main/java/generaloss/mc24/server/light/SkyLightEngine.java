package generaloss.mc24.server.light;

import generaloss.mc24.server.block.BlockProperty;
import generaloss.mc24.server.block.BlockState;
import generaloss.mc24.server.chunk.Chunk;
import generaloss.mc24.server.chunk.ChunkCache;
import generaloss.mc24.server.column.ChunkColumn;
import generaloss.mc24.server.column.ColumnCache;
import generaloss.mc24.server.column.ColumnHeightMap;
import generaloss.mc24.server.common.DirectionConsumer;
import generaloss.mc24.server.world.World;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class SkyLightEngine<C extends Chunk> extends LightEngine {

    private final ColumnCache<C> columnCache;
    private final ChunkCache<C> chunkCache;
    private final Queue<SkyLightEntry> increaseQueue;
    private final Queue<SkyLightEntry> decreaseQueue;

    public SkyLightEngine(World<C> world) {
        this.columnCache = new ColumnCache<>(world);
        this.chunkCache = new ChunkCache<>(world);
        this.increaseQueue = new ConcurrentLinkedQueue<>();
        this.decreaseQueue = new ConcurrentLinkedQueue<>();
    }

    public ChunkCache<C> chunkCache() {
        return chunkCache;
    }

    public ColumnCache<C> columnCache() {
        return columnCache;
    }

    private void addIncreaseEntry(int x, int y, int z, int level) {
        if(level < 1)
            return;
        increaseQueue.add(new SkyLightEntry(x, y, z, Math.min(MAX_LEVEL, level)));
    }

    public void increase(Chunk chunk, int x, int y, int z, int level) {
        if(chunk == null)
            return;
        chunkCache.initFor((C) chunk);

        this.addIncreaseEntry(x, y, z, level);
        this.processIncrease();
    }

    public void processIncrease() {
        while(!increaseQueue.isEmpty()) {
            final SkyLightEntry entry = increaseQueue.poll();

            final int x = entry.x();
            final int y = entry.y();
            final int z = entry.z();

            final int prevLevel = chunkCache.getSkyLightLevel(x, y, z);

            final int level = entry.level();
            if(level <= prevLevel)
                continue;

            chunkCache.setSkyLightLevel(x, y, z, level);

            this.addDirectionalEntries(increaseQueue, x, y, z, level);
        }
    }

    private void addDirectionalEntries(Queue<SkyLightEntry> queue, int x, int y, int z, int level) {
        DirectionConsumer.forEach(x, y, z, (neighborX, neighborY, neighborZ) -> {

            final BlockState blockstate = chunkCache.getBlockState(neighborX, neighborY, neighborZ);
            final int blockOpacity = blockstate.getBlockProperties().get(BlockProperty.OPACITY);
            final int neighborLevel = (level - Math.max(1, blockOpacity));

            queue.add(new SkyLightEntry(neighborX, neighborY, neighborZ, neighborLevel));
        });
    }


    private void addDecreaseEntry(int x, int y, int z, int levelFrom) {
        if(levelFrom < 1)
            return;
        decreaseQueue.add(new SkyLightEntry(x, y, z, Math.min(MAX_LEVEL, levelFrom)));
    }

    public void decrease(Chunk chunk, int x, int y, int z, int levelFrom) {
        if(chunk == null)
            return;
        chunkCache.initFor((C) chunk);

        this.addDecreaseEntry(x, y, z, levelFrom);
        this.processDecrease();
    }

    public void processDecrease() {
        while(!decreaseQueue.isEmpty()) {
            final SkyLightEntry entry = decreaseQueue.poll();

            final int level = entry.level();
            if(level < 0)
                continue;

            final int x = entry.x();
            final int y = entry.y();
            final int z = entry.z();

            final int blockLevel = chunkCache.getSkyLightLevel(x, y, z);
            if(level > blockLevel)
                continue;
            if(blockLevel >= level + 1) {
                this.addDirectionalEntries(increaseQueue, x, y, z, blockLevel);
                continue;
            }

            chunkCache.setSkyLightLevel(x, y, z, 0);
            this.addDirectionalEntries(decreaseQueue, x, y, z, level);
        }

        this.processIncrease();
    }



    public void diffuseSkyLight(ChunkColumn<C> column) {
        columnCache.initFor(column);

        column.forEach((localX, localZ) -> {
            final int height = (column.getHeight(localX, localZ) + 1);

            int maxNeighborHeight = height;
            for(int i = -1; i < 2; i++) {
                for(int j = -1; j < 2; j++) {
                    if(i == 0 && j == 0)
                        continue;

                    final int neighborHeight = (columnCache.getHeight(localX + i, localZ + j) + 1);
                    maxNeighborHeight = Math.max(maxNeighborHeight, neighborHeight);
                }
            }

            for(int y = height; y <= maxNeighborHeight; y++)
                column.setSkyLightLevel(localX, y, localZ, MAX_LEVEL);
        });
    }


    public void onHeightUpdatedUp(ChunkColumn<C> column, int localX, int localZ, int prevHeight, int newHeight) {
        if(prevHeight == ColumnHeightMap.NO_HEIGHT)
            return;

        columnCache.initFor(column);
        for(int y = prevHeight + 1; y <= newHeight; y++) {
            System.out.println("set to 0 at y=" + y + ": " + column.setSkyLightLevel(localX, y, localZ, 0));

            for(int i = -1; i < 2; i++){
                for(int j = -1; j < 2; j++){
                    if(i == 0 && j == 0)
                        continue;
                    columnCache.setSkyLightLevel(localX + i, y, localZ + j, MAX_LEVEL);
                }
            }
        }

        final int y = (newHeight + 1);
        for(int i = -1; i < 2; i++)
            for(int j = -1; j < 2; j++)
                columnCache.setSkyLightLevel(localX + i, y, localZ + j, MAX_LEVEL);
    }

    public void onHeightUpdatedDown(ChunkColumn<C> column, int localX, int localZ, int prevHeight, int newHeight) {
        if(prevHeight == ColumnHeightMap.NO_HEIGHT)
            return;

        for(int y = newHeight; y <= prevHeight; y++)
            column.setSkyLightLevel(localX, y, localZ, MAX_LEVEL);
    }

}
