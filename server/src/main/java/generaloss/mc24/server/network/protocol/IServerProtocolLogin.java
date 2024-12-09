package generaloss.mc24.server.network.protocol;

import generaloss.mc24.server.network.packet2s.EncodeKeyPacket2S;
import generaloss.mc24.server.network.packet2s.LoginRequestPacket2S;
import generaloss.mc24.server.network.packet2s.ServerInfoRequestPacket2S;
import generaloss.mc24.server.network.packet2s.SessionIDPacket2S;
import jpize.util.net.tcp.packet.PacketHandler;

public interface IServerProtocolLogin extends PacketHandler {

    void handleServerInfoRequest(ServerInfoRequestPacket2S packet);

    void handleLoginRequest(LoginRequestPacket2S packet);

    void handleConnectionKey(EncodeKeyPacket2S packet);

    void handleSessionID(SessionIDPacket2S packet);

}
