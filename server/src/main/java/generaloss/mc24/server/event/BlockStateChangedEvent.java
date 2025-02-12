package generaloss.mc24.server.event;

import generaloss.mc24.server.block.BlockState;
import generaloss.mc24.server.chunk.Chunk;
import generaloss.mc24.server.world.World;

public interface BlockStateChangedEvent<W extends World<? extends C>, C extends Chunk<? extends W>> {

    void invoke(C chunk, int x, int y, int z, BlockState state);

}
