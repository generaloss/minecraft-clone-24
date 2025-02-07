package generaloss.mc24.client.level;

import generaloss.mc24.client.chunkmesh.ChunkMesh;
import generaloss.mc24.server.chunk.Chunk;
import generaloss.mc24.server.network.packet2c.ChunkPacket2C;
import jpize.util.math.vector.Vec3f;

public class LevelChunk extends Chunk<WorldLevel> implements Comparable<LevelChunk> {

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

    @Override
    public int compareTo(LevelChunk chunk) {
        final Vec3f player_pos = world().context().player().position();
        final float distance0 = player_pos.dst(super.position().getVec3iBlock());
        final float distance1 = player_pos.dst(chunk.position().getVec3iBlock());
        return Double.compare(distance0, distance1);
    }

}
