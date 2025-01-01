package generaloss.mc24.server.world;

import generaloss.mc24.server.Server;
import generaloss.mc24.server.chunk.Chunk;
import generaloss.mc24.server.chunkload.WorldChunkLoader;
import generaloss.mc24.server.worldgen.IChunkGenerator;
import jpize.util.time.Tickable;

public class ServerWorld extends World<Chunk<ServerWorld>> implements Tickable {

    private final Server server;
    private final String ID;
    private final IChunkGenerator chunkGenerator;
    private final WorldSpawnPoint spawnPoint;
    private final WorldChunkLoader chunkLoader;

    public ServerWorld(Server server, String ID, IChunkGenerator chunkGenerator) {
        this.server = server;
        this.ID = ID;
        this.chunkGenerator = chunkGenerator;
        this.spawnPoint = new WorldSpawnPoint();
        this.chunkLoader = new WorldChunkLoader(this);
    }

    public Server getServer() {
        return server;
    }

    public String getID() {
        return ID;
    }

    public IChunkGenerator getChunkGenerator() {
        return chunkGenerator;
    }

    public WorldSpawnPoint getSpawnPoint() {
        return spawnPoint;
    }

    public WorldChunkLoader getChunkLoader() {
        return chunkLoader;
    }


    @Override
    public void tick() {

    }

}
