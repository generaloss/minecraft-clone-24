package generaloss.mc24.client.chunkmesh;

import generaloss.mc24.client.level.LevelChunk;
import generaloss.mc24.client.level.WorldLevel;
import generaloss.mc24.server.Direction;
import generaloss.mc24.server.block.BlockState;
import generaloss.mc24.server.chunk.ChunkCache;

public class BlockAndLightCache {

    public static final int CENTER_BLOCK_INDEX = 13;

    private final BlockState[] blocks;
    private final int[] lightLevels;

    public BlockAndLightCache() {
        this.blocks = new BlockState[3 * 3 * 3];
        this.lightLevels = new int[3 * 3 * 3 * 3];
    }


    private int blockIndex(int i, int j, int k) {
        return (i * 9 + j * 3 + k);
    }

    public BlockState getBlockState(int i, int j, int k) {
        return blocks[this.blockIndex(i + 1, j + 1, k + 1)];
    }

    public BlockState getBlockState(Direction dir) {
        return this.getBlockState(dir.getX(), dir.getY(), dir.getZ());
    }


    private int lightIndex(int i, int j, int k) {
        return (i * 27 + j * 9 + k * 3);
    }

    public int getLightLevel(int i, int j, int k, int channel) {
        return lightLevels[this.lightIndex(i + 1, j + 1, k + 1) + channel];
    }


    public void cacheNeighborsFor(int x, int y, int z, BlockState centerBlockstate, ChunkCache<WorldLevel, LevelChunk> chunkCache) {
        for(int i = 0; i < 3; i++){
            for(int j = 0; j < 3; j++){
                for(int k = 0; k < 3; k++){

                    final int blockIndex = this.blockIndex(i, j, k);
                    if(blockIndex == CENTER_BLOCK_INDEX){
                        blocks[blockIndex] = centerBlockstate;
                        continue;
                    }

                    final int blockX = (x + i - 1);
                    final int blockY = (y + j - 1);
                    final int blockZ = (z + k - 1);

                    final BlockState neighborBlock = chunkCache.getBlockState(blockX, blockY, blockZ);
                    blocks[blockIndex] = neighborBlock;

                    final int lightIndex = this.lightIndex(i, j, k);
                    for(int channel = 0; channel < 3; channel++){
                        final int lightLevel = chunkCache.getBlockLightLevel(blockX, blockY, blockZ, channel);
                        lightLevels[lightIndex + channel] = lightLevel;
                    }
                }
            }
        }
    }

}
