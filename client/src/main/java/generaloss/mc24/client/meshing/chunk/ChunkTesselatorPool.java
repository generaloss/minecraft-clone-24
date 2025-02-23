package generaloss.mc24.client.meshing.chunk;

import generaloss.mc24.client.level.LevelChunk;
import generaloss.mc24.client.level.WorldLevel;
import jpize.util.Disposable;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChunkTesselatorPool implements Disposable {

    private final ExecutorService executors;
    private final ChunkMeshCache meshCache;
    private final Queue<LevelChunk> taskQueue;
    private final ChunkTesselator[] tesselators;

    public ChunkTesselatorPool(int nTesselators, WorldLevel level) {
        this.taskQueue = new LinkedList<>();
        this.meshCache = new ChunkMeshCache();
        // create executors
        this.executors = Executors.newWorkStealingPool(nTesselators);
        // create tesselators
        this.tesselators = new ChunkTesselator[nTesselators];
        for(int i = 0; i < nTesselators; i++)
            this.tesselators[i] = new ChunkTesselator(level, meshCache);
    }

    public void tesselate(LevelChunk chunk) {
        if(chunk == null || taskQueue.contains(chunk))
            return;
        taskQueue.add(chunk);
    }

    private ChunkTesselator getFreeTesselator() {
        for(ChunkTesselator tesselator: tesselators)
            if(tesselator.getStatus() == ChunkTesselatorStatus.FREE)
                return tesselator;
        return null;
    }

    public void update() {
        while(!taskQueue.isEmpty()) {
            final ChunkTesselator freeTesselator = this.getFreeTesselator();
            if(freeTesselator == null)
                break;

            final LevelChunk chunk = taskQueue.poll();
            if(chunk == null)
                continue;

            freeTesselator.tesselate(chunk, executors);
        }

        for(ChunkTesselator tesselator: tesselators)
            if(tesselator.getStatus() == ChunkTesselatorStatus.DONE)
                tesselator.unlock();
    }

    public void reset() {
        taskQueue.clear();
    }

    @Override
    public void dispose() {
        this.reset();
        meshCache.dispose();
    }

}
