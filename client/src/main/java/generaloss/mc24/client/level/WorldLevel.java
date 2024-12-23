package generaloss.mc24.client.level;

import generaloss.mc24.client.Main;
import generaloss.mc24.client.chunk.ChunkTesselator;
import generaloss.mc24.server.chunk.Chunk;
import generaloss.mc24.server.world.World;
import jpize.util.Disposable;
import jpize.util.camera.PerspectiveCamera;

public class WorldLevel extends World<LevelChunk> implements Disposable {

    private final Main context;
    private final ChunkTesselator tesselator;
    private final LevelRenderer renderer;

    public WorldLevel(Main context) {
        this.context = context;
        this.tesselator = new ChunkTesselator(context, this);
        this.renderer = new LevelRenderer(context, this);
        // callbacks
        super.getBlockLightEngine().registerIncreasedCallback((chunk, x, y, z, r, g, b) -> {
            for(Chunk<?> cachedChunk: super.getBlockLightEngine().chunkCache().getChunks())
                tesselator.tesselate((LevelChunk) cachedChunk);
        });
        super.registerBlockStateChangedCallback((chunk, x, y, z, state) ->
            tesselator.tesselate(chunk)
        );
    }

    public Main context() {
        return context;
    }

    public ChunkTesselator tesselator() {
        return tesselator;
    }


    public void update() {
        tesselator.update();
    }

    public void render(PerspectiveCamera camera) {
        renderer.render(camera);
    }


    public void reset() {
        for(LevelChunk chunk: this.getChunks())
            chunk.freeMesh();
        super.clearChunks();
    }

    @Override
    public void dispose() {
        this.reset();
        tesselator.dispose();
    }

}
