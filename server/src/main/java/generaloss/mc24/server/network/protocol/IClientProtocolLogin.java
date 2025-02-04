package generaloss.mc24.server.network.protocol;

import generaloss.mc24.server.network.packet2c.PublicKeyPacket2C;
import generaloss.mc24.server.network.packet2c.ServerInfoResponsePacket2C;

public interface IClientProtocolLogin {

    void handleServerInfoResponse(ServerInfoResponsePacket2C packet);

    void handlePublicKey(PublicKeyPacket2C packet);

}
