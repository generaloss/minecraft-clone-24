package generaloss.mc24.server.world;

import generaloss.mc24.server.block.BlockState;
import generaloss.mc24.server.chunk.Chunk;
import generaloss.mc24.server.chunk.ChunkPos;
import jpize.util.math.Maths;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public abstract class World <C extends Chunk<? extends World<C>>> {

    private final Map<Long, C> chunks;
    private final BlockLightEngine<World<C>, C> blockLightEngine;
    private final List<BlockStateChangedCallback<World<C>, C>> blockStateChangedCallbacks;
    private final List<BlockLightChangedCallback<World<C>, C>> blockLightChangedCallbacks;

    public World() {
        this.chunks = new ConcurrentHashMap<>();
        this.blockLightEngine = new BlockLightEngine<>(this);
        this.blockStateChangedCallbacks = new ArrayList<>();
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
        return this.getChunk(position.getX(), position.getY(), position.getZ());
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
            return null;
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
            this.invokeBlockStateChangedCallbacks(chunk, localX, localY, localZ, state);
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


    public void registerBlockStateChangedCallback(BlockStateChangedCallback<World<C>, C> callback) {
        blockStateChangedCallbacks.add(callback);
    }

    public void unregisterBlockStateChangedCallback(BlockStateChangedCallback<World<C>, C> callback) {
        blockStateChangedCallbacks.remove(callback);
    }

    private void invokeBlockStateChangedCallbacks(C chunk, int x, int y, int z, BlockState state) {
        for(BlockStateChangedCallback<World<C>, C> callback: blockStateChangedCallbacks)
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
