package generaloss.mc24.server.world;

import generaloss.mc24.server.chunk.Chunk;

public class BlockLightEngine <W extends World<?>> {

    private final W world;
    private final Chunk<W>[][][] chunkCache;

    public BlockLightEngine(W world) {
        this.world = world;
        this.chunkCache = new Chunk[16][16][16];
    }

    public void propagateLight(Chunk<W> chunk) {

    }

}
