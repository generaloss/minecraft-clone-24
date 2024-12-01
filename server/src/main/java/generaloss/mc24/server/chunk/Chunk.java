package generaloss.mc24.server.chunk;

import generaloss.mc24.server.block.BlockState;
import generaloss.mc24.server.registry.Registries;
import generaloss.mc24.server.world.World;

public class Chunk <W extends World<? extends Chunk<W>>> {

    public static final int SIZE = 16;
    public static final int SIZE_BOUND = (SIZE - 1);
    public static final int AREA = SIZE * SIZE;
    public static final int VOLUME = AREA * SIZE;

    private final W world;
    private final ChunkPos position;
    private final Registries registries;
    private final ByteNibbleArray blockStateIndices;
    private final ByteNibbleArray[] blockLightLevels;

    public Chunk(W world, ChunkPos position, Registries registries) {
        this.world = world;
        this.position = position;
        this.registries = registries;
        this.blockStateIndices = new ByteNibbleArray();
        this.blockLightLevels = new ByteNibbleArray[3];
        for(int i= 0; i < blockLightLevels.length; i++)
            this.blockLightLevels[i] = new ByteNibbleArray();
    }


    public W world() {
        return world;
    }

    public ChunkPos position() {
        return position;
    }

    public BlockState getBlockState(int x, int y, int z) {
        if(blockStateIndices.isOutOfBounds(x, y, z))
            return null;
        final int stateID = blockStateIndices.get(x, y, z);
        if(stateID == -1)
            return null;
        return registries.getBlockState(stateID);
    }

    public boolean setBlockState(int x, int y, int z, BlockState state) {
        final int stateID = registries.getBlockStateID(state);
        if(stateID == -1 || blockStateIndices.isOutOfBounds(x, y, z))
            return false;
        blockStateIndices.set(x, y, z, stateID);
        return true;
    }


    public byte getBlockLightLevel(int x, int y, int z, int channel) {
        if(blockStateIndices.isOutOfBounds(x, y, z))
            return 0;
        return blockLightLevels[channel].get(x, y, z);
    }

    public boolean setBlockLightLevel(int x, int y, int z, int channel, int level) {
        if(blockStateIndices.isOutOfBounds(x, y, z))
            return false;
        blockLightLevels[channel].set(x, y, z, level);
        return true;
    }

    public boolean setBlockLightLevel(int x, int y, int z, int r, int g, int b) {
        if(blockStateIndices.isOutOfBounds(x, y, z))
            return false;
        blockLightLevels[0].set(x, y, z, r);
        blockLightLevels[1].set(x, y, z, g);
        blockLightLevels[2].set(x, y, z, b);
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
