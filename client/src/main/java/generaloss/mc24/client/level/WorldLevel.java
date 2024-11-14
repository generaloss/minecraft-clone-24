package generaloss.mc24.client.level;

import generaloss.mc24.client.level.tesselation.ChunkMesh;
import generaloss.mc24.client.level.tesselation.ChunkTesselator;
import jpize.util.math.Maths;

public class WorldLevel {

    private final ChunkTesselator tesselator;
    private final LevelChunk chunk;
    private ChunkMesh chunkMesh;

    public WorldLevel() {
        this.tesselator = new ChunkTesselator();
        chunk = new LevelChunk(this);
        chunk.forEachBlock((x, y, z) -> {
            if(Maths.randomBoolean(0.3F))
                chunk.setBlock(x, y, z, 1);
        });
        tesselator.tesselate(chunk, chunkMesh -> {
            this.chunkMesh = chunkMesh;
        });
        tesselator.update();
    }

    public LevelChunk getChunk() {
        return chunk;
    }

    public ChunkMesh getChunkMesh() {
        return chunkMesh;
    }

}
