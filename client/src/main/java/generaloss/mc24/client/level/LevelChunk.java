package generaloss.mc24.client.level;

import generaloss.mc24.client.chunk.ChunkMesh;
import generaloss.mc24.server.block.Block;
import generaloss.mc24.server.block.BlockProperty;
import generaloss.mc24.server.block.BlockState;
import generaloss.mc24.server.chunk.Chunk;
import generaloss.mc24.server.chunk.ChunkPos;
import generaloss.mc24.server.registry.Registries;
import jpize.util.math.vector.Vec3i;

public class LevelChunk extends Chunk<WorldLevel> {

    private ChunkMesh mesh;

    public LevelChunk(WorldLevel level, ChunkPos position, Registries registries) {
        super(level, position, registries);
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
    public boolean setBlockState(int x, int y, int z, BlockState blockState) {
        final boolean success = super.setBlockState(x, y, z, blockState);
        if(success) {
            final Block block = blockState.getBlock();
            final Vec3i glowing = block.properties().getVec3i("glowing");
            super.setBlockLightLevel(x, y, z, glowing.x, glowing.y, glowing.z);
            final WorldLevel level = super.world();
            level.blockLightEngine().increase(this, x, y, z, glowing.x, glowing.y, glowing.z);
        }
        return success;
    }

}
