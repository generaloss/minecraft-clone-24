package generaloss.mc24.server.network.protocol;

import generaloss.mc24.server.network.packet2c.Packet2CPublicKey;
import generaloss.mc24.server.network.packet2c.Packet2CServerInfoResponse;
import jpize.util.net.tcp.packet.PacketHandler;

public interface IClientProtocolLogin extends PacketHandler {

    void handleServerInfoResponse(Packet2CServerInfoResponse packet);

    void handlePublicKey(Packet2CPublicKey packet);

}
