package generaloss.mc24.client.level;

import generaloss.mc24.client.Main;
import generaloss.mc24.client.chunkmesh.ChunkTesselatorPool;
import generaloss.mc24.server.chunk.Chunk;
import generaloss.mc24.server.chunk.ChunkPos;
import generaloss.mc24.server.network.packet2s.SetBlockStatePacket2S;
import generaloss.mc24.server.registry.ServerRegistries;
import generaloss.mc24.server.world.World;
import jpize.util.Disposable;
import jpize.util.camera.PerspectiveCamera;

import java.util.Collection;
import java.util.stream.Collectors;

public class WorldLevel extends World<LevelChunk> implements Disposable {

    private final Main context;
    private final ChunkTesselatorPool tesselators;
    private final LevelRenderer renderer;

    public WorldLevel(Main context) {
        this.context = context;
        this.tesselators = new ChunkTesselatorPool(16, this);
        this.renderer = new LevelRenderer(context, this);

        // light callback
        super.getBlockLightEngine().registerIncreasedCallback((chunk, x, y, z, r, g, b) -> {
            // tesselate all cached chunks
            super.getBlockLightEngine().chunkCache().forEach(tesselators::tesselate);
        });

        // blockstate callback
        super.registerBlockstateChangedCallback((chunk, localX, localY, localZ, blockstate) -> {
            // send packet
            context.connection().sendPacket(new SetBlockStatePacket2S(
                chunk.position(), localX, localY, localZ, ServerRegistries.BLOCK_STATE.getID(blockstate)
            ));

            // tesselate chunk
            tesselators.tesselate(chunk);

            // tesselate neighbor chunks
            final int chunkX = (localX == 0 ? -1 : (localX == Chunk.SIZE_BOUND ? 1 : 0));
            final int chunkY = (localY == 0 ? -1 : (localY == Chunk.SIZE_BOUND ? 1 : 0));
            final int chunkZ = (localZ == 0 ? -1 : (localZ == Chunk.SIZE_BOUND ? 1 : 0));
            final int count = (Math.abs(chunkX) + Math.abs(chunkY) + Math.abs(chunkZ));
            if(count > 0) {
                tesselators.tesselate(this.getChunk(chunk.position().getNeighborPacked(chunkX, chunkY, chunkZ)));
            }
            if(count > 1) {
                if(chunkX == 0){
                    tesselators.tesselate(this.getChunk(chunk.position().getNeighborPacked(0, chunkY, 0)));
                    tesselators.tesselate(this.getChunk(chunk.position().getNeighborPacked(0, 0, chunkZ)));
                }else if(chunkY == 0){
                    tesselators.tesselate(this.getChunk(chunk.position().getNeighborPacked(chunkX, 0, 0)));
                    tesselators.tesselate(this.getChunk(chunk.position().getNeighborPacked(0, 0, chunkZ)));
                }else if(chunkZ == 0){
                    tesselators.tesselate(this.getChunk(chunk.position().getNeighborPacked(chunkX, 0, 0)));
                    tesselators.tesselate(this.getChunk(chunk.position().getNeighborPacked(0, chunkY, 0)));
                }else{
                    tesselators.tesselate(this.getChunk(chunk.position().getNeighborPacked(0, chunkY, chunkZ)));
                    tesselators.tesselate(this.getChunk(chunk.position().getNeighborPacked(chunkX, 0, chunkZ)));
                    tesselators.tesselate(this.getChunk(chunk.position().getNeighborPacked(chunkX, chunkY, 0)));
                    tesselators.tesselate(this.getChunk(chunk.position().getNeighborPacked(0, 0, chunkZ)));
                    tesselators.tesselate(this.getChunk(chunk.position().getNeighborPacked(0, chunkY, 0)));
                    tesselators.tesselate(this.getChunk(chunk.position().getNeighborPacked(chunkX, 0, 0)));
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


    public Collection<LevelChunk> getSortedChunks() {
        final Collection<LevelChunk> chunks = this.getChunks();
        return chunks.stream().sorted().collect(Collectors.toList());
    }

    @Override
    public void putChunk(LevelChunk chunk) {
        super.putChunk(chunk);
        tesselators.tesselate(chunk);

        final ChunkPos position = chunk.position();
        tesselators.tesselate(super.getChunk(position.getNeighborPacked( 1,  0,  0)));
        tesselators.tesselate(super.getChunk(position.getNeighborPacked( 0,  1,  0)));
        tesselators.tesselate(super.getChunk(position.getNeighborPacked( 0,  0,  1)));
        tesselators.tesselate(super.getChunk(position.getNeighborPacked(-1,  0,  0)));
        tesselators.tesselate(super.getChunk(position.getNeighborPacked( 0, -1,  0)));
        tesselators.tesselate(super.getChunk(position.getNeighborPacked( 0,  0, -1)));
    }


    public void update() {
        tesselators.update();
    }

    public void render(PerspectiveCamera camera) {
        renderer.render(camera);
    }


    public void reset() {
        super.clearChunks();
        tesselators.reset();
        for(LevelChunk chunk: this.getChunks())
            chunk.freeMesh();
    }

    @Override
    public void dispose() {
        this.reset();
        tesselators.dispose();
    }

}
