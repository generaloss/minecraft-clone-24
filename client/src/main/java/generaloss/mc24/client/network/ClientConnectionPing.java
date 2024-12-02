package generaloss.mc24.client.network;

import generaloss.mc24.client.Main;
import generaloss.mc24.server.network.packet2c.Packet2CPingResponse;
import generaloss.mc24.server.network.protocol.ClientPacketHandlerPing;

public class ClientConnectionPing extends ClientConnection implements ClientPacketHandlerPing {

    public ClientConnectionPing(Main context) {
        super(context);
    }

    @Override
    public void onPingResponse(Packet2CPingResponse packet) {

    }

}
