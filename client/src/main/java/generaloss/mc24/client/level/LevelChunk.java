package generaloss.mc24.client.level;

import generaloss.mc24.client.chunk.ChunkMesh;
import generaloss.mc24.server.block.Block;
import generaloss.mc24.server.block.BlockState;
import generaloss.mc24.server.chunk.Chunk;
import generaloss.mc24.server.network.packet2c.ChunkPacket2C;
import jpize.util.math.vector.Vec3i;

public class LevelChunk extends Chunk<WorldLevel> {

    private ChunkMesh mesh;

    public LevelChunk(WorldLevel level, ChunkPacket2C packet) {
        super(
            level, packet.getPosition(), packet.getBlockStateIndices(),
            packet.getBlockLight(), level.context().registries()
        );
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
