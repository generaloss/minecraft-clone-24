package generaloss.mc24.server.network.protocol;

import generaloss.mc24.server.network.packet2c.AbilitiesPacket2C;
import generaloss.mc24.server.network.packet2c.ChunkPacket2C;
import generaloss.mc24.server.network.packet2c.EntityMovePacket2C;
import generaloss.mc24.server.network.packet2c.SetBlockStatePacket2C;
import jpize.util.net.tcp.packet.INetPacketHandler;

public interface IClientProtocolGame extends INetPacketHandler {

    void handleChunk(ChunkPacket2C packet);

    void handleSetBlockState(SetBlockStatePacket2C packet);

    void handleAbilitiesPacket(AbilitiesPacket2C packet);

    void handleEntityMove(EntityMovePacket2C packet);

}
