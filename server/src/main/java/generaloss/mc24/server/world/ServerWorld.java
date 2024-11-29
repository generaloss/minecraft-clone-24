package generaloss.mc24.server.world;

import generaloss.mc24.server.chunk.Chunk;

public class ServerWorld extends World<Chunk<ServerWorld>> {

    private final String ID;

    public ServerWorld(String ID) {
        this.ID = ID;
    }

    public String getID() {
        return ID;
    }

}
