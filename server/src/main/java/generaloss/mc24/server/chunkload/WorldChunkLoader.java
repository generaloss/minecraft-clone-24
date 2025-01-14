package generaloss.mc24.server.chunkload;

import generaloss.mc24.server.chunk.Chunk;
import generaloss.mc24.server.chunk.ChunkPos;
import generaloss.mc24.server.chunk.ServerChunk;
import generaloss.mc24.server.world.ServerWorld;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class WorldChunkLoader {

    private final ServerWorld world;
    private final List<IChunkLoadEntry> loadEntriesList;

    public WorldChunkLoader(ServerWorld world) {
        this.world = world;
        this.loadEntriesList = new CopyOnWriteArrayList<>();
        // spawn loader
        this.addLoaderEntry(world.getSpawnPoint());
        // load chunks
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


    private void update() {
        System.out.println("[INFO]: Preparing spawn area..");
        for(int y = -6; y < 1; y++){
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
        for(Chunk<ServerWorld> chunk: world.getChunks()) {
            if(chunk instanceof ServerChunk serverChunk) {
                world.getChunkGenerator().generateDecoration(serverChunk);
                serverChunk.markLoaded();
            }
        }
        System.out.println("[INFO]: " + world.getChunks().size() + " chunks loaded.");
    }

    private void loadChunk(int chunkX, int chunkY, int chunkZ) {
        final ChunkPos position = new ChunkPos(chunkX, chunkY, chunkZ);
        final ServerChunk chunk = new ServerChunk(world, position);
        world.getChunkGenerator().generateBase(chunk);
        world.putChunk(chunk);
    }

}
