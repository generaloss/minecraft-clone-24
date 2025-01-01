package generaloss.mc24.client.chunk;

import jpize.util.Disposable;

import java.util.concurrent.ConcurrentLinkedQueue;

public class ChunkMeshCache implements Disposable {

    private final ConcurrentLinkedQueue<ChunkMesh> freeMeshes;

    public ChunkMeshCache() {
        this.freeMeshes = new ConcurrentLinkedQueue<>();
    }

    public ChunkMesh getFreeOrCreate() {
        if(!freeMeshes.isEmpty())
            return freeMeshes.poll();
        return new ChunkMesh(this);
    }

    public void free(ChunkMesh mesh) {
        freeMeshes.add(mesh);
    }

    @Override
    public void dispose() {
        while(!freeMeshes.isEmpty())
            freeMeshes.poll().dispose();
    }

}
