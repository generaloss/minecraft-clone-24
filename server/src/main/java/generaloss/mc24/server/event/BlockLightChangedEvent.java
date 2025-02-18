package generaloss.mc24.server.event;

import generaloss.mc24.server.chunk.Chunk;
import generaloss.mc24.server.world.World;

public interface BlockLightChangedEvent<W extends World<? extends C>, C extends Chunk> {

    void invoke(C chunk, int x, int y, int z, int r, int g, int b);

}
