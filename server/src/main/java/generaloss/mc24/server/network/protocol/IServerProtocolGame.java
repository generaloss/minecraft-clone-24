package generaloss.mc24.server.network.protocol;

import generaloss.mc24.server.network.packet2s.InitSessionPacket2S;
import generaloss.mc24.server.network.packet2s.PlayerMovePacket2S;
import generaloss.mc24.server.network.packet2s.SetBlockStatePacket2S;

public interface IServerProtocolGame {

    void handleInitSession(InitSessionPacket2S packet);

    void handleSetBlockState(SetBlockStatePacket2S packet);

    void handlePlayerMove(PlayerMovePacket2S packet);

}
