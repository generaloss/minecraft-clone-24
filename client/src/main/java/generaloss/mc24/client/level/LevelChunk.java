package generaloss.mc24.client.level;

import generaloss.mc24.client.chunk.ChunkMesh;
import generaloss.mc24.server.block.BlockState;
import generaloss.mc24.server.chunk.Chunk;
import generaloss.mc24.server.chunk.ChunkPos;
import generaloss.mc24.server.registry.IntRegistry;

public class LevelChunk extends Chunk {

    private ChunkMesh mesh;

    public LevelChunk(WorldLevel level, ChunkPos position, IntRegistry<BlockState> blockStateRegistry) {
        super(level, position, blockStateRegistry);
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
