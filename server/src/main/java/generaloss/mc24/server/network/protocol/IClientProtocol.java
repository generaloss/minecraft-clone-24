package generaloss.mc24.server.network.protocol;

import generaloss.mc24.server.network.packet2c.DisconnectPacket2C;

public interface IClientProtocol {

    void handleDisconnectPacket(DisconnectPacket2C packet);

}
