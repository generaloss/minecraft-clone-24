package generaloss.mc24.server.chunk;

import generaloss.mc24.server.block.Block;
import generaloss.mc24.server.block.BlockProperty;
import generaloss.mc24.server.block.BlockState;
import generaloss.mc24.server.column.ChunkColumn;
import generaloss.mc24.server.common.XYZConsumer;
import generaloss.mc24.server.common.XZConsumer;
import generaloss.mc24.server.registry.ServerRegistries;
import generaloss.mc24.server.world.SkyLightEngine;
import generaloss.mc24.server.world.World;

public abstract class Chunk {

    public static final int SIZE = 16;
    public static final int SIZE_BOUND = (SIZE - 1);
    public static final int AREA = (SIZE * SIZE);
    public static final int VOLUME = (AREA * SIZE);

    private final World<?> world;
    private final ChunkColumn<?> column;
    private final ChunkPos position;
    private final ChunkStorage storage;

    public Chunk(ChunkColumn<?> column, ChunkPos position, ChunkStorage storage) {
        this.world = column.world();
        this.column = column;
        this.position = position;
        this.storage = storage;
    }

    public Chunk(ChunkColumn<?> column, ChunkPos position, boolean hasSkylight) {
        this(column, position, new ChunkStorage(hasSkylight));
    }


    public World<?> world() {
        return world;
    }

    public ChunkColumn<?> column() {
        return column;
    }

    public ChunkPos position() {
        return position;
    }

    public ChunkStorage storage() {
        return storage;
    }


    public BlockState getBlockState(int x, int y, int z) {
        final ChunkByteArray blockstates = storage.blockstates();
        if(blockstates.isOutOfBounds(x, y, z))
            return Block.VOID.getDefaultState();

        final int stateID = blockstates.get(x, y, z);
        if(stateID == -1)
            return Block.VOID.getDefaultState();

        return ServerRegistries.BLOCK_STATE.get(stateID);
    }

    public boolean setBlockState(int x, int y, int z, BlockState blockstate) {
        // get & validate stateID
        final ChunkByteArray blockstates = storage.blockstates();
        final int stateID = ServerRegistries.BLOCK_STATE.getID(blockstate);
        if(stateID == -1 || blockstates.isOutOfBounds(x, y, z))
            return false;

        // set state ID
        blockstates.set(x, y, z, stateID);

        // update heightmap
        column.heightmap().updateHeightAndDepth(x, position.getBlockY() + y, z, blockstate);

        // current & previous glowing
        final int[] glowing = blockstate.getBlockProperties().get(BlockProperty.GLOWING);
        final int prevLightR = this.getBlockLightLevel(x, y, z, 0);
        final int prevLightG = this.getBlockLightLevel(x, y, z, 1);
        final int prevLightB = this.getBlockLightLevel(x, y, z, 2);

        // decrease light
        if(glowing[0] < prevLightR || glowing[1] < prevLightG || glowing[2] < prevLightB)
            world.getBlockLightEngine().decrease(this, x, y, z, prevLightR, prevLightG, prevLightB);
        // increase light
        if(glowing[0] > prevLightR || glowing[1] > prevLightG || glowing[2] > prevLightB)
            world.getBlockLightEngine().increase(this, x, y, z, glowing[0], glowing[1], glowing[2]);
        // fill gap with light
        world.getBlockLightEngine().fillGapWithNeighborMaxLight(this, x, y, z);
        return true;
    }


    public byte getBlockLightLevel(int x, int y, int z, int channel) {
        final ChunkMultiByteArray blocklight = storage.blocklight();
        if(blocklight.isOutOfBounds(channel, x, y, z))
            return 0;
        return blocklight.get(channel, x, y, z);
    }

    public boolean setBlockLightLevel(int x, int y, int z, int channel, int level) {
        final ChunkMultiByteArray blocklight = storage.blocklight();
        if(blocklight.isOutOfBounds(channel, x, y, z))
            return false;
        blocklight.set(channel, x, y, z, level);
        return true;
    }

    public boolean setBlockLightLevel(int x, int y, int z, int r, int g, int b) {
        final ChunkMultiByteArray blocklight = storage.blocklight();
        if(blocklight.isOutOfBounds(x, y, z))
            return false;

        blocklight.set(0, x, y, z, r);
        blocklight.set(1, x, y, z, g);
        blocklight.set(2, x, y, z, b);
        return true;
    }


    public byte getSkyLightLevel(int x, int y, int z) {
        final ChunkByteArray skylight = storage.skylight();
        if(skylight.isOutOfBounds(x, y, z))
            return SkyLightEngine.MAX_LEVEL;
        return skylight.get(x, y, z);
    }

    public boolean setSkyLightLevel(int x, int y, int z, int level) {
        final ChunkByteArray skylight = storage.skylight();
        if(skylight.isOutOfBounds(x, y, z))
            return false;
        skylight.set(x, y, z, level);
        return true;
    }


    public int getLightLevel(int x, int y, int z, int channel) {
        final ChunkByteArray skylight = storage.skylight();
        if(skylight.isOutOfBounds(x, y, z))
            return SkyLightEngine.MAX_LEVEL;

        final ChunkMultiByteArray blocklight = storage.blocklight();
        return Math.max(skylight.get(x, y, z), blocklight.get(channel, x, y, z));
    }


    public void forEach(XYZConsumer consumer) {
        for(int y = 0; y < Chunk.SIZE; y++)
            for(int x = 0; x < Chunk.SIZE; x++)
                for(int z = 0; z < Chunk.SIZE; z++)
                    consumer.accept(x, y, z);
    }

    public void forEach(XZConsumer consumer) {
        for(int x = 0; x < Chunk.SIZE; x++)
            for(int z = 0; z < Chunk.SIZE; z++)
                consumer.accept(x, z);
    }

}
