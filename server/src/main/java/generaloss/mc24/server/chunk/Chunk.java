package generaloss.mc24.server.chunk;

import generaloss.mc24.server.block.Block;
import generaloss.mc24.server.block.BlockState;
import generaloss.mc24.server.registry.ServerRegistries;
import generaloss.mc24.server.world.World;
import jpize.util.math.vector.Vec3i;

public class Chunk <W extends World<? extends Chunk<W>>> {

    public static final int SIZE = 16;
    public static final int SIZE_BOUND = (SIZE - 1);
    public static final int AREA = SIZE * SIZE;
    public static final int VOLUME = AREA * SIZE;

    private final W world;
    private final ChunkPos position;
    private final ByteNibbleArray blockstateIDs;
    private final ByteMultiNibbleArray blockLight;

    public Chunk(W world, ChunkPos position, ByteNibbleArray blockstateIDs, ByteMultiNibbleArray blockLight) {
        this.world = world;
        this.position = position;
        this.blockstateIDs = blockstateIDs;
        this.blockLight = blockLight;
    }

    public Chunk(W world, ChunkPos position) {
        this(world, position, new ByteNibbleArray(), new ByteMultiNibbleArray(3));
    }


    public W world() {
        return world;
    }

    public ChunkPos position() {
        return position;
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
        return ServerRegistries.BLOCK_STATE.get(stateID);
    }

    public boolean setBlockState(int x, int y, int z, BlockState blockstate) {
        // get & validate stateID
        final int stateID = ServerRegistries.BLOCK_STATE.getID(blockstate);
        if(stateID == -1 || blockstateIDs.isOutOfBounds(x, y, z))
            return false;

        // get previous state
        final BlockState prevBlockstate = ServerRegistries.BLOCK_STATE.get(blockstateIDs.get(x, y, z));

        // set state ID
        blockstateIDs.set(x, y, z, stateID);

        // increase glowing
        final Vec3i glowing = blockstate.getBlock().properties().getVec3i("glowing");
        if(blockstate.getStatePropertyValue("lit") != null && !(boolean) blockstate.getStatePropertyValue("lit"))
            glowing.mul(0);
        world.getBlockLightEngine().increase(this, x, y, z, glowing.x, glowing.y, glowing.z);

        // remove black area
        final Vec3i prevGlowing = prevBlockstate.getBlock().properties().getVec3i("glowing");
        if(glowing.x < prevGlowing.x || glowing.y < prevGlowing.y || glowing.z < prevGlowing.z){
            world.getBlockLightEngine().decrease(this, x, y, z, prevGlowing.x, prevGlowing.y, prevGlowing.z);
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
