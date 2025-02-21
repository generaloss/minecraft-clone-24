package generaloss.mc24.server.chunk;

import generaloss.mc24.server.block.Block;
import generaloss.mc24.server.block.BlockProperty;
import generaloss.mc24.server.block.BlockState;
import generaloss.mc24.server.column.ChunkColumn;
import generaloss.mc24.server.common.XYZConsumer;
import generaloss.mc24.server.common.XZConsumer;
import generaloss.mc24.server.light.BlockLightEngine;
import generaloss.mc24.server.light.SkyLightEngine;
import generaloss.mc24.server.registry.ServerRegistries;
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
        final int stateID = blockstates.get(x, y, z);
        if(stateID == -1)
            return Block.VOID.getDefaultState();

        return ServerRegistries.BLOCK_STATE.get(stateID);
    }

    public void setBlockState(int x, int y, int z, BlockState blockstate) {
        // set stateID
        final int stateID = ServerRegistries.BLOCK_STATE.getID(blockstate);
        storage.blockstates().set(x, y, z, stateID);

        // current & previous glowing
        final int[] glowing = blockstate.getBlockProperties().get(BlockProperty.GLOWING);
        final int prevLightR = this.getBlockLightLevel(x, y, z, 0);
        final int prevLightG = this.getBlockLightLevel(x, y, z, 1);
        final int prevLightB = this.getBlockLightLevel(x, y, z, 2);

        // block light
        final BlockLightEngine<?> blocklightEngine = world.getBlockLightEngine();
        // decrease light
        if(glowing[0] < prevLightR || glowing[1] < prevLightG || glowing[2] < prevLightB)
            blocklightEngine.decrease(this, x, y, z, prevLightR, prevLightG, prevLightB);
        // increase light
        if(glowing[0] > prevLightR || glowing[1] > prevLightG || glowing[2] > prevLightB)
            blocklightEngine.increase(this, x, y, z, glowing[0], glowing[1], glowing[2]);
        // fill gap with light
        blocklightEngine.fillGapWithNeighborMaxLight(this, x, y, z);

        // skylight
        final SkyLightEngine<?> skylightEngine = world.getSkyLightEngine();
        // decrease light
        skylightEngine.decrease(column, x, y, z, this.getSkyLightLevel(x, y, z));
        // increase light & update heightmap
        column.heightmap().updateHeight(x, position.getBlockY() + y, z, blockstate);
        // fill gap with light
        skylightEngine.fillGapWithNeighborMaxLight(column, x, y, z);
    }


    public byte getBlockLightLevel(int x, int y, int z, int channel) {
        final ChunkMultiByteArray blocklight = storage.blocklight();
        return blocklight.get(channel, x, y, z);
    }

    public void setBlockLightLevel(int x, int y, int z, int channel, int level) {
        final ChunkMultiByteArray blocklight = storage.blocklight();
        blocklight.set(channel, x, y, z, level);
    }

    public void setBlockLightLevel(int x, int y, int z, int r, int g, int b) {
        final ChunkMultiByteArray blocklight = storage.blocklight();
        blocklight.set(0, x, y, z, r);
        blocklight.set(1, x, y, z, g);
        blocklight.set(2, x, y, z, b);
    }


    public byte getSkyLightLevel(int x, int y, int z) {
        final ChunkByteArray skylight = storage.skylight();
        return skylight.get(x, y, z);
    }

    public void setSkyLightLevel(int x, int y, int z, int level) {
        final ChunkByteArray skylight = storage.skylight();
        skylight.set(x, y, z, level);
    }


    public int getLightLevel(int x, int y, int z, int channel) {
        final ChunkByteArray skylight = storage.skylight();
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
