package generaloss.mc24.server.column;

import generaloss.mc24.server.chunk.Chunk;
import generaloss.mc24.server.common.IntSortedList;
import generaloss.mc24.server.world.World;

import java.util.concurrent.CopyOnWriteArrayList;

public abstract class ChunkColumn<W extends World<? extends Chunk<W>>> {

    private final IntSortedList<Chunk<W>> chunks;

    public ChunkColumn() {
        this.chunks = new IntSortedList<>(
            new CopyOnWriteArrayList<>(),
            chunk -> chunk.position().getY()
        );
    }

    public Iterable<Chunk<W>> chunks() {
        return chunks.list();
    }

    public Iterable<Chunk<W>> chunks(int startChunkY, int endChunkY) {
        return chunks.sublist(startChunkY, endChunkY);
    }


    public Chunk<W> getChunk(int chunkY) {
        return chunks.get(chunkY);
    }

    public void putChunk(Chunk<W> chunk) {
        chunks.put(chunk);
    }

    public Chunk<W> removeChunk(Chunk<W> chunk) {
        return chunks.remove(chunk);
    }

    public Chunk<W> removeChunk(int chunkY) {
        return chunks.remove(chunkY);
    }

}
