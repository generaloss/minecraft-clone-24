package generaloss.mc24.client.level.mesh;

import jpize.util.Disposable;

import java.util.Stack;

public class ChunkMeshCache implements Disposable {

    private final Stack<ChunkMesh> freeMeshes;

    public ChunkMeshCache() {
        this.freeMeshes = new Stack<>();
    }

    public ChunkMesh getFreeOrCreate() {
        if(!freeMeshes.empty())
            return freeMeshes.pop();
        return new ChunkMesh(this);
    }

    public void free(ChunkMesh mesh) {
        freeMeshes.push(mesh);
    }

    @Override
    public void dispose() {
        while(!freeMeshes.empty())
            freeMeshes.pop().dispose();
    }

}
