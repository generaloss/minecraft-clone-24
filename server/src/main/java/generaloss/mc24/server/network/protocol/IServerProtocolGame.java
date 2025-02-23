package generaloss.mc24.server.network.protocol;

import generaloss.mc24.server.network.packet2s.InitSessionPacket2S;
import generaloss.mc24.server.network.packet2s.PlayerMovePacket2S;
import generaloss.mc24.server.network.packet2s.SetBlockStatePacket2S;

public interface IServerProtocolGame {

    void handleInitSessionPacket(InitSessionPacket2S packet);

    void handleSetBlockStatePacket(SetBlockStatePacket2S packet);

    void handlePlayerMovePacket(PlayerMovePacket2S packet);

}
