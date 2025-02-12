package generaloss.mc24.server.column;

import generaloss.mc24.server.chunk.ServerChunk;
import generaloss.mc24.server.world.ServerWorld;

public class ServerChunkColumn extends ChunkColumn<ServerChunk> {

    public ServerChunkColumn(ServerWorld world, ColumnPos position) {
        super(world, position);
    }

}
