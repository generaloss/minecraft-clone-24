package generaloss.mc24.server.column;

import generaloss.mc24.server.block.Block;
import generaloss.mc24.server.block.BlockState;
import generaloss.mc24.server.chunk.Chunk;
import generaloss.mc24.server.light.SkyLightEngine;
import generaloss.mc24.server.world.World;
import jpize.util.math.Mathc;
import jpize.util.math.Maths;
import jpize.util.math.vector.Vec3i;

import java.util.function.Consumer;

public class ColumnCache<C extends Chunk> {

    public static final int SIZE = 3;
    public static final int AREA = (SIZE * SIZE);
    public static final int CENTER_COLUMN_INDEX = (AREA - 1) / 2;

    private final World<C> world;
    private final ChunkColumn<C>[] columns;
    private boolean hasNullColumns;
    private final Vec3i norBlockPos;

    public ColumnCache(World<C> world) {
        this.world = world;
        this.columns = new ChunkColumn[AREA];
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
        return (i * SIZE + j);
    }

    public ChunkColumn<C> get(int relativeX, int relativeZ) {
        return columns[this.index(relativeX + 1, relativeZ + 1)];
    }


    public void initFor(ChunkColumn<C> column) {
        if(!hasNullColumns && columns[CENTER_COLUMN_INDEX] == column)
            return;

        hasNullColumns = false;
        final ColumnPos position = column.position();

        for(int i = 0; i < SIZE; i++) {
            for(int k = 0; k < SIZE; k++) {
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


    public BlockState getBlockState(int x, int y, int z) {
        final ChunkColumn<C> column = this.findForBlock(x, z);
        if(column == null)
            return Block.VOID.getDefaultState();
        return column.getBlockState(norBlockPos.x, y, norBlockPos.z);
    }

    public boolean setBlockState(int x, int y, int z, BlockState blockstate) {
        final ChunkColumn<C> column = this.findForBlock(x, z);
        if(column == null)
            return false;
        return column.setBlockState(norBlockPos.x, y, norBlockPos.z, blockstate);
    }

    public byte getBlockLightLevel(int x, int y, int z, int channel) {
        final ChunkColumn<C> column = this.findForBlock(x, z);
        if(column == null)
            return 0;
        return column.getBlockLightLevel(norBlockPos.x, y, norBlockPos.z, channel);
    }

    public boolean setBlockLightLevel(int x, int y, int z, int channel, int level) {
        final ChunkColumn<C> column = this.findForBlock(x, z);
        if(column == null)
            return false;
        return column.setBlockLightLevel(norBlockPos.x, y, norBlockPos.z, channel, level);
    }

    public boolean setBlockLightLevel(int x, int y, int z, int redLevel, int greenLevel, int blueLevel) {
        final ChunkColumn<C> column = this.findForBlock(x, z);
        if(column == null)
            return false;
        return column.setBlockLightLevel(norBlockPos.x, y, norBlockPos.z, redLevel, greenLevel, blueLevel);
    }

    public int getSkyLightLevel(int x, int y, int z) {
        final ChunkColumn<C> column = this.findForBlock(x, z);
        if(column == null)
            return SkyLightEngine.MAX_LEVEL;
        return column.getSkyLightLevel(norBlockPos.x, y, norBlockPos.z);
    }

    public boolean setSkyLightLevel(int x, int y, int z, int level) {
        final ChunkColumn<C> column = this.findForBlock(x, z);
        if(column == null)
            return false;
        return column.setSkyLightLevel(norBlockPos.x, y, norBlockPos.z, level);
    }

    public int getLightLevel(int x, int y, int z, int channel) {
        final ChunkColumn<C> column = this.findForBlock(x, z);
        if(column == null)
            return SkyLightEngine.MAX_LEVEL;
        return column.getLightLevel(norBlockPos.x, y, norBlockPos.z, channel);
    }

}
