package generaloss.mc24.server.worldgen;

import generaloss.mc24.server.chunk.ServerChunk;
import generaloss.mc24.server.registry.RegistryBlocks;
import jpize.util.math.FastNoise;
import jpize.util.math.Maths;

public class ChunkGenerator01 implements IChunkGenerator {

    private final FastNoise noise;

    public ChunkGenerator01() {
        this.noise = new FastNoise(345)
                .setFrequency(1 / 64F);
    }

    @Override
    public void generateBase(ServerChunk chunk) {
        final RegistryBlocks blocks = chunk.world().getServer().registries().BLOCKS;

        final int chunkLocalX = chunk.position().getBlockX();
        final int chunkLocalY = chunk.position().getBlockY();
        final int chunkLocalZ = chunk.position().getBlockZ();

        chunk.forEach((x, y, z) -> {
            final int worldX = (chunkLocalX + x);
            final int worldY = (chunkLocalY + y);
            final int worldZ = (chunkLocalZ + z);

            if(worldY > -10){
                if(Maths.randomBoolean(0.002F))
                    chunk.setBlockState(x, y, z, blocks.get("torch").getDefaultState());
                return;
            }

            if(noise.get(worldX, worldY, worldZ) > 0F){
                chunk.setBlockState(x, y, z, blocks.get("stone").getDefaultState());
            }else{
                if(Maths.randomBoolean(0.0002F))
                    chunk.setBlockState(x, y, z, blocks.get("torch").getDefaultState());
            }
        });

        chunk.forEach((x, y, z) -> {
            if(chunk.getBlockState(x, y, z).isBlockID("stone") && (chunk.getBlockState(x, y + 1, z) == null || chunk.getBlockState(x, y + 1, z).isBlockID("air")))
                chunk.setBlockState(x, y, z, blocks.get("grass_block").getDefaultState());
        });
    }

    @Override
    public void generateDecoration(ServerChunk chunk) {

    }

}
