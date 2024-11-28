package generaloss.mc24.server.world;

import generaloss.mc24.server.chunk.Chunk;
import generaloss.mc24.server.chunk.ChunkPos;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ChunkHolder <C extends Chunk<?>> {

    private final Map<Long, C> chunks;

    public ChunkHolder() {
        this.chunks = new HashMap<>();
    }

    public Collection<C> getChunks() {
        return chunks.values();
    }

    public C getChunk(long packedPosition) {
        return chunks.get(packedPosition);
    }

    public C getChunk(int chunkX, int chunkY, int chunkZ) {
        return this.getChunk(ChunkPos.pack(chunkX, chunkY, chunkZ));
    }

    public C getChunk(ChunkPos position) {
        return this.getChunk(position.getX(), position.getY(), position.getZ());
    }

    public void putChunk(C chunk) {
        chunks.put(chunk.position().pack(), chunk);
    }

    public void removeChunk(long packedPosition) {
        chunks.remove(packedPosition);
    }

    public void removeChunk(ChunkPos position) {
        this.removeChunk(position.pack());
    }

    public void removeChunk(C chunk) {
        this.removeChunk(chunk.position());
    }

}
