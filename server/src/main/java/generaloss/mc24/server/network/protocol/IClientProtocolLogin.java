package generaloss.mc24.server.network.protocol;

import generaloss.mc24.server.network.packet2c.LoginStatePacket2C;
import generaloss.mc24.server.network.packet2c.PublicKeyPacket2C;
import generaloss.mc24.server.network.packet2c.ServerInfoResponsePacket2C;
import generaloss.mc24.server.network.packet2c.InitSessionPacket2C;

public interface IClientProtocolLogin {

    void handleServerInfoResponse(ServerInfoResponsePacket2C packet);

    void handlePublicKey(PublicKeyPacket2C packet);

    void handleLoginState(LoginStatePacket2C packet);

    void handleInitSession(InitSessionPacket2C packet);

}
