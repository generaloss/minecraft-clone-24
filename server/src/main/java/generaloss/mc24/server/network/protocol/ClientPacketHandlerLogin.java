package generaloss.mc24.server.network.protocol;

import generaloss.mc24.server.network.packet2c.Packet2CServerInfoResponse;
import jpize.util.net.tcp.packet.PacketHandler;

public interface ClientPacketHandlerLogin extends PacketHandler {

    void onServerInfoResponse(Packet2CServerInfoResponse packet);

}
