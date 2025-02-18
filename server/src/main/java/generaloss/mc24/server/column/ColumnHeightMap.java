package generaloss.mc24.server.column;

import generaloss.mc24.server.block.BlockState;
import generaloss.mc24.server.chunk.Chunk;
import generaloss.mc24.server.chunk.ChunkPos;

import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

public class ColumnHeightMap {

    public static final int NO_HEIGHT = Integer.MIN_VALUE;

    private final ChunkColumn<? extends Chunk> column;
    private final Predicate<BlockState> predicate;
    private final ColumnIntArray max, min;

    public ColumnHeightMap(ChunkColumn<? extends Chunk> column, Predicate<BlockState> predicate) {
        this.column = column;
        this.predicate = predicate;
        this.min = new ColumnIntArray(NO_HEIGHT);
        this.max = new ColumnIntArray(NO_HEIGHT);
    }

    public ColumnIntArray max() {
        return max;
    }

    public ColumnIntArray min() {
        return min;
    }

    public void updateHeightAndDepth(int localX, int y, int localZ, BlockState blockstate) {
        this.updateHeight(localX, y, localZ, blockstate);
        this.updateDepth(localX, y, localZ, blockstate);
    }

    public void updateHeight(int localX, int y, int localZ, BlockState blockstate) {
        final int height = max.get(localX, localZ);
        if(height > y) // impossible with height=NO_HEIGHT
            return;

        final boolean isOpaque = predicate.test(blockstate);
        if(height == y){ // impossible with height=NO_HEIGHT
            if(!isOpaque) {
                int topChunkY = ChunkPos.byBlock(--y);
                final List<? extends Chunk> chunks = column.getChunksTo(topChunkY);
                Collections.reverse(chunks);

                Chunk chunk = chunks.iterator().next();
                while(chunk != null) {
                    int localY = (y & Chunk.SIZE_BOUND);
                    do{
                        final BlockState downBlockstate = chunk.getBlockState(localX, localY, localZ);
                        if(predicate.test(downBlockstate)) {
                            max.set(localX, localZ, y);
                            return;
                        }
                        y--;
                        localY--;
                    }while(localY >= 0);

                    // next chunk down
                    chunk = chunks.iterator().next();
                }
            }
        }else if(isOpaque){
            max.set(localX, localZ, y);
        }
    }

    public void updateDepth(int localX, int y, int localZ, BlockState blockstate) {
        final int depth = min.get(localX, localZ);
        if(depth != NO_HEIGHT && depth < y)
            return;

        final boolean isOpaque = predicate.test(blockstate);
        if(depth == y){ // impossible with depth=NO_HEIGHT
            if(!isOpaque) {
                int downChunkY = ChunkPos.byBlock(++y);
                final List<? extends Chunk> chunks = column.getChunksFrom(downChunkY);

                Chunk chunk = chunks.iterator().next();
                while(chunk != null) {
                    int localY = (y & Chunk.SIZE_BOUND);
                    do{
                        final BlockState upBlockstate = chunk.getBlockState(localX, localY, localZ);
                        if(predicate.test(upBlockstate)) {
                            min.set(localX, localZ, y);
                            return;
                        }
                        y++;
                        localY++;
                    }while(localY < Chunk.SIZE);

                    // next chunk up
                    chunk = chunks.iterator().next();
                }
            }
        }else if(isOpaque){
            min.set(localX, localZ, y);
        }
    }


    public void updateHeightAndDepth() {
        this.updateHeight();
        this.updateDepth();
    }

    public void updateHeight() {

    }

    public void updateDepth() {

    }

}
