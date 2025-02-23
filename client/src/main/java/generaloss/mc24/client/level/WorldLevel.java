package generaloss.mc24.client.level;

import generaloss.mc24.client.Main;
import generaloss.mc24.client.meshing.chunk.ChunkTesselatorPool;
import generaloss.mc24.client.renderer.LevelRenderer;
import generaloss.mc24.server.chunk.Chunk;
import generaloss.mc24.server.chunk.ChunkPos;
import generaloss.mc24.server.chunk.ChunkStorage;
import generaloss.mc24.server.column.ChunkColumn;
import generaloss.mc24.server.column.ColumnCache;
import generaloss.mc24.server.column.ColumnPos;
import generaloss.mc24.server.event.Events;
import generaloss.mc24.server.network.packet2s.SetBlockStatePacket2S;
import generaloss.mc24.server.registry.ServerRegistries;
import generaloss.mc24.server.world.World;
import jpize.util.Disposable;
import jpize.util.camera.PerspectiveCamera;

public class WorldLevel extends World<LevelChunk> implements Disposable {

    private final Main context;
    private final ChunkTesselatorPool tesselators;
    private final LevelRenderer renderer;

    public WorldLevel(Main context) {
        this.context = context;
        this.tesselators = new ChunkTesselatorPool(16, this);
        this.renderer = new LevelRenderer(context, this);

        // blockstate changed event
        Events.registerBlockstateChanged((chunk, localX, localY, localZ, blockstate) -> {
            // send packet
            context.network().sendPacket(new SetBlockStatePacket2S(
                chunk.position(), localX, localY, localZ, ServerRegistries.BLOCK_STATE.getID(blockstate)
            ));

            // tesselate chunk
            tesselators.tesselate((LevelChunk) chunk);

            // tesselate neighbor chunks
            final int chunkX = (localX == 0 ? -1 : (localX == Chunk.SIZE_BOUND ? 1 : 0));
            final int chunkY = (localY == 0 ? -1 : (localY == Chunk.SIZE_BOUND ? 1 : 0));
            final int chunkZ = (localZ == 0 ? -1 : (localZ == Chunk.SIZE_BOUND ? 1 : 0));
            final int count = (Math.abs(chunkX) + Math.abs(chunkY) + Math.abs(chunkZ));
            if(count > 0) {
                tesselators.tesselate(this.getChunk(chunk.position().getNeighbor(chunkX, chunkY, chunkZ)));
            }
            if(count > 1) {
                if(chunkX == 0){
                    tesselators.tesselate(this.getChunk(chunk.position().getNeighbor(0, chunkY, 0)));
                    tesselators.tesselate(this.getChunk(chunk.position().getNeighbor(0, 0, chunkZ)));
                }else if(chunkY == 0){
                    tesselators.tesselate(this.getChunk(chunk.position().getNeighbor(chunkX, 0, 0)));
                    tesselators.tesselate(this.getChunk(chunk.position().getNeighbor(0, 0, chunkZ)));
                }else if(chunkZ == 0){
                    tesselators.tesselate(this.getChunk(chunk.position().getNeighbor(chunkX, 0, 0)));
                    tesselators.tesselate(this.getChunk(chunk.position().getNeighbor(0, chunkY, 0)));
                }else{
                    tesselators.tesselate(this.getChunk(chunk.position().getNeighbor(0, chunkY, chunkZ)));
                    tesselators.tesselate(this.getChunk(chunk.position().getNeighbor(chunkX, 0, chunkZ)));
                    tesselators.tesselate(this.getChunk(chunk.position().getNeighbor(chunkX, chunkY, 0)));
                    tesselators.tesselate(this.getChunk(chunk.position().getNeighbor(0, 0, chunkZ)));
                    tesselators.tesselate(this.getChunk(chunk.position().getNeighbor(0, chunkY, 0)));
                    tesselators.tesselate(this.getChunk(chunk.position().getNeighbor(chunkX, 0, 0)));
                }
            }
        });

        // blocklight changed event
        Events.registerBlockLightChanged((_chunk, _x, _y, _z, _r, _g, _b) -> {
            // tesselate all cached chunks
            super.getBlockLightEngine().chunkCache().forEach(tesselators::tesselate);
        });

        // skylight changed event
        Events.registerSkyLightChanged((_column, _x, _z, lowY, highY, _level) -> {
            // tesselate all cached chunks
            final ColumnCache<?> columnCache = super.getSkyLightEngine().columnCache();

            final int lowChunkY = ChunkPos.byBlock(lowY - 1);
            final int highChunkY = ChunkPos.byBlock(highY + 1);

            for(int columnX = -1; columnX < 2; columnX++){
                for(int columnZ = -1; columnZ < 2; columnZ++){
                    final ChunkColumn<?> column = columnCache.get(columnX, columnZ);
                    if(column == null)
                        continue;
                    for(Chunk chunk: column.getChunks(lowChunkY, highChunkY))
                        tesselators.tesselate((LevelChunk) chunk);
                }
            }
        });
    }

    public Main context() {
        return context;
    }

    public ChunkTesselatorPool tesselators() {
        return tesselators;
    }


    @Override
    protected ChunkColumn<LevelChunk> createColumn(ColumnPos position) {
        return new LevelChunkColumn(this, position);
    }

    @Override
    public LevelChunk createChunk(ChunkPos position, ChunkStorage storage) {
        final LevelChunkColumn column = (LevelChunkColumn) super.createAndGetColumn(position.getX(), position.getZ());
        final LevelChunk chunk = new LevelChunk(column, position, storage);
        column.putChunk(chunk);

        column.heightmap().onClientCreatedChunk(column, chunk);

        tesselators.tesselate(chunk);
        tesselators.tesselate(super.getChunk(position.getNeighbor( 1,  0,  0)));
        tesselators.tesselate(super.getChunk(position.getNeighbor( 0,  1,  0)));
        tesselators.tesselate(super.getChunk(position.getNeighbor( 0,  0,  1)));
        tesselators.tesselate(super.getChunk(position.getNeighbor(-1,  0,  0)));
        tesselators.tesselate(super.getChunk(position.getNeighbor( 0, -1,  0)));
        tesselators.tesselate(super.getChunk(position.getNeighbor( 0,  0, -1)));
        return chunk;
    }


    public void update() {
        tesselators.update();
    }

    public void render(PerspectiveCamera camera) {
        renderer.render(camera);
    }


    public void reset() {
        super.clearColumns();
        tesselators.reset();
        super.forEachChunk(chunk -> {
            chunk.freeMesh();
            return true;
        });
    }

    @Override
    public void dispose() {
        this.reset();
        tesselators.dispose();
    }

}
