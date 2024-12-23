package generaloss.mc24.server.world;

import generaloss.mc24.server.Server;
import generaloss.mc24.server.chunk.Chunk;
import generaloss.mc24.server.chunk.ServerChunk;
import generaloss.mc24.server.chunkload.WorldChunkLoader;
import generaloss.mc24.server.network.packet2c.SetBlockStatePacket2C;
import generaloss.mc24.server.worldgen.IChunkGenerator;
import jpize.util.time.Tickable;

public class ServerWorld extends World<Chunk<ServerWorld>> implements Tickable {

    private final Server server;
    private final String ID;
    private final IChunkGenerator chunkGenerator;
    private final WorldChunkLoader chunkLoader;

    public ServerWorld(Server server, String ID, IChunkGenerator chunkGenerator) {
        this.server = server;
        this.ID = ID;
        this.chunkGenerator = chunkGenerator;
        this.chunkLoader = new WorldChunkLoader(this);
        super.registerBlockStateChangedCallback((chunk, x, y, z, state) -> {
            if(!((ServerChunk) chunk).isLoaded())
                return;

            final int stateID = server.registries().BLOCK_STATES.getID(state);
            server.net().tcpServer().broadcast(new SetBlockStatePacket2C(chunk.position(), x, y, z, stateID));
        });
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

    public WorldChunkLoader getChunkLoader() {
        return chunkLoader;
    }


    @Override
    public void tick() {

    }

}
