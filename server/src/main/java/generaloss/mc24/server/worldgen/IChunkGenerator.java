package generaloss.mc24.server.worldgen;

import generaloss.mc24.server.chunk.ServerChunk;

public interface IChunkGenerator {

    void generateBase(ServerChunk chunk);

    void generateDecoration(ServerChunk chunk);

}
