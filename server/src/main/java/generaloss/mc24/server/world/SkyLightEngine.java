package generaloss.mc24.server.world;

import generaloss.mc24.server.chunk.Chunk;
import generaloss.mc24.server.chunk.ChunkPos;
import generaloss.mc24.server.column.ChunkColumn;
import generaloss.mc24.server.column.ColumnCache;
import generaloss.mc24.server.column.ColumnHeightMap;

public class SkyLightEngine<C extends Chunk> {

    public static final int MAX_LEVEL = Chunk.SIZE_BOUND;

    private final ColumnCache<C> columnCache;

    public SkyLightEngine(World<C> world) {
        this.columnCache = new ColumnCache<>(world);
    }

    public void diffuseSkyLight(ChunkColumn<C> column) {
        columnCache.initFor(column);

        column.forEach((x, z) -> {
            final int height = (column.getHeight(x, z) + 1);

            int maxNeighborHeight = height;
            for(int i = -1; i < 2; i++){
                for(int j = -1; j < 2; j++){
                    if(i == 0 && j == 0)
                        continue;

                    final int neighborHeight = (columnCache.getHeight(x + i, z + j) + 1);
                    maxNeighborHeight = Math.max(maxNeighborHeight, neighborHeight);
                }
            }

            for(int y = height; y <= maxNeighborHeight; y++) {
                final C chunk = column.getChunk(ChunkPos.byBlock(y));
                chunk.setSkyLightLevel(x, y & Chunk.SIZE_BOUND, z, MAX_LEVEL);
            }
        });
    }

    public void onHeightUpdatedUp(ChunkColumn<C> column, int localX, int localZ, int prevHeight, int newHeight) {
        if(prevHeight == ColumnHeightMap.NO_HEIGHT){
            System.out.println("no update up");
            return;
        }

        System.out.println("update height up (" + prevHeight + "=>" + newHeight + ")");

        //columnCache.initFor(column);
        for(int y = prevHeight; y <= newHeight; y++) {
            final C chunk = column.getChunk(ChunkPos.byBlock(y));
            if(chunk != null)
                chunk.setSkyLightLevel(localX, y & Chunk.SIZE_BOUND, localZ, 0);
        }

        final C chunk = column.getChunk(ChunkPos.byBlock(newHeight + 1));
        if(chunk != null)
            chunk.setSkyLightLevel(localX, (newHeight + 1) & Chunk.SIZE_BOUND, localZ, MAX_LEVEL);
    }

    public void onHeightUpdatedDown(ChunkColumn<C> column, int localX, int localZ, int prevHeight, int newHeight) {
        if(prevHeight == ColumnHeightMap.NO_HEIGHT){
            System.out.println("no update down");
            return;
        }

        System.out.println("update height down (" + prevHeight + "=>" + newHeight + ")");

        //columnCache.initFor(column);
        for(int y = newHeight; y <= prevHeight; y++) {
            System.out.println("iterate to " + y);
            final C chunk = column.getChunk(ChunkPos.byBlock(y));
            if(chunk != null){
                chunk.setSkyLightLevel(localX, y & Chunk.SIZE_BOUND, localZ, MAX_LEVEL);
                System.out.println("  set " + y + " = 15");
            }
        }
    }

}
