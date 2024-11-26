package generaloss.mc24.client.level;

import generaloss.mc24.client.chunk.ChunkMesh;
import generaloss.mc24.server.chunk.Chunk;
import generaloss.mc24.server.chunk.ChunkPos;
import generaloss.mc24.server.registry.Registries;

public class LevelChunk extends Chunk {

    private ChunkMesh mesh;

    public LevelChunk(WorldLevel level, ChunkPos position, Registries registries) {
        super(level, position, registries);
    }

    public WorldLevel level() {
        return (WorldLevel) super.world();
    }

    public ChunkMesh mesh() {
        return mesh;
    }


    public void setMesh(ChunkMesh mesh) {
        this.mesh = mesh;
    }

    public void freeMesh() {
        if(mesh == null)
            return;
        mesh.free();
        mesh = null;
    }

}
