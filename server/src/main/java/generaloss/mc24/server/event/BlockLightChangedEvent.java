package generaloss.mc24.server.event;

import generaloss.mc24.server.chunk.Chunk;

public interface BlockLightChangedEvent {

    void invoke(Chunk chunk, int x, int y, int z, int r, int g, int b);

}
