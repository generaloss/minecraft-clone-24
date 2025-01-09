package generaloss.mc24.server.worldgen;

import generaloss.mc24.server.block.BlockState;
import generaloss.mc24.server.chunk.ServerChunk;
import generaloss.mc24.server.registry.ServerRegistries;
import jpize.util.math.FastNoise;
import jpize.util.math.Maths;

public class ChunkGenerator01 implements IChunkGenerator {

    private final FastNoise noise;

    public ChunkGenerator01() {
        this.noise = new FastNoise(345)
            .setFrequency(1 / 48F);
    }

    @Override
    public void generateBase(ServerChunk chunk) {
        final int chunkLocalX = chunk.position().getBlockX();
        final int chunkLocalY = chunk.position().getBlockY();
        final int chunkLocalZ = chunk.position().getBlockZ();

        chunk.forEach((x, y, z) -> {
            final int worldX = (chunkLocalX + x);
            final int worldY = (chunkLocalY + y);
            final int worldZ = (chunkLocalZ + z);
            if(worldY < -1 && noise.get(worldX, worldY, worldZ) > 0F)
                chunk.setBlockState(x, y, z, ServerRegistries.BLOCK.get("stone").getDefaultState());
        });
    }

    @Override
    public void generateDecoration(ServerChunk chunk) {
        final int chunkLocalX = chunk.position().getBlockX();
        final int chunkLocalY = chunk.position().getBlockY();
        final int chunkLocalZ = chunk.position().getBlockZ();

        chunk.forEach((x, y, z) -> {
            final int worldX = (chunkLocalX + x);
            final int worldY = (chunkLocalY + y);
            final int worldZ = (chunkLocalZ + z);
            final BlockState blockstate = chunk.getBlockState(x, y, z);

            // torches
            if((blockstate.isBlockID("air")) && Maths.randomBoolean(0.0002F))
                chunk.setBlockState(x, y, z, ServerRegistries.BLOCK.get("torch").getDefaultState());

            // grass_block
            if(blockstate.isBlockID("stone") && chunk.getBlockState(x, y + 1, z).isBlockID("void", "air"))
                chunk.setBlockState(x, y, z, ServerRegistries.BLOCK.get("grass_block").getDefaultState());
        });
    }

}
