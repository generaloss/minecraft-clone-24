package generaloss.mc24.server.world;

import generaloss.mc24.server.Directory;
import generaloss.mc24.server.block.BlockProperty;
import generaloss.mc24.server.block.BlockState;
import generaloss.mc24.server.chunk.Chunk;
import jpize.util.math.vector.Vec3i;
import jpize.util.time.Stopwatch;

import java.util.LinkedList;
import java.util.Queue;

public class BlockLightEngine <C extends Chunk<?>> {

    public static final int MAX_LEVEL = 15;

    private record Task <C extends Chunk<?>> (C chunk, int x, int y, int z, int lightLevel) { }
    private record Node(int x, int y, int z, int lightLevel) { }

    private final Queue<Task<C>> increaseTaskQueue, decreaseTaskQueue;
    private final Queue<Node> increaseQueue, decreaseQueue;
    private final ChunkCache<C> chunkCache;

    public <W extends World<C>> BlockLightEngine(W world) {
        this.increaseTaskQueue = new LinkedList<>();
        this.decreaseTaskQueue = new LinkedList<>();
        this.increaseQueue = new LinkedList<>();
        this.decreaseQueue = new LinkedList<>();
        this.chunkCache = new ChunkCache<>(world);
    }

    public void increase(C chunk, int x, int y, int z, int lightLevel) {
        if(chunk == null)
            return;
        increaseTaskQueue.add(new Task<>(chunk, x, y, z, lightLevel));
    }

    public void decrease(C chunk, int x, int y, int z, int lightLevel) {
        if(chunk == null)
            return;
        decreaseTaskQueue.add(new Task<>(chunk, x, y, z, lightLevel));
    }

    public void update() {
        final Stopwatch timer = new Stopwatch().start();
        while(!increaseTaskQueue.isEmpty()) {
            final Task<C> task = increaseTaskQueue.poll();
            chunkCache.cacheNeighborsFor(task.chunk);
            increaseQueue.add(new Node(task.x, task.y, task.z, task.lightLevel));
            this.processIncrease();
        }
        // !soon!
        // while(!decreaseTaskQueue.isEmpty()) {
        //     final Task task = decreaseTaskQueue.poll();
        //     chunkCache.cacheNeighborsFor(task.chunk);
        //     decreaseQueue.add(new Node(task.x, task.y, task.z, task.lightLevel));
        //     this.processDecrease(task.chunk, task.x, task.y, task.z, task.lightLevel);
        // }
    }

    public void processIncrease() {
        while(!increaseQueue.isEmpty()) {
            final Node node = increaseQueue.poll();
            final int lightLevel = node.lightLevel;

            for(int i = 0; i < 6; i++){
                final Directory dir = Directory.values()[i];
                final Vec3i normal = dir.getNormal();

                final int nx = (node.x + normal.x);
                final int ny = (node.y + normal.y);
                final int nz = (node.z + normal.z);

                final int neighborLightLevel = chunkCache.getBlockLightLevel(nx, ny, nz);

                if(neighborLightLevel >= lightLevel - 1)
                    continue;

                final BlockState neighborBlockState = chunkCache.getBlockState(nx, ny, nz);
                if(neighborBlockState == null)
                    continue;

                final int neighborBlockOpacity = neighborBlockState.properties().getInt(BlockProperty.OPACITY);
                final int targetLightLevel = (lightLevel - Math.max(1, neighborBlockOpacity));

                if(targetLightLevel > neighborLightLevel){
                    chunkCache.setBlockLightLevel(nx, ny, nz, targetLightLevel);
                    increaseQueue.add(new Node(nx, ny, nz, targetLightLevel));
                }
            }
        }
    }

}
