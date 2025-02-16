package generaloss.mc24.server.worldgen;

import generaloss.mc24.server.block.BlockState;
import generaloss.mc24.server.chunk.ServerChunk;
import generaloss.mc24.server.registry.ServerRegistries;
import jpize.util.math.FastNoise;
import jpize.util.math.Maths;

public class ChunkGenerator2DNoise implements IChunkGenerator {

    private final FastNoise noise;

    public ChunkGenerator2DNoise() {
        this.noise = new FastNoise(345)
            .setFrequency(1F / 292F)
            .setFractalOctaves(3)
            .setFractalType(FastNoise.FractalType.FBM);
    }

    @Override
    public void generateBase(ServerChunk chunk) {
        final int chunkLocalX = chunk.position().getBlockX();
        final int chunkLocalY = chunk.position().getBlockY();
        final int chunkLocalZ = chunk.position().getBlockZ();

        chunk.forEach((x, z) -> {
            final int worldX = (chunkLocalX + x);
            final int worldZ = (chunkLocalZ + z);

            final int height = (Math.round(noise.get(worldX, worldZ) * 25 - chunkLocalY));
            if(height < 0)
                return;

            chunk.setBlockState(x, height, z, ServerRegistries.BLOCK.get("grass_block").resource().getDefaultState());
            chunk.setBlockState(x, height - 1, z, ServerRegistries.BLOCK.get("dirt").resource().getDefaultState());
            chunk.setBlockState(x, height - 2, z, ServerRegistries.BLOCK.get("dirt").resource().getDefaultState());

            for(int y = 0; y < height - 2; y++)
                chunk.setBlockState(x, y, z, ServerRegistries.BLOCK.get("stone").resource().getDefaultState());
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
                chunk.setBlockState(x, y, z, ServerRegistries.BLOCK.get("torch").resource().getDefaultState());
        });
    }

}