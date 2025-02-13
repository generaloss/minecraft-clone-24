package generaloss.mc24.server.worldgen;

import generaloss.mc24.server.block.BlockState;
import generaloss.mc24.server.chunk.ServerChunk;
import generaloss.mc24.server.registry.ServerRegistries;
import jpize.util.math.Maths;

public class ChunkGeneratorFlat implements IChunkGenerator {

    @Override
    public void generateBase(ServerChunk chunk) {
        final int chunkLocalX = chunk.position().getBlockX();
        final int chunkLocalY = chunk.position().getBlockY();
        final int chunkLocalZ = chunk.position().getBlockZ();

        chunk.forEach((x, y, z) -> {
            final int worldY = (chunkLocalY + y);
            if(worldY == -1)
                chunk.setBlockState(x, y, z, ServerRegistries.BLOCK.get("grass_block").resource().getDefaultState());
            if(worldY == -2)
                chunk.setBlockState(x, y, z, ServerRegistries.BLOCK.get("dirt").resource().getDefaultState());
            if(worldY < -2)
                chunk.setBlockState(x, y, z, ServerRegistries.BLOCK.get("stone").resource().getDefaultState());
        });
    }

    @Override
    public void generateDecoration(ServerChunk chunk) {
        final int chunkLocalX = chunk.position().getBlockX();
        final int chunkLocalY = chunk.position().getBlockY();
        final int chunkLocalZ = chunk.position().getBlockZ();

        chunk.forEach((x, y, z) -> {
            // torches
            final BlockState blockstate = chunk.getBlockState(x, y, z);
            if((blockstate.isBlockID("air")) && Maths.randomBoolean(0.0002F))
                chunk.setBlockState(x, y, z, ServerRegistries.BLOCK.get("torch").resource().getDefaultState());
        });
    }

}