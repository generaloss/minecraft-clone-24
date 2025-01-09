package generaloss.mc24.client.level;

import generaloss.mc24.client.chunkmesh.ChunkMesh;
import generaloss.mc24.server.chunk.Chunk;
import generaloss.mc24.server.network.packet2c.ChunkPacket2C;

public class LevelChunk extends Chunk<WorldLevel> {

    private ChunkMesh mesh;

    public LevelChunk(WorldLevel level, ChunkPacket2C packet) {
        super(
            level, packet.getPosition(), packet.getBlockstateIDs(),
            packet.getBlockLight()
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
