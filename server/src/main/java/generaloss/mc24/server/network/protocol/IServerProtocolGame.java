package generaloss.mc24.server.network.protocol;

import generaloss.mc24.server.network.packet2s.PlayerMovePacket2S;
import generaloss.mc24.server.network.packet2s.SetBlockStatePacket2S;
import jpize.util.net.tcp.packet.INetPacketHandler;

public interface IServerProtocolGame extends INetPacketHandler {

    void handleSetBlockState(SetBlockStatePacket2S packet);

    void handlePlayerMove(PlayerMovePacket2S packet);

}
