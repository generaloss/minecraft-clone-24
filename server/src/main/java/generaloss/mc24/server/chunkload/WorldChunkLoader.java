package generaloss.mc24.server.chunkload;

import generaloss.mc24.server.chunk.ChunkPos;
import generaloss.mc24.server.chunk.ServerChunk;
import generaloss.mc24.server.world.ServerWorld;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class WorldChunkLoader {

    private final ServerWorld world;
    private final List<IChunkLoadEntry> loadEntriesList;
    private final ChunkLoadPoint spawnLoadEntry;

    public WorldChunkLoader(ServerWorld world) {
        this.world = world;
        this.loadEntriesList = new CopyOnWriteArrayList<>();
        // spawn
        this.spawnLoadEntry = new ChunkLoadPoint();
        this.addLoaderEntry(this.spawnLoadEntry);
        // load
        this.update();
    }


    public Collection<IChunkLoadEntry> getLoadEntries() {
        return loadEntriesList;
    }

    public void addLoaderEntry(IChunkLoadEntry loaderEntry) {
        loadEntriesList.add(loaderEntry);
    }

    public void removeLoaderEntry(IChunkLoadEntry loaderEntry) {
        loadEntriesList.remove(loaderEntry);
    }


    public ChunkLoadPoint getSpawnLoadEntry() {
        return spawnLoadEntry;
    }


    private void update() {
        System.out.println("Preparing spawn area..");
        for(int y = -6; y < 0; y++){
            this.loadChunk(0, y, 0);
            for(int r = 0; r < 6; r++){
                for(int i = -r; i <= r; i++)
                    this.loadChunk(i, y, r);
                for(int i = -r + 1; i <= r; i++)
                    this.loadChunk(r, y, i);
                for(int i = -r + 1; i <= r; i++)
                    this.loadChunk(i, y, -r);
                for(int i = -r; i <= r - 1; i++)
                    this.loadChunk(-r, y, i);
            }
        }
        System.out.println(world.getChunks().size() + " chunks generated");
    }

    private void loadChunk(int chunkX, int chunkY, int chunkZ) {
        final ChunkPos position = new ChunkPos(chunkX, chunkY, chunkZ);
        final ServerChunk chunk = new ServerChunk(world, position);
        world.getChunkGenerator().generateBase(chunk);
        world.putChunk(chunk);
    }

}
