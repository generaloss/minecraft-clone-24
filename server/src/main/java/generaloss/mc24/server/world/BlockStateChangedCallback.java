package generaloss.mc24.server.world;

import generaloss.mc24.server.block.BlockState;
import generaloss.mc24.server.chunk.Chunk;

public interface BlockStateChangedCallback <W extends World<C>, C extends Chunk<? extends W>> {

    void invoke(C chunk, int x, int y, int z, BlockState state);

}
