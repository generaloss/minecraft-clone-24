package generaloss.mc24.client.level;

import generaloss.mc24.server.column.ChunkColumn;
import generaloss.mc24.server.column.ColumnPos;

public class ClientChunkColumn extends ChunkColumn<LevelChunk> {

    public ClientChunkColumn(WorldLevel level, ColumnPos position) {
        super(level, position);
    }

}
