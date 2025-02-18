package generaloss.mc24.server.column;

import generaloss.mc24.server.block.Block;
import generaloss.mc24.server.block.BlockProperty;
import generaloss.mc24.server.block.BlockState;
import generaloss.mc24.server.chunk.Chunk;
import generaloss.mc24.server.chunk.ChunkPos;
import generaloss.mc24.server.registry.ServerRegistries;
import generaloss.mc24.server.world.SkyLightEngine;
import generaloss.mc24.server.world.World;
import jpize.util.math.Mathc;
import jpize.util.math.Maths;
import jpize.util.math.vector.Vec3i;

import java.util.function.Consumer;

public class ColumnCache<C extends Chunk> {

    public static final int CENTER_COLUMN_INDEX = 5;

    private final World<C> world;
    private final ChunkColumn<C>[] columns;
    private boolean hasNullColumns;
    private final Vec3i norBlockPos;

    public ColumnCache(World<C> world) {
        this.world = world;
        this.columns = new ChunkColumn[3 * 3];
        this.norBlockPos = new Vec3i();
    }

    public ChunkColumn<C> getCenterColumn() {
        return columns[CENTER_COLUMN_INDEX];
    }

    public void forEach(Consumer<ChunkColumn<C>> consumer) {
        for(ChunkColumn<C> column: columns)
            consumer.accept(column);
    }


    private int index(int i, int j) {
        return (i * 3 + j);
    }

    public ChunkColumn<C> get(int i, int j) {
        return columns[this.index(i + 1, j + 1)];
    }


    public void initFor(ChunkColumn<C> column) {
        if(!hasNullColumns && columns[CENTER_COLUMN_INDEX] == column)
            return;

        hasNullColumns = false;
        final ColumnPos position = column.position();

        for(int i = 0; i < 3; i++) {
            for(int k = 0; k < 3; k++) {
                final int index = this.index(i, k);
                if(index == CENTER_COLUMN_INDEX) {
                    columns[index] = column;
                    continue;
                }

                final ChunkColumn<C> neighbor = world.getColumn(position.getNeighbor(i - 1, k - 1));
                if(neighbor == null)
                    hasNullColumns = true;

                columns[index] = neighbor;
            }
        }
    }


    public ChunkColumn<C> findForBlock(int x, int z) {
        final int columnX = Mathc.signum(Maths.floor((float) x / Chunk.SIZE));
        final int columnZ = Mathc.signum(Maths.floor((float) z / Chunk.SIZE));
        final ChunkColumn<C> column = this.get(columnX, columnZ);
        if(column == null)
            return null;

        // normalize block position
        norBlockPos.x = (x - columnX * Chunk.SIZE);
        norBlockPos.z = (z - columnZ * Chunk.SIZE);
        return column;
    }

    public int getHeight(int x, int z) {
        final ChunkColumn<C> column = this.findForBlock(x, z);
        if(column == null)
            return ColumnHeightMap.NO_HEIGHT;
        return column.getHeight(norBlockPos.x, norBlockPos.z);
    }


    public C findChunkForBlock(int x, int y, int z) {
        final ChunkColumn<C> column = this.findForBlock(x, z);
        if(column == null)
            return null;

        final C chunk = column.getChunk(ChunkPos.byBlock(y));
        if(chunk == null)
            return null;

        // normalize y
        norBlockPos.y = (y & Chunk.SIZE_BOUND);
        return chunk;
    }

    public BlockState getBlockState(int x, int y, int z) {
        final C chunk = this.findChunkForBlock(x, y, z);
        if(chunk == null)
            return Block.VOID.getDefaultState();
        final byte stateID = chunk.storage().blockstates().get(norBlockPos.x, norBlockPos.y, norBlockPos.z);
        return ServerRegistries.BLOCK_STATE.get(stateID);
    }

    public boolean setBlockState(int x, int y, int z, BlockState blockstate) {
        final C chunk = this.findChunkForBlock(x, y, z);
        if(chunk == null)
            return false;
        return chunk.setBlockState(norBlockPos.x, norBlockPos.y, norBlockPos.z, blockstate);
    }

    public byte getBlockLightLevel(int x, int y, int z, int channel) {
        final C chunk = this.findChunkForBlock(x, y, z);
        if(chunk == null)
            return 0;
        return chunk.getBlockLightLevel(norBlockPos.x, norBlockPos.y, norBlockPos.z, channel);
    }

    public int getSkyLightLevel(int x, int y, int z) {
        final C chunk = this.findChunkForBlock(x, y, z);
        if(chunk == null)
            return SkyLightEngine.MAX_LEVEL;
        return chunk.getSkyLightLevel(norBlockPos.x, norBlockPos.y, norBlockPos.z);
    }

    public int getLightLevel(int x, int y, int z, int channel) {
        final C chunk = this.findChunkForBlock(x, y, z);
        if(chunk == null)
            return SkyLightEngine.MAX_LEVEL;
        return chunk.getLightLevel(norBlockPos.x, norBlockPos.y, norBlockPos.z, channel);
    }

    public boolean setBlockLightLevel(int x, int y, int z, int channel, int lightLevel) {
        final C chunk = this.findChunkForBlock(x, y, z);
        if(chunk == null)
            return false;

        final int opacity = this.getBlockState(x, y, z).getBlockProperties().get(BlockProperty.OPACITY);
        if(opacity + lightLevel > 15)
            throw new RuntimeException("(" + x + ", " + y + ", " + z + "): opacity="+ opacity +", lightLevel="+lightLevel + ", sum="+(opacity +lightLevel) + " > 15");

        return chunk.setBlockLightLevel(norBlockPos.x, norBlockPos.y, norBlockPos.z, channel, lightLevel);
    }

}
