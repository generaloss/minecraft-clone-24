package generaloss.mc24.server.event;

import generaloss.mc24.server.block.BlockState;
import generaloss.mc24.server.chunk.Chunk;

public interface BlockStateChangedEvent {

    void invoke(Chunk chunk, int x, int y, int z, BlockState state);

}
