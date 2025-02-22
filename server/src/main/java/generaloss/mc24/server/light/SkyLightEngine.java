package generaloss.mc24.server.light;

import generaloss.mc24.server.block.BlockProperty;
import generaloss.mc24.server.block.BlockState;
import generaloss.mc24.server.chunk.Chunk;
import generaloss.mc24.server.column.ChunkColumn;
import generaloss.mc24.server.column.ColumnCache;
import generaloss.mc24.server.column.ColumnHeightMap;
import generaloss.mc24.server.common.DirectionConsumer;
import generaloss.mc24.server.event.Events;
import generaloss.mc24.server.world.World;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class SkyLightEngine<C extends Chunk> extends LightEngine {

    private final ColumnCache<C> columnCache;
    private final Queue<SkyLightEntry> increaseQueue;
    private final Queue<SkyLightEntry> decreaseQueue;

    public SkyLightEngine(World<C> world) {
        this.columnCache = new ColumnCache<>(world);
        this.increaseQueue = new ConcurrentLinkedQueue<>();
        this.decreaseQueue = new ConcurrentLinkedQueue<>();
    }

    public ColumnCache<C> columnCache() {
        return columnCache;
    }

    private void addIncreaseEntry(int x, int y, int z, int level) {
        if(level < 1)
            return;
        increaseQueue.add(new SkyLightEntry(x, y, z, Math.min(MAX_LEVEL, level)));
    }

    public void processIncrease() {
        while(!increaseQueue.isEmpty()) {
            final SkyLightEntry entry = increaseQueue.poll();

            final int x = entry.x();
            final int y = entry.y();
            final int z = entry.z();
            final int level = entry.level();

            final int prevLevel = columnCache.getSkyLightLevel(x, y, z);
            if(level <= prevLevel)
                continue;

            columnCache.setSkyLightLevel(x, y, z, level);

            this.addDirectionalEntries(increaseQueue, x, y, z, level);
        }
    }

    private void addDirectionalEntries(Queue<SkyLightEntry> queue, int x, int y, int z, int level) {
        DirectionConsumer.forEach(x, y, z, (neighborX, neighborY, neighborZ) -> {

            final BlockState blockstate = columnCache.getBlockState(neighborX, neighborY, neighborZ);
            final int blockOpacity = blockstate.getBlockProperties().get(BlockProperty.OPACITY);
            final int neighborLevel = (level - Math.max(1, blockOpacity));
            if(neighborLevel < 0)
                return;

            queue.add(new SkyLightEntry(neighborX, neighborY, neighborZ, neighborLevel));
        });
    }


    private void addDecreaseEntry(int x, int y, int z, int levelFrom) {
        if(levelFrom < 1)
            return;
        decreaseQueue.add(new SkyLightEntry(x, y, z, Math.min(MAX_LEVEL, levelFrom)));
    }

    public void processDecrease() {
        while(!decreaseQueue.isEmpty()) {
            final SkyLightEntry entry = decreaseQueue.poll();

            final int x = entry.x();
            final int y = entry.y();
            final int z = entry.z();
            final int level = entry.level();

            final int blockLevel = columnCache.getSkyLightLevel(x, y, z);
            if(level > blockLevel)
                continue;

            if(blockLevel >= level + 1) {
                this.addDirectionalEntries(increaseQueue, x, y, z, blockLevel);
                continue;
            }

            columnCache.setSkyLightLevel(x, y, z, 0);
            this.addDirectionalEntries(decreaseQueue, x, y, z, level);
        }

        this.processIncrease();
    }


    public void decrease(ChunkColumn<?> column, int x, int y, int z, int levelFrom) {
        columnCache.initFor((ChunkColumn<C>) column);
        this.addDecreaseEntry(x, y, z, levelFrom);
        this.processDecrease();
    }


    public void initChunkSkyLight(ChunkColumn<C> column) {
        columnCache.initFor(column);

        column.forEach((localX, localZ) -> {
            final int height = (column.getHeight(localX, localZ) + 1);
            if(height == ColumnHeightMap.NO_HEIGHT)
                return;

            int maxNeighborHeight = height;
            for(int x = (localX - 1); x < (localX + 2); x++){
                for(int z = (localZ - 1); z < (localZ + 2); z++){
                    if(x == localX && z == localZ)
                        continue;

                    final int neighborHeight = (columnCache.getHeight(x, z) + 1);
                    if(neighborHeight == ColumnHeightMap.NO_HEIGHT)
                        continue;

                    maxNeighborHeight = Math.max(maxNeighborHeight, neighborHeight);
                }
            }

            for(int y = height; y <= maxNeighborHeight; y++)
                this.addIncreaseEntry(localX, y, localZ, MAX_LEVEL);
            this.processIncrease();
        });
    }

    public void fillGapWithNeighborMaxLight(ChunkColumn<?> column, int localX, int y, int localZ, int blockOpacity) {
        columnCache.initFor((ChunkColumn<C>) column);

        final int[] maxLevel = new int[1];
        DirectionConsumer.forEach(localX, y, localZ, (neighborX, neighborY, neighborZ) -> {
            final int neighborLevel = columnCache.getSkyLightLevel(neighborX, neighborY, neighborZ);
            System.out.println("neighborLevel: " + neighborLevel);
            maxLevel[0] = Math.max(maxLevel[0], neighborLevel);
        });

        System.out.println("fill gap with " + (maxLevel[0] - Math.max(1, blockOpacity)) + "(max: " + maxLevel[0] + ")");
        maxLevel[0] -= Math.max(1, blockOpacity);

        this.addIncreaseEntry(localX, y, localZ, maxLevel[0]);
        this.processIncrease();
        Events.invokeSkyLightChanged(column, localX, localZ, y, y, maxLevel[0]);
    }

    public void onHeightUpdatedUp(ChunkColumn<C> column, int localX, int localZ, int prevHeight, int newHeight) {
        // decrease under placed block
        final int lowHeight = (prevHeight + 1);
        for(int y = lowHeight; y <= newHeight; y++) {
            final int levelFrom = column.getSkyLightLevel(localX, y, localZ);
            this.addDecreaseEntry(localX, y, localZ, levelFrom);
        }

        this.processDecrease();
        Events.invokeSkyLightChanged(column, localX, localZ, lowHeight, newHeight, 0);
    }

    public void onHeightUpdatedDown(ChunkColumn<C> column, int localX, int localZ, int prevHeight, int newHeight) {
        // increase under destroyed block
        final int lowHeight = (newHeight + 1);
        for(int y = lowHeight; y <= prevHeight; y++)
            this.addIncreaseEntry(localX, y, localZ, MAX_LEVEL);

        this.processIncrease();
        Events.invokeSkyLightChanged(column, localX, localZ, lowHeight, prevHeight, MAX_LEVEL);
    }

}
