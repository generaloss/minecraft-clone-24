package generaloss.mc24.client.level.tesselation;

import java.util.Stack;

public class ChunkMeshCache {

    private final Stack<ChunkMesh> freeMeshes;

    public ChunkMeshCache() {
        this.freeMeshes = new Stack<>();
    }

    public ChunkMesh getFreeOrCreate() {
        if(!freeMeshes.isEmpty())
            return freeMeshes.pop();
        return new ChunkMesh(this);
    }

    public void free(ChunkMesh mesh) {
        freeMeshes.push(mesh);
    }

}
