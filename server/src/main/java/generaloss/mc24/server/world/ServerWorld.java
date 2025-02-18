package generaloss.mc24.server.world;

import generaloss.mc24.server.Server;
import generaloss.mc24.server.chunk.ChunkPos;
import generaloss.mc24.server.chunk.ChunkStorage;
import generaloss.mc24.server.chunk.ServerChunk;
import generaloss.mc24.server.chunkload.WorldLoader;
import generaloss.mc24.server.column.ChunkColumn;
import generaloss.mc24.server.column.ColumnPos;
import generaloss.mc24.server.column.ServerChunkColumn;
import generaloss.mc24.server.worldgen.IChunkGenerator;
import jpize.util.time.Tickable;

public class ServerWorld extends World<ServerChunk> implements Tickable {

    private final Server server;
    private final String ID;
    private final IChunkGenerator chunkGenerator;
    private final WorldSpawnPoint spawnPoint;
    private final WorldLoader loader;

    public ServerWorld(Server server, String ID, IChunkGenerator chunkGenerator) {
        this.server = server;
        this.ID = ID;
        this.chunkGenerator = chunkGenerator;
        this.spawnPoint = new WorldSpawnPoint();
        this.loader = new WorldLoader(this);
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

    public WorldLoader getLoader() {
        return loader;
    }


    @Override
    public void tick() {

    }

    @Override
    protected ChunkColumn<ServerChunk> createColumn(ColumnPos position) {
        return new ServerChunkColumn(this, position);
    }

    @Override
    public ServerChunk createChunk(ChunkPos position, ChunkStorage storage) {
        final ServerChunkColumn column = (ServerChunkColumn) super.createAndGetColumn(position.getX(), position.getZ());
        final ServerChunk chunk = new ServerChunk(column, position, storage);
        column.putChunk(chunk);
        return chunk;
    }

}
