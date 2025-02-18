package generaloss.mc24.server.chunkload;

import generaloss.mc24.server.chunk.ChunkPos;
import generaloss.mc24.server.chunk.ChunkStorage;
import generaloss.mc24.server.chunk.ServerChunk;
import generaloss.mc24.server.column.ChunkColumn;
import generaloss.mc24.server.world.ServerWorld;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class WorldLoader {

    private final ServerWorld world;
    private final List<IChunkLoadEntry> loadEntriesList;

    public WorldLoader(ServerWorld world) {
        this.world = world;
        this.loadEntriesList = new CopyOnWriteArrayList<>();
        // spawn loader
        this.addLoaderEntry(world.getSpawnPoint());
        // load chunks
        this.init();
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


    private void init() {
        System.out.println("[INFO]: Preparing spawn area..");
        for(int y = -2; y < 2; y++){
            this.loadChunk(0, y, 0);
            for(int r = 0; r < 6; r++){
                for(int i = -r; i <= r - 1; i++)
                    this.loadChunk(i, y, r);
                for(int i = -r + 1; i <= r; i++)
                    this.loadChunk(r, y, i);
                for(int i = -r + 1; i <= r; i++)
                    this.loadChunk(i, y, -r);
                for(int i = -r; i <= r - 1; i++)
                    this.loadChunk(-r, y, i);
            }
        }
        for(ChunkColumn<ServerChunk> column: world.getColumns()) {
            for(ServerChunk chunk: column.getChunks()){
                world.getChunkGenerator().generateDecoration(chunk);
                chunk.markLoaded();
            }
        }
        for(ChunkColumn<ServerChunk> column: world.getColumns())
            world.getSkyLightEngine().diffuseSkyLight(column);

        System.out.println("[INFO]: " + world.getColumns().size() + " chunks loaded.");
    }

    private void loadChunk(int chunkX, int chunkY, int chunkZ) {
        final ChunkPos position = new ChunkPos(chunkX, chunkY, chunkZ);
        final ServerChunk chunk = world.createChunk(position, new ChunkStorage(true));
        world.getChunkGenerator().generateBase(chunk);
    }

}
