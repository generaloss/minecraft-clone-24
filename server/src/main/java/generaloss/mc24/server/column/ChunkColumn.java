package generaloss.mc24.server.column;

import generaloss.mc24.server.chunk.Chunk;
import generaloss.mc24.server.world.World;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class ChunkColumn<W extends World<? extends Chunk<W>>> {

    private final Map<Integer, Chunk<W>> chunks;

    public ChunkColumn() {
        this.chunks = new ConcurrentHashMap<>();
    }


    public Chunk<W> getChunk(int chunkY) {
        return chunks.get(chunkY);
    }

    public void putChunk(Chunk<W> chunk) {

    }

    public void removeChunk(int chunkY) {

    }

}
