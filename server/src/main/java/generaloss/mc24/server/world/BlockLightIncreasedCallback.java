package generaloss.mc24.server.world;

import generaloss.mc24.server.chunk.Chunk;

public interface BlockLightIncreasedCallback <W extends World<C>, C extends Chunk<? extends W>> {

    void invoke(C chunk, int x, int y, int z, int r, int g, int b);

}
