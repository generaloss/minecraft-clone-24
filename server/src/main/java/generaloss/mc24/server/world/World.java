package generaloss.mc24.server.world;

import generaloss.mc24.server.block.Block;
import generaloss.mc24.server.block.BlockState;
import generaloss.mc24.server.chunk.Chunk;
import generaloss.mc24.server.chunk.ChunkPos;
import jpize.util.math.Maths;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public abstract class World <C extends Chunk<? extends World<C>>> {

    private final Map<Long, C> chunks;
    private final BlockLightEngine<World<C>, C> blockLightEngine;
    private final List<BlockStateChangedCallback<World<C>, C>> blockstateChangedCallbacks;
    private final List<BlockLightChangedCallback<World<C>, C>> blockLightChangedCallbacks;

    public World() {
        this.chunks = new ConcurrentHashMap<>();
        this.blockLightEngine = new BlockLightEngine<>(this);
        this.blockstateChangedCallbacks = new ArrayList<>();
        this.blockLightChangedCallbacks = new ArrayList<>();
    }

    public BlockLightEngine<World<C>, C> getBlockLightEngine() {
        return blockLightEngine;
    }


    public Collection<C> getChunks() {
        return chunks.values();
    }

    public C getChunk(long packedPosition) {
        return chunks.get(packedPosition);
    }

    public C getChunk(int chunkX, int chunkY, int chunkZ) {
        return this.getChunk(ChunkPos.pack(chunkX, chunkY, chunkZ));
    }

    public C getChunk(ChunkPos position) {
        return this.getChunk(position.pack());
    }

    public void putChunk(C chunk) {
        chunks.put(chunk.position().pack(), chunk);
    }

    public void removeChunk(long packedPosition) {
        chunks.remove(packedPosition);
    }

    public void removeChunk(ChunkPos position) {
        this.removeChunk(position.pack());
    }

    public void removeChunk(C chunk) {
        this.removeChunk(chunk.position());
    }

    public void clearChunks() {
        chunks.clear();
    }


    public C getChunkByBlock(int x, int y, int z) {
        return this.getChunk(
            Maths.floor((float) x / Chunk.SIZE),
            Maths.floor((float) y / Chunk.SIZE),
            Maths.floor((float) z / Chunk.SIZE)
        );
    }

    public BlockState getBlockState(int x, int y, int z) {
        final C chunk = this.getChunkByBlock(x, y, z);
        if(chunk == null)
            return Block.VOID.getDefaultState();
        final int localX = (x & Chunk.SIZE_BOUND);
        final int localY = (y & Chunk.SIZE_BOUND);
        final int localZ = (z & Chunk.SIZE_BOUND);
        return chunk.getBlockState(localX, localY, localZ);
    }

    public boolean setBlockState(int x, int y, int z, BlockState state) {
        final C chunk = this.getChunkByBlock(x, y, z);
        if(chunk == null)
            return false;
        final int localX = (x & Chunk.SIZE_BOUND);
        final int localY = (y & Chunk.SIZE_BOUND);
        final int localZ = (z & Chunk.SIZE_BOUND);
        final boolean success = chunk.setBlockState(localX, localY, localZ, state);
        if(success)
            this.invokeBlockstateChangedCallbacks(chunk, localX, localY, localZ, state);
        return success;
    }

    public byte getBlockLightLevel(int x, int y, int z, int channel) {
        final C chunk = this.getChunkByBlock(x, y, z);
        if(chunk == null)
            return 0;
        final int localX = (x & Chunk.SIZE_BOUND);
        final int localY = (y & Chunk.SIZE_BOUND);
        final int localZ = (z & Chunk.SIZE_BOUND);
        return chunk.getBlockLightLevel(localX, localY, localZ, channel);
    }

    public boolean setBlockLightLevel(int x, int y, int z, int channel, int level) {
        final C chunk = this.getChunkByBlock(x, y, z);
        if(chunk == null)
            return false;
        final int localX = (x & Chunk.SIZE_BOUND);
        final int localY = (y & Chunk.SIZE_BOUND);
        final int localZ = (z & Chunk.SIZE_BOUND);
        final boolean success = chunk.setBlockLightLevel(localX, localY, localZ, channel, level);
        // callbacka ne budet
        return success;
    }

    public boolean setBlockLightLevel(int x, int y, int z, int r, int g, int b) {
        final C chunk = this.getChunkByBlock(x, y, z);
        if(chunk == null)
            return false;
        final int localX = (x & Chunk.SIZE_BOUND);
        final int localY = (y & Chunk.SIZE_BOUND);
        final int localZ = (z & Chunk.SIZE_BOUND);
        final boolean success = chunk.setBlockLightLevel(localX, localY, localZ, r, g, b);
        if(success)
            this.invokeBlockLightChangedCallbacks(chunk, localX, localY, localZ, r, g, b);
        return success;
    }


    public void registerBlockstateChangedCallback(BlockStateChangedCallback<World<C>, C> callback) {
        blockstateChangedCallbacks.add(callback);
    }

    public void unregisterBlockstateChangedCallback(BlockStateChangedCallback<World<C>, C> callback) {
        blockstateChangedCallbacks.remove(callback);
    }

    private void invokeBlockstateChangedCallbacks(C chunk, int x, int y, int z, BlockState state) {
        for(BlockStateChangedCallback<World<C>, C> callback: blockstateChangedCallbacks)
            callback.invoke(chunk, x, y, z, state);
    }


    public void registerBlockLightChangedCallback(BlockLightChangedCallback<World<C>, C> callback) {
        blockLightChangedCallbacks.add(callback);
    }

    public void unregisterBlockLightChangedCallback(BlockLightChangedCallback<World<C>, C> callback) {
        blockLightChangedCallbacks.remove(callback);
    }

    private void invokeBlockLightChangedCallbacks(C chunk, int x, int y, int z, int r, int g, int b) {
        for(BlockLightChangedCallback<World<C>, C> callback: blockLightChangedCallbacks)
            callback.invoke(chunk, x, y, z, r, g, b);
    }

}
