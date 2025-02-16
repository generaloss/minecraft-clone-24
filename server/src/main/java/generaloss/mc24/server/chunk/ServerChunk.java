package generaloss.mc24.server.chunk;

import generaloss.mc24.server.world.ServerWorld;

public class ServerChunk extends Chunk<ServerWorld> {

    private boolean loaded;

    public ServerChunk(ServerWorld world, ChunkPos position, boolean hasSkylight) {
        super(world, position, hasSkylight);
    }

    public boolean isLoaded() {
        return loaded;
    }

    public void markLoaded() {
        loaded = true;
    }

}
