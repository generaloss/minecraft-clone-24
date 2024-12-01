package generaloss.mc24.server.chunk;

import generaloss.mc24.server.registry.Registries;
import generaloss.mc24.server.world.ServerWorld;

public class ServerChunk extends Chunk<ServerWorld> {

    public ServerChunk(ServerWorld world, ChunkPos position, Registries registries) {
        super(world, position, registries);
    }

}
