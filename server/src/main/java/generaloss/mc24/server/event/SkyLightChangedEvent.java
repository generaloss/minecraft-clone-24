package generaloss.mc24.server.event;

import generaloss.mc24.server.column.ChunkColumn;

public interface SkyLightChangedEvent {

    void invoke(ChunkColumn<?> chunk, int x, int z, int lowY, int highY, int level);

}
