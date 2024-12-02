package generaloss.mc24.server.network.protocol;

import generaloss.mc24.server.network.packet2c.Packet2CPingResponse;
import jpize.util.net.tcp.packet.PacketHandler;

public interface ClientPacketHandlerPing extends PacketHandler {

    void onPingResponse(Packet2CPingResponse packet);

}
