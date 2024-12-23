package generaloss.mc24.server.network.protocol;

import generaloss.mc24.server.network.packet2c.ChunkPacket2C;
import generaloss.mc24.server.network.packet2c.SetBlockStatePacket2C;
import jpize.util.net.tcp.packet.PacketHandler;

public interface IClientProtocolGame extends PacketHandler {

    void handleChunk(ChunkPacket2C packet);

    void handleSetBlockState(SetBlockStatePacket2C packet);

}
