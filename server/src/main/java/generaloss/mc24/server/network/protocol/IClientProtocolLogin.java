package generaloss.mc24.server.network.protocol;

import generaloss.mc24.server.network.packet2c.LoginStatePacket2C;
import generaloss.mc24.server.network.packet2c.PublicKeyPacket2C;
import generaloss.mc24.server.network.packet2c.ServerInfoResponsePacket2C;
import generaloss.mc24.server.network.packet2c.InitSessionPacket2C;

public interface IClientProtocolLogin {

    void handleServerInfoResponsePacket(ServerInfoResponsePacket2C packet);

    void handlePublicKeyPacket(PublicKeyPacket2C packet);

    void handleLoginStatePacket(LoginStatePacket2C packet);

    void handleInitSessionPacket(InitSessionPacket2C packet);

}
