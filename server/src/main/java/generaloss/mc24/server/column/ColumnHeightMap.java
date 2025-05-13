package generaloss.mc24.server.column;

import generaloss.mc24.server.block.BlockState;
import generaloss.mc24.server.chunk.Chunk;
import generaloss.mc24.server.chunk.ChunkPos;
import generaloss.mc24.server.world.World;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

public class ColumnHeightMap<C extends Chunk> {

    public static final int NO_HEIGHT = Integer.MIN_VALUE;

    private final World<C> world;
    private final ChunkColumn<C> column;
    private final Predicate<BlockState> opaqueBlockTest;
    private final ColumnIntArray max;

    public ColumnHeightMap(ChunkColumn<C> column, Predicate<BlockState> opaqueBlockTest) {
        this.world = column.world();
        this.column = column;
        this.opaqueBlockTest = opaqueBlockTest;
        this.max = new ColumnIntArray(NO_HEIGHT);
    }

    public ColumnIntArray max() {
        return max;
    }

    public void onClientCreatedChunk(ChunkColumn<C> column, C chunk) {
        final int chunkBlockY = chunk.position().getBlockY();
        column.forEach((localX, localZ) -> {
            final int prevHeight = max.get(localX, localZ);
            if(prevHeight > chunkBlockY)
                return;

            for(int localY = Chunk.SIZE_BOUND; localY >= 0; localY--){
                final BlockState blockstate = chunk.getBlockState(localX, localY, localZ);
                if(opaqueBlockTest.test(blockstate)) {
                    max.set(localX, localZ, chunkBlockY + localY);
                    return;
                }
            }
        });
    }

    public void updateHeight(int localX, int newHeight, int localZ, BlockState blockstate) {
        int prevHeight = max.get(localX, localZ);
        if(prevHeight > newHeight)
            return;

        final boolean isOpaque = opaqueBlockTest.test(blockstate);
        if(prevHeight == NO_HEIGHT) {
            if(!isOpaque)
                return;

            max.set(localX, localZ, newHeight);
        }

        if(prevHeight == newHeight){
            if(!isOpaque) {
                final int topChunkY = ChunkPos.byBlock(newHeight);
                final List<C> chunks = column.getChunksTo(topChunkY);
                Collections.reverse(chunks);
                final Iterator<C> chunkIterator = chunks.iterator();
                assert chunkIterator.hasNext();

                while(true) {
                    final C chunk = chunkIterator.next();

                    int localY = (newHeight & Chunk.SIZE_BOUND);
                    do{
                        final BlockState downBlockstate = chunk.getBlockState(localX, localY, localZ);
                        if(opaqueBlockTest.test(downBlockstate)) {
                            max.set(localX, localZ, newHeight);

                            // update skylight down
                            if(newHeight != NO_HEIGHT)
                                world.getSkyLightEngine().onHeightUpdatedDown(column, localX, localZ, prevHeight, newHeight);
                            return;
                        }
                        newHeight--;
                        localY--;
                    }while(localY >= 0);

                    if(!chunkIterator.hasNext()) {
                        newHeight = chunk.position().getBlockY();
                        max.set(localX, localZ, newHeight);

                        // update skylight down
                        world.getSkyLightEngine().onHeightUpdatedDown(column, localX, localZ, prevHeight, newHeight);
                        break;
                    }
                }
            }
        }else if(isOpaque){
            max.set(localX, localZ, newHeight);

            // update skylight up
            if(prevHeight != NO_HEIGHT)
                world.getSkyLightEngine().onHeightUpdatedUp(column, localX, localZ, prevHeight, newHeight);
        }
    }

}
