package generaloss.mc24.server.network.protocol;

import generaloss.mc24.server.network.packet2s.SetBlockStatePacket2S;
import jpize.util.net.tcp.packet.PacketHandler;

public interface IServerProtocolGame extends PacketHandler {

    void handleSetBlockState(SetBlockStatePacket2S packet);

}
