package generaloss.mc24.client.meshing.chunk;

import generaloss.mc24.client.level.LevelChunk;
import generaloss.mc24.client.level.WorldLevel;
import jpize.util.Disposable;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChunkTesselatorPool implements Disposable {

    private final ExecutorService executors;
    private final ChunkMeshCache meshCache;
    private final List<TesselateGroup> groups;
    private final ChunkTesselator[] tesselators;

    public ChunkTesselatorPool(int nTesselators, WorldLevel level) {
        this.groups = new CopyOnWriteArrayList<>();
        this.meshCache = new ChunkMeshCache();
        // create executors
        this.executors = Executors.newWorkStealingPool(nTesselators);
        // create tesselators
        this.tesselators = new ChunkTesselator[nTesselators];
        for(int i = 0; i < nTesselators; i++)
            this.tesselators[i] = new ChunkTesselator(level, meshCache);
    }

    public void tesselate(Iterable<LevelChunk> chunks) {
        final List<LevelChunk> toTesselate = new LinkedList<>();
        for(LevelChunk chunk: chunks) {
            if(chunk == null && !toTesselate.contains(chunk))
                continue;
            toTesselate.add(chunk);
        }
        groups.add(new TesselateGroup(toTesselate));
    }

    public void tesselate(LevelChunk chunk) {
        if(chunk == null)
            return;
        groups.add(new TesselateGroup(chunk));
    }

    private ChunkTesselator getFreeTesselator() {
        for(ChunkTesselator tesselator: tesselators)
            if(tesselator.getStatus() == ChunkTesselatorStatus.FREE)
                return tesselator;
        return null;
    }

    public void update() {
        for(ChunkTesselator tesselator: tesselators)
            if(tesselator.getStatus() == ChunkTesselatorStatus.DONE)
                tesselator.unlock();

        for(TesselateGroup group: groups) {
            while(group.hasNext()) {
                final ChunkTesselator freeTesselator = this.getFreeTesselator();
                if(freeTesselator == null)
                    return;

                final LevelChunk chunk = group.next();
                freeTesselator.tesselate(chunk, executors, mesh ->
                    group.setMesh(chunk, mesh, () ->
                        groups.remove(group)
                    )
                );
            }
        }
    }

    public void reset() {
        groups.clear();
    }

    @Override
    public void dispose() {
        this.reset();
        meshCache.dispose();
    }

}
