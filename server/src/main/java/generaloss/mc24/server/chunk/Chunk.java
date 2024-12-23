package generaloss.mc24.server.chunk;

import generaloss.mc24.server.block.Block;
import generaloss.mc24.server.block.BlockState;
import generaloss.mc24.server.registry.Registries;
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
    private final ByteNibbleArray blockStateIndices;
    private final ByteMultiNibbleArray blockLight;

    public Chunk(W world, ChunkPos position, ByteNibbleArray blockStateIndices,
                 ByteMultiNibbleArray blockLight, Registries registries) {
        this.world = world;
        this.position = position;
        this.blockStateIndices = blockStateIndices;
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

    public ByteNibbleArray getBlockStateIndices() {
        return blockStateIndices;
    }

    public ByteMultiNibbleArray getBlockLight() {
        return blockLight;
    }


    public BlockState getBlockState(int x, int y, int z) {
        if(blockStateIndices.isOutOfBounds(x, y, z))
            return null;
        final int stateID = blockStateIndices.get(x, y, z);
        if(stateID == -1)
            return null;
        return registries.BLOCK_STATES.get(stateID);
    }

    public boolean setBlockState(int x, int y, int z, BlockState state) {
        final int stateID = registries.BLOCK_STATES.getID(state);
        if(stateID == -1 || blockStateIndices.isOutOfBounds(x, y, z))
            return false;
        blockStateIndices.set(x, y, z, stateID);
        // glowing
        final Vec3i glowing = state.getBlock().properties().getVec3i("glowing");
        this.setBlockLightLevel(x, y, z, glowing.x, glowing.y, glowing.z);
        world.getBlockLightEngine().increase(this, x, y, z, glowing.x, glowing.y, glowing.z);
        return true;
    }


    public byte getBlockLightLevel(int x, int y, int z, int channel) {
        if(blockStateIndices.isOutOfBounds(x, y, z))
            return 0;
        return blockLight.get(channel, x, y, z);
    }

    public boolean setBlockLightLevel(int x, int y, int z, int channel, int level) {
        if(blockStateIndices.isOutOfBounds(x, y, z))
            return false;
        blockLight.set(channel, x, y, z, level);
        return true;
    }

    public boolean setBlockLightLevel(int x, int y, int z, int r, int g, int b) {
        if(blockStateIndices.isOutOfBounds(x, y, z))
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
