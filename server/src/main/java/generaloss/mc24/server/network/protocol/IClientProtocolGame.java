package generaloss.mc24.server.network.protocol;

import generaloss.mc24.server.network.packet2c.*;

public interface IClientProtocolGame {

    void handleChunkPacket(ChunkPacket2C packet);

    void handleSetBlockStatePacket(SetBlockStatePacket2C packet);

    void handleInitPlayerPacket(InitPlayerPacket2C packet);

    void handleInsertEntityPacket(InsertEntityPacket2C packet);

    void handleRemoveEntityPacket(RemoveEntityPacket2C packet);

    void handleMoveEntityPacket(MoveEntityPacket2C packet);

}
