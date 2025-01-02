package generaloss.mc24.server.chunk;

import generaloss.mc24.server.block.Block;
import generaloss.mc24.server.block.BlockState;
import generaloss.mc24.server.registry.Registries;
import generaloss.mc24.server.world.BlockLightEngine;
import generaloss.mc24.server.world.World;
import jpize.util.math.vector.Vec3i;

public class Chunk <W extends World<? extends Chunk<W>>> {

    public static final int SIZE = 16;
    public static final int SIZE_BOUND = (SIZE - 1);
    public static final int AREA = SIZE * SIZE;
    public static final int VOLUME = AREA * SIZE;

    private final W world;
    private final ChunkPos position;
    private final Registries registries;
    private final ByteNibbleArray blockstateIDs;
    private final ByteMultiNibbleArray blockLight;

    public Chunk(W world, ChunkPos position, ByteNibbleArray blockstateIDs,
                 ByteMultiNibbleArray blockLight, Registries registries) {
        this.world = world;
        this.position = position;
        this.blockstateIDs = blockstateIDs;
        this.blockLight = blockLight;
        this.registries = registries;
    }

    public Chunk(W world, ChunkPos position, Registries registries) {
        this(world, position, new ByteNibbleArray(), new ByteMultiNibbleArray(3), registries);
    }


    public W world() {
        return world;
    }

    public ChunkPos position() {
        return position;
    }

    public Registries registries() {
        return registries;
    }

    public ByteNibbleArray getBlockStateIDs() {
        return blockstateIDs;
    }

    public ByteMultiNibbleArray getBlockLight() {
        return blockLight;
    }


    public BlockState getBlockState(int x, int y, int z) {
        if(blockstateIDs.isOutOfBounds(x, y, z))
            return Block.VOID.getDefaultState();
        final int stateID = blockstateIDs.get(x, y, z);
        if(stateID == -1)
            return Block.VOID.getDefaultState();
        return registries.BLOCK_STATES.get(stateID);
    }

    public boolean setBlockState(int x, int y, int z, BlockState state) {
        // get & validate stateID
        final int stateID = registries.BLOCK_STATES.getID(state);
        if(stateID == -1 || blockstateIDs.isOutOfBounds(x, y, z))
            return false;

        // get previous state
        //final BlockState previousState = registries.BLOCK_STATES.get(blockstateIndices.get(x, y, z));

        // set state ID
        blockstateIDs.set(x, y, z, stateID);

        // remove black area
        if(state.getBlock().properties().getInt("opacity") == BlockLightEngine.MAX_LEVEL){
            this.setBlockLightLevel(x, y, z, 0, 0, 0);
        }

        // increase glowing
        final Vec3i glowing = state.getBlock().properties().getVec3i("glowing");
        world.getBlockLightEngine().increase(this, x, y, z, glowing.x, glowing.y, glowing.z);
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
