package generaloss.mc24.server.chunk;

import generaloss.mc24.server.block.BlockState;
import generaloss.mc24.server.world.World;
import jpize.util.math.Mathc;
import jpize.util.math.Maths;
import jpize.util.math.vector.Vec3i;

public class ChunkCache <W extends World<C>, C extends Chunk<? extends W>> {

    public static final int CENTER_CHUNK_INDEX = 13;

    private final W world;
    private final Chunk<?>[] chunks;
    private final Vec3i norBlockPos;

    public ChunkCache(W world) {
        this.world = world;
        this.chunks = new Chunk<?>[3 * 3 * 3];
        this.norBlockPos = new Vec3i();
    }

    public Chunk<?>[] getChunks() {
        return chunks;
    }


    public Chunk<?> get(int i, int j, int k) {
        return chunks[(i + 1) * 9 + (j + 1) * 3 + (k + 1)];
    }


    public void cacheNeighborsFor(C chunk) {
        if(chunks[CENTER_CHUNK_INDEX] == chunk)
            return;

        final ChunkPos position = chunk.position();

        for(int i = 0; i < 3; i++){
            for(int j = 0; j < 3; j++){
                for(int k = 0; k < 3; k++){
                    if(i == 1 && j == 1 && k == 1){
                        chunks[CENTER_CHUNK_INDEX] = chunk;
                        continue;
                    }
                    final C neighbor = world.getChunk(position.getNeighborPacked(i - 1, j - 1, k - 1));
                    chunks[i * 9 + j * 3 + k] = neighbor;
                }
            }
        }
    }


    public C findForBlock(int x, int y, int z) {
        final int chunkY = Mathc.signum(Maths.floor((float) y / Chunk.SIZE));
        final int chunkX = Mathc.signum(Maths.floor((float) x / Chunk.SIZE));
        final int chunkZ = Mathc.signum(Maths.floor((float) z / Chunk.SIZE));
        final Chunk<?> chunk = this.get(chunkX, chunkY, chunkZ);
        if(chunk == null)
            return null;

        // normalize block position
        norBlockPos.x = (x - chunkX * Chunk.SIZE);
        norBlockPos.y = (y - chunkY * Chunk.SIZE);
        norBlockPos.z = (z - chunkZ * Chunk.SIZE);
        return (C) chunk;
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

    public byte getBlockLightLevel(int x, int y, int z, int channel) {
        final C chunk = this.findForBlock(x, y, z);
        if(chunk == null)
            return 0;
        return chunk.getBlockLightLevel(norBlockPos.x, norBlockPos.y, norBlockPos.z, channel);
    }

    public boolean setBlockLightLevel(int x, int y, int z, int channel, int lightLevel) {
        final C chunk = this.findForBlock(x, y, z);
        if(chunk == null)
            return false;
        return chunk.setBlockLightLevel(norBlockPos.x, norBlockPos.y, norBlockPos.z, channel, lightLevel);
    }

}
