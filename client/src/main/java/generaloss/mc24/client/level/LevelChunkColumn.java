package generaloss.mc24.client.level;

import generaloss.mc24.server.column.ChunkColumn;
import generaloss.mc24.server.column.ColumnPos;

public class LevelChunkColumn extends ChunkColumn<LevelChunk> {

    public LevelChunkColumn(WorldLevel level, ColumnPos position) {
        super(level, position);
    }

}
