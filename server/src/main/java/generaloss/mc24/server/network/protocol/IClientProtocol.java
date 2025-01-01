package generaloss.mc24.server.network.protocol;

import generaloss.mc24.server.network.packet2c.DisconnectPacket2C;
import jpize.util.net.tcp.packet.INetPacketHandler;

public interface IClientProtocol extends INetPacketHandler {

    void handleDisconnect(DisconnectPacket2C packet);

}
