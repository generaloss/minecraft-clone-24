package generaloss.mc24.server.network.protocol;

import generaloss.mc24.server.network.packet2c.*;

public interface IClientProtocolGame {

    void handleChunk(ChunkPacket2C packet);

    void handleSetBlockState(SetBlockStatePacket2C packet);

    void handleInitPlayer(InitPlayerPacket2C packet);

    void handleInsertEntity(InsertEntityPacket2C packet);

    void handleRemoveEntity(RemoveEntityPacket2C packet);

    void handleMoveEntity(MoveEntityPacket2C packet);

}
