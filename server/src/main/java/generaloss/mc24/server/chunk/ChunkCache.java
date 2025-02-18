package generaloss.mc24.server.chunk;

import generaloss.mc24.server.block.Block;
import generaloss.mc24.server.block.BlockProperty;
import generaloss.mc24.server.block.BlockState;
import generaloss.mc24.server.registry.ServerRegistries;
import generaloss.mc24.server.world.SkyLightEngine;
import generaloss.mc24.server.world.World;
import jpize.util.math.Mathc;
import jpize.util.math.Maths;
import jpize.util.math.vector.Vec3i;

import java.util.function.Consumer;

public class ChunkCache<C extends Chunk> {

    public static final int CENTER_CHUNK_INDEX = 13;

    private final World<C> world;
    private final Chunk[] chunks;
    private final Vec3i norBlockPos;
    private boolean hasNullChunks;

    public ChunkCache(World<C> world) {
        this.world = world;
        this.chunks = new Chunk[3 * 3 * 3];
        this.norBlockPos = new Vec3i();
    }

    public C getCenterChunk() {
        return (C) chunks[CENTER_CHUNK_INDEX];
    }

    public void forEach(Consumer<C> consumer) {
        for(Chunk chunk: chunks)
            consumer.accept((C) chunk);
    }


    private int index(int i, int j, int k) {
        return (i * 9 + j * 3 + k);
    }

    public Chunk get(int i, int j, int k) {
        return chunks[this.index(i + 1, j + 1, k + 1)];
    }


    public void cacheNeighborsFor(C chunk) {
        if(!hasNullChunks && chunks[CENTER_CHUNK_INDEX] == chunk)
            return;

        hasNullChunks = false;
        final ChunkPos position = chunk.position();

        for(int i = 0; i < 3; i++) {
            for(int j = 0; j < 3; j++) {
                for(int k = 0; k < 3; k++) {
                    final int index = this.index(i, j, k);
                    if(index == CENTER_CHUNK_INDEX) {
                        chunks[index] = chunk;
                        continue;
                    }

                    final C neighbor = world.getChunk(position.getNeighbor(i - 1, j - 1, k - 1));
                    if(neighbor == null)
                        hasNullChunks = true;

                    chunks[index] = neighbor;
                }
            }
        }
    }


    public C findForBlock(int x, int y, int z) {
        final int chunkX = Mathc.signum(Maths.floor((float) x / Chunk.SIZE));
        final int chunkY = Mathc.signum(Maths.floor((float) y / Chunk.SIZE));
        final int chunkZ = Mathc.signum(Maths.floor((float) z / Chunk.SIZE));
        final Chunk chunk = this.get(chunkX, chunkY, chunkZ);
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
            return Block.VOID.getDefaultState();
        final byte stateID = chunk.storage().blockstates().get(norBlockPos.x, norBlockPos.y, norBlockPos.z);
        return ServerRegistries.BLOCK_STATE.get(stateID);
    }

    public boolean setBlockState(int x, int y, int z, BlockState blockstate) {
        final C chunk = this.findForBlock(x, y, z);
        if(chunk == null)
            return false;
        return chunk.setBlockState(norBlockPos.x, norBlockPos.y, norBlockPos.z, blockstate);
    }

    public byte getBlockLightLevel(int x, int y, int z, int channel) {
        final C chunk = this.findForBlock(x, y, z);
        if(chunk == null)
            return 0;
        return chunk.getBlockLightLevel(norBlockPos.x, norBlockPos.y, norBlockPos.z, channel);
    }

    public int getSkyLightLevel(int x, int y, int z) {
        final C chunk = this.findForBlock(x, y, z);
        if(chunk == null)
            return SkyLightEngine.MAX_LEVEL;
        return chunk.getSkyLightLevel(norBlockPos.x, norBlockPos.y, norBlockPos.z);
    }

    public int getLightLevel(int x, int y, int z, int channel) {
        final C chunk = this.findForBlock(x, y, z);
        if(chunk == null)
            return SkyLightEngine.MAX_LEVEL;
        return chunk.getLightLevel(norBlockPos.x, norBlockPos.y, norBlockPos.z, channel);
    }

    public boolean setBlockLightLevel(int x, int y, int z, int channel, int lightLevel) {
        final C chunk = this.findForBlock(x, y, z);
        if(chunk == null)
            return false;

        final int opacity = this.getBlockState(x, y, z).getBlockProperties().get(BlockProperty.OPACITY);
        if(opacity + lightLevel > 15)
            throw new RuntimeException("(" + x + ", " + y + ", " + z + "): opacity="+ opacity +", lightLevel="+lightLevel + ", sum="+(opacity +lightLevel) + " > 15");

        return chunk.setBlockLightLevel(norBlockPos.x, norBlockPos.y, norBlockPos.z, channel, lightLevel);
    }

}
