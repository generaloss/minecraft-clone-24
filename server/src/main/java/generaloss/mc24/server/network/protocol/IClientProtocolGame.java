package generaloss.mc24.server.network.protocol;

import generaloss.mc24.server.network.packet2c.*;
import jpize.util.net.tcp.packet.INetPacketHandler;

public interface IClientProtocolGame extends INetPacketHandler {

    void handleChunk(ChunkPacket2C packet);

    void handleSetBlockState(SetBlockStatePacket2C packet);

    void handleAbilitiesPacket(AbilitiesPacket2C packet);

    void handleInsertEntity(InsertEntityPacket2C packet);

    void handleRemoveEntity(RemoveEntityPacket2C packet);

    void handleMoveEntity(MoveEntityPacket2C packet);

}
