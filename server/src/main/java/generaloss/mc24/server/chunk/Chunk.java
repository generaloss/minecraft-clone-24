package generaloss.mc24.server.chunk;

import generaloss.mc24.server.block.Block;
import generaloss.mc24.server.block.BlockState;
import generaloss.mc24.server.registry.ServerRegistries;
import generaloss.mc24.server.world.World;

public abstract class Chunk<W extends World<? extends Chunk<? extends W>>> {

    public static final int SIZE = 16;
    public static final int SIZE_BOUND = (SIZE - 1);
    public static final int AREA = (SIZE * SIZE);
    public static final int VOLUME = (AREA * SIZE);

    private final W world;
    private final ChunkPos position;
    private final ChunkByteArray blockstateIDs;
    private final ChunkMultiByteArray blockLight;

    public Chunk(W world, ChunkPos position, ChunkByteArray blockstateIDs, ChunkMultiByteArray blockLight) {
        this.world = world;
        this.position = position;
        this.blockstateIDs = blockstateIDs;
        this.blockLight = blockLight;
    }

    public Chunk(W world, ChunkPos position) {
        this(world, position, new ChunkByteArray(), new ChunkMultiByteArray(3));
    }


    public W world() {
        return world;
    }

    public ChunkPos position() {
        return position;
    }

    public ChunkByteArray getBlockStateIDs() {
        return blockstateIDs;
    }

    public ChunkMultiByteArray getBlockLight() {
        return blockLight;
    }


    public BlockState getBlockState(int x, int y, int z) {
        if(blockstateIDs.isOutOfBounds(x, y, z))
            return Block.VOID.getDefaultState();

        final int stateID = blockstateIDs.get(x, y, z);
        if(stateID == -1)
            return Block.VOID.getDefaultState();

        return ServerRegistries.BLOCK_STATE.get(stateID);
    }

    public boolean setBlockState(int x, int y, int z, BlockState blockstate) {
        // get & validate stateID
        final int stateID = ServerRegistries.BLOCK_STATE.getID(blockstate);
        if(stateID == -1 || blockstateIDs.isOutOfBounds(x, y, z))
            return false;

        // set state ID
        blockstateIDs.set(x, y, z, stateID);

        // current & previous glowing
        final int[] glowing = blockstate.getBlockProperties().getIntArray("glowing");
        final int prevLightR = this.getBlockLightLevel(x, y, z, 0);
        final int prevLightG = this.getBlockLightLevel(x, y, z, 1);
        final int prevLightB = this.getBlockLightLevel(x, y, z, 2);

        // increase/decrease glowing
        if(glowing[0] < prevLightR || glowing[1] < prevLightG || glowing[2] < prevLightB) {
            world.getBlockLightEngine().decrease(this, x, y, z, prevLightR, prevLightG, prevLightB);
        }else if(glowing[0] > prevLightR || glowing[1] > prevLightG || glowing[2] > prevLightB) {
            world.getBlockLightEngine().increase(this, x, y, z, glowing[0], glowing[1], glowing[2]);
        }else{
            world.getBlockLightEngine().fillGapWithNeighborMaxLight(this, x, y, z);
        }
        return true;
    }


    public byte getBlockLightLevel(int x, int y, int z, int channel) {
        if(blockLight.isOutOfBounds(channel, x, y, z))
            return 0;
        return blockLight.get(channel, x, y, z);
    }

    public boolean setBlockLightLevel(int x, int y, int z, int channel, int level) {
        if(blockLight.isOutOfBounds(channel, x, y, z))
            return false;
        blockLight.set(channel, x, y, z, level);
        return true;
    }

    public boolean setBlockLightLevel(int x, int y, int z, int r, int g, int b) {
        if(blockstateIDs.isOutOfBounds(x, y, z))
            return false;
        blockLight.set(0, x, y, z, r);
        blockLight.set(1, x, y, z, g);
        blockLight.set(2, x, y, z, b);
        return true;
    }


    public interface Vec3iConsumer {
        void accept(int x, int y, int z);
    }

    public void forEach(Vec3iConsumer consumer) {
        for (int y = 0; y < Chunk.SIZE; y++)
            for (int x = 0; x < Chunk.SIZE; x++)
                for (int z = 0; z < Chunk.SIZE; z++)
                    consumer.accept(x, y, z);
    }

}
