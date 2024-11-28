package generaloss.mc24.server.chunk;

import generaloss.mc24.server.block.BlockState;
import generaloss.mc24.server.registry.Registries;
import generaloss.mc24.server.world.World;

public class Chunk <W extends World<? extends Chunk<W>>> {

    public static final int SIZE = 32;
    public static final int AREA = SIZE * SIZE;
    public static final int VOLUME = AREA * SIZE;

    private final W world;
    private final ChunkPos position;
    private final Registries registries;
    private final ByteNibbleArray blockStateIndices;
    private final ByteNibbleArray blockLightLevels;

    public Chunk(W world, ChunkPos position, Registries registries) {
        this.world = world;
        this.position = position;
        this.registries = registries;
        this.blockStateIndices = new ByteNibbleArray();
        this.blockLightLevels = new ByteNibbleArray();
    }


    public W world() {
        return world;
    }

    public ChunkPos position() {
        return position;
    }

    public BlockState getBlockState(int x, int y, int z) {
        final int stateID = blockStateIndices.get(x, y, z);
        if(stateID == -1)
            return null;
        return registries.getBlockState(stateID);
    }

    public boolean setBlockState(int x, int y, int z, BlockState blockState) {
        final int stateID = registries.getBlockStateID(blockState);
        if(stateID == -1)
            return false;
        return blockStateIndices.set(x, y, z, stateID);
    }


    public byte getBlockLightLevel(int x, int y, int z) {
        return blockLightLevels.get(x, y, z);
    }

    public boolean setBlockLightLevel(int x, int y, int z, int lightLevel) {
        return blockLightLevels.set(x, y, z, lightLevel);
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
