package generaloss.mc24.server.chunkload;

import generaloss.mc24.server.world.ServerWorld;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ServerChunkLoader {

    private final ServerWorld world;
    private final List<IChunkLoadEntry> loaderEntriesList;
    private final ChunkLoadPoint spawnAreaLoader;

    public ServerChunkLoader(ServerWorld world) {
        this.world = world;
        this.loaderEntriesList = new CopyOnWriteArrayList<>();
        // spawn
        this.spawnAreaLoader = new ChunkLoadPoint();
        this.addLoaderEntry(this.spawnAreaLoader);
    }


    public void addLoaderEntry(IChunkLoadEntry loaderEntry) {
        loaderEntriesList.add(loaderEntry);
    }

    public void removeLoaderEntry(IChunkLoadEntry loaderEntry) {
        loaderEntriesList.remove(loaderEntry);
    }


    public ChunkLoadPoint getSpawnPoint() {
        return spawnAreaLoader;
    }


    public void update() {
        //loaderEntriesList
    }

}
