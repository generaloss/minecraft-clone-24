package generaloss.mc24.server.network.protocol;

import generaloss.mc24.server.network.packet2s.Packet2SConnectionKey;
import generaloss.mc24.server.network.packet2s.Packet2SLoginRequest;
import generaloss.mc24.server.network.packet2s.Packet2SServerInfoRequest;
import generaloss.mc24.server.network.packet2s.Packet2SSessionID;
import jpize.util.net.tcp.packet.PacketHandler;

public interface IServerProtocolLogin extends PacketHandler {

    void handleServerInfoRequest(Packet2SServerInfoRequest packet);

    void handleLoginRequest(Packet2SLoginRequest packet);

    void handleConnectionKey(Packet2SConnectionKey packet);

    void handleSessionID(Packet2SSessionID packet);

}
