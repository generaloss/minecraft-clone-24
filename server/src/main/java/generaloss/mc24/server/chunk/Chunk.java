package generaloss.mc24.server.chunk;

import generaloss.mc24.server.block.BlockState;
import generaloss.mc24.server.registry.IntRegistry;
import generaloss.mc24.server.registry.Registries;
import generaloss.mc24.server.world.World;

public class Chunk {

    public static final int SIZE = 16;
    public static final int AREA = SIZE * SIZE;
    public static final int VOLUME = AREA * SIZE;

    private final World world;
    private final ChunkPos position;
    private final IntRegistry<BlockState> blockStateRegistry;
    private final IntNibbleArray blockStateIndices;

    public Chunk(World world, ChunkPos position, Registries registries) {
        this.world = world;
        this.position = position;
        this.blockStateRegistry = registries.blockState();
        this.blockStateIndices = new IntNibbleArray();
    }


    public World world() {
        return world;
    }

    public ChunkPos position() {
        return position;
    }

    public BlockState getBlockState(int x, int y, int z) {
        final int stateID = blockStateIndices.get(x, y, z);
        if(stateID == -1)
            return null;
        return blockStateRegistry.get(stateID);
    }

    public boolean setBlockState(int x, int y, int z, BlockState blockState) {
        final int stateID = blockStateRegistry.getID(blockState);
        if(stateID == -1)
            return false;
        return blockStateIndices.set(x, y, z, stateID);
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
