package generaloss.mc24.server.column;

import generaloss.mc24.server.chunk.Chunk;
import generaloss.mc24.server.common.IntSortedList;
import generaloss.mc24.server.world.World;

import java.util.concurrent.CopyOnWriteArrayList;

public abstract class ChunkColumn<C extends Chunk<? extends World<C>>> {

    private final World<C> world;
    private final ColumnPos position;
    private final IntSortedList<C> chunks;

    public ChunkColumn(World<C> world, ColumnPos position) {
        this.world = world;
        this.position = position;
        this.chunks = new IntSortedList<>(new CopyOnWriteArrayList<>(),
            chunk -> chunk.position().getY()
        );
    }

    public World<C> world() {
        return world;
    }

    public ColumnPos position() {
        return position;
    }


    public int size() {
        return chunks.size();
    }

    public Iterable<C> getChunks() {
        return chunks.list();
    }

    public Iterable<C> getChunks(int startChunkY, int endChunkY) {
        return chunks.sublist(startChunkY, endChunkY);
    }


    public C getChunk(int chunkY) {
        return chunks.get(chunkY);
    }

    public void putChunk(C chunk) {
        chunks.put(chunk);
    }

    public C removeChunk(C chunk) {
        return chunks.remove(chunk);
    }

    public C removeChunk(int chunkY) {
        return chunks.remove(chunkY);
    }

}
