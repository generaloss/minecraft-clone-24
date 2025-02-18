package generaloss.mc24.server.column;

import generaloss.mc24.server.block.BlockProperty;
import generaloss.mc24.server.chunk.Chunk;
import generaloss.mc24.server.common.IntSortedList;
import generaloss.mc24.server.common.XZConsumer;
import generaloss.mc24.server.world.World;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class ChunkColumn<C extends Chunk> {

    private final World<C> world;
    private final ColumnPos position;
    private final ColumnHeightMap<C> heightmap;
    private final IntSortedList<C> chunks;

    public ChunkColumn(World<C> world, ColumnPos position) {
        this.world = world;
        this.position = position;
        this.heightmap = new ColumnHeightMap<>(this, (blockstate -> {
            final int opacity = blockstate.getBlockProperties().get(BlockProperty.OPACITY);
            return (opacity != 0);
        }));
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

    public ColumnHeightMap<C> heightmap() {
        return heightmap;
    }


    public int size() {
        return chunks.size();
    }

    public Iterable<C> getChunks() {
        return chunks.list();
    }

    public List<C> getChunks(int startChunkY, int endChunkY) {
        return chunks.sublist(startChunkY, endChunkY);
    }

    public List<C> getChunksFrom(int startChunkY) {
        return chunks.sublistFrom(startChunkY);
    }

    public List<C> getChunksTo(int endChunkY) {
        return chunks.sublistTo(endChunkY);
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


    public int getHeight(int x, int z) {
        return heightmap.max().get(x, z);
    }


    public void forEach(XZConsumer consumer) {
        for(int x = 0; x < Chunk.SIZE; x++)
            for(int z = 0; z < Chunk.SIZE; z++)
                consumer.accept(x, z);
    }

}
