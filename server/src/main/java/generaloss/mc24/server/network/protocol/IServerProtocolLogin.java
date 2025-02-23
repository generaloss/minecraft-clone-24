package generaloss.mc24.server.network.protocol;

import generaloss.mc24.server.network.packet2s.EncodeKeyPacket2S;
import generaloss.mc24.server.network.packet2s.LoginRequestPacket2S;
import generaloss.mc24.server.network.packet2s.ServerInfoRequestPacket2S;
import generaloss.mc24.server.network.packet2s.SessionIDPacket2S;

public interface IServerProtocolLogin {

    void handleServerInfoRequestPacket(ServerInfoRequestPacket2S packet);

    void handleLoginRequestPacket(LoginRequestPacket2S packet);

    void handleConnectionKeyPacket(EncodeKeyPacket2S packet);

    void handleSessionIDPacket(SessionIDPacket2S packet);

}
