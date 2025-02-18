package generaloss.mc24.server.chunk;

import generaloss.mc24.server.column.ServerChunkColumn;
import generaloss.mc24.server.world.ServerWorld;

public class ServerChunk extends Chunk {

    private boolean loaded;

    public ServerChunk(ServerChunkColumn column, ChunkPos position, ChunkStorage storage) {
        super(column, position, storage);
    }

    @Override
    public ServerWorld world() {
        return (ServerWorld) super.world();
    }

    @Override
    public ServerChunkColumn column() {
        return (ServerChunkColumn) super.column();
    }


    public boolean isLoaded() {
        return loaded;
    }

    public void markLoaded() {
        loaded = true;
    }

}
