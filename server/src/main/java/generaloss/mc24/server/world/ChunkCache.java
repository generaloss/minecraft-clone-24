package generaloss.mc24.server.world;

import generaloss.mc24.server.block.BlockState;
import generaloss.mc24.server.chunk.Chunk;
import generaloss.mc24.server.chunk.ChunkPos;
import jpize.util.math.Mathc;
import jpize.util.math.Maths;
import jpize.util.math.vector.Vec3i;

public class ChunkCache <C extends Chunk<?>> {

    private final World<C> world;
    private final Chunk<?>[][][] chunkCache;
    private final Vec3i norBlockPos;

    public ChunkCache(World<C> world) {
        this.world = world;
        this.chunkCache = new Chunk[3][3][3];
        this.norBlockPos = new Vec3i();
    }

    public C get(int i, int j, int k) {
        return (C) chunkCache[i + 1][j + 1][k + 1];
    }


    public void cacheNeighborsFor(C chunk) {
        if(chunkCache[1][1][1] == chunk)
            return;

        final ChunkPos position = chunk.position();

        for(int i = 0; i < 3; i++){
            for(int j = 0; j < 3; j++){
                for(int k = 0; k < 3; k++){
                    if(i == 1 && j == 1 && k == 1){
                        chunkCache[1][1][1] = chunk;
                        continue;
                    }
                    final C neighbor = world.getChunk(position.getNeighborPacked(i - 1, j - 1, k - 1));
                    chunkCache[i][j][k] = neighbor;
                }
            }
        }
    }


    public C findForBlock(int x, int y, int z) {
        final int chunkY = Mathc.signum(Maths.floor((float) y / Chunk.SIZE));
        final int chunkX = Mathc.signum(Maths.floor((float) x / Chunk.SIZE));
        final int chunkZ = Mathc.signum(Maths.floor((float) z / Chunk.SIZE));
        final C chunk = this.get(chunkX, chunkY, chunkZ);
        if(chunk == null)
            return null;

        // normalize block position
        norBlockPos.x = (x - chunkX * Chunk.SIZE);
        norBlockPos.y = (y - chunkY * Chunk.SIZE);
        norBlockPos.z = (z - chunkZ * Chunk.SIZE);
        return chunk;
    }

    public BlockState getBlockState(int x, int y, int z) {
        final C chunk = this.findForBlock(x, y, z);
        if(chunk == null)
            return null;
        return chunk.getBlockState(norBlockPos.x, norBlockPos.y, norBlockPos.z);
    }

    public boolean setBlockState(int x, int y, int z, BlockState blockState) {
        final C chunk = this.findForBlock(x, y, z);
        if(chunk == null)
            return false;
        return chunk.setBlockState(norBlockPos.x, norBlockPos.y, norBlockPos.z, blockState);
    }

    public byte getBlockLightLevel(int x, int y, int z) {
        final C chunk = this.findForBlock(x, y, z);
        if(chunk == null)
            return 0;
        return chunk.getBlockLightLevel(norBlockPos.x, norBlockPos.y, norBlockPos.z);
    }

    public boolean setBlockLightLevel(int x, int y, int z, int lightLevel) {
        final C chunk = this.findForBlock(x, y, z);
        if(chunk == null)
            return false;
        return chunk.setBlockLightLevel(norBlockPos.x, norBlockPos.y, norBlockPos.z, lightLevel);
    }

}
