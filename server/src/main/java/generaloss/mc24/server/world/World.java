package generaloss.mc24.server.world;

import generaloss.mc24.server.block.Block;
import generaloss.mc24.server.block.BlockState;
import generaloss.mc24.server.chunk.Chunk;
import generaloss.mc24.server.chunk.ChunkPos;
import generaloss.mc24.server.chunk.ChunkStorage;
import generaloss.mc24.server.column.ChunkColumn;
import generaloss.mc24.server.column.ColumnPos;
import generaloss.mc24.server.event.Events;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

public abstract class World<C extends Chunk> {

    private final Map<Long, ChunkColumn<C>> columns;
    private final BlockLightEngine<C> blockLightEngine;
    private final SkyLightEngine<C> skyLightEngine;

    public World() {
        this.columns = new ConcurrentHashMap<>();
        this.blockLightEngine = new BlockLightEngine<>(this);
        this.skyLightEngine = new SkyLightEngine<>(this);
    }

    public BlockLightEngine<C> getBlockLightEngine() {
        return blockLightEngine;
    }

    public SkyLightEngine<C> getSkyLightEngine() {
        return skyLightEngine;
    }


    public Collection<ChunkColumn<C>> getColumns() {
        return columns.values();
    }

    public ChunkColumn<C> getColumn(long packedXZ) {
        return columns.get(packedXZ);
    }

    public ChunkColumn<C> getColumn(int columnX, int columnZ) {
        return columns.get(ColumnPos.pack(columnX, columnZ));
    }

    public ChunkColumn<C> getColumn(ColumnPos position) {
        return this.getColumn(position.getX(), position.getZ());
    }

    public ChunkColumn<C> createAndGetColumn(int columnX, int columnZ) {
        final ColumnPos position = new ColumnPos(columnX, columnZ);
        if(!columns.containsKey(position.getPacked()))
            this.putColumn(this.createColumn(position));
        return columns.get(position.getPacked());
    }

    public void putColumn(ChunkColumn<C> column) {
        columns.put(column.position().getPacked(), column);
    }

    public void removeColumn(long packedXZ) {
        columns.remove(packedXZ);
    }

    public void removeColumn(ColumnPos position) {
        this.removeColumn(position.getPacked());
    }

    public void removeColumn(ChunkColumn<C> column) {
        this.removeColumn(column.position());
    }

    public void clearColumns() {
        columns.clear();
    }

    abstract protected ChunkColumn<C> createColumn(ColumnPos position);


    public void forEachChunk(Predicate<C> comsumer) {
        for(ChunkColumn<C> column: columns.values())
            for(C chunk: column.getChunks())
                if(!comsumer.test(chunk))
                    break;
    }


    public C getChunk(int chunkX, int chunkY, int chunkZ) {
        final ChunkColumn<C> column = this.getColumn(chunkX, chunkZ);
        if(column == null)
            return null;
        return column.getChunk(chunkY);
    }

    public C getChunk(ChunkPos position) {
        return this.getChunk(position.getX(), position.getY(), position.getZ());
    }


    public abstract C createChunk(ChunkPos position, ChunkStorage storage);

    public void removeChunk(ChunkPos position) {
        if(position == null)
            throw new IllegalArgumentException("Chunk position cannot be null");

        final long packedColumnXZ = ColumnPos.pack(position.getX(), position.getZ());
        final ChunkColumn<C> column = this.getColumn(packedColumnXZ);
        if(column == null)
            return;

        column.removeChunk(position.getY());
        if(column.size() == 0)
            this.removeColumn(packedColumnXZ);
    }

    public void removeChunk(C chunk) {
        if(chunk == null)
            throw new IllegalArgumentException("Chunk cannot be null");
        this.removeChunk(chunk.position());
    }


    public C getChunkByBlock(int x, int y, int z) {
        return this.getChunk(
            ChunkPos.byBlock(x),
            ChunkPos.byBlock(y),
            ChunkPos.byBlock(z)
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
            Events.invokeBlockstateChanged(chunk, localX, localY, localZ, state);
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
            Events.invokeBlockLightChanged(chunk, localX, localY, localZ, r, g, b);
        return success;
    }


   

}
