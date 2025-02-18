package generaloss.mc24.server.world;

import generaloss.mc24.server.Facing;
import generaloss.mc24.server.chunk.Chunk;
import generaloss.mc24.server.chunk.ChunkPos;
import generaloss.mc24.server.column.ChunkColumn;
import generaloss.mc24.server.column.ColumnCache;

public class SkyLightEngine<C extends Chunk> {

    public static final int MAX_LEVEL = Chunk.SIZE_BOUND;

    private final ColumnCache<C> columnCache;

    public SkyLightEngine(World<C> world) {
        this.columnCache = new ColumnCache<>(world);
    }

    public void diffuseSkyLight(ChunkColumn<C> column) {
        columnCache.cacheNeighborsFor(column);

        column.forEach((x, z) -> {
            final int height = (column.getHeight(x, z) + 1);

            int maxNeighborHeight = height;
            for(Facing facing: Facing.values()){
                final int nx = (x + facing.getX());
                final int nz = (z + facing.getZ());

                final int neighborHeight = (columnCache.getHeight(nx, nz) + 1);
                maxNeighborHeight = Math.max(maxNeighborHeight, neighborHeight);
            }

            for(int y = height; y <= maxNeighborHeight; y++){
                final C chunk = column.getChunk(ChunkPos.byBlock(y));
                chunk.setSkyLightLevel(x, y & Chunk.SIZE_BOUND, z, SkyLightEngine.MAX_LEVEL);
            }
        });
    }

}
