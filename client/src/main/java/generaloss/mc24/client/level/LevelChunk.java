package generaloss.mc24.client.level;

import generaloss.mc24.client.meshing.chunk.ChunkMesh;
import generaloss.mc24.server.chunk.Chunk;
import generaloss.mc24.server.chunk.ChunkPos;
import generaloss.mc24.server.chunk.ChunkStorage;
import jpize.util.math.vector.Vec3f;

public class LevelChunk extends Chunk implements Comparable<LevelChunk> {

    private ChunkMesh mesh;

    public LevelChunk(LevelChunkColumn column, ChunkPos position, ChunkStorage storage) {
        super(column, position, storage);
    }

    @Override
    public WorldLevel world() {
        return (WorldLevel) super.world();
    }

    @Override
    public LevelChunkColumn column() {
        return (LevelChunkColumn) super.column();
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

    @Override
    public int compareTo(LevelChunk chunk) {
        final Vec3f player_pos = world().context().player().position();
        final float distance0 = player_pos.dst(super.position().getVec3iBlock());
        final float distance1 = player_pos.dst(chunk.position().getVec3iBlock());
        return Double.compare(distance0, distance1);
    }

}
