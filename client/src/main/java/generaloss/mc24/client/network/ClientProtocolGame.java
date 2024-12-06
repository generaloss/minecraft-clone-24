package generaloss.mc24.client.network;

import generaloss.mc24.client.Main;
import generaloss.mc24.server.network.protocol.ClientPacketHandlerGame;

public class ClientProtocolGame extends ClientProtocol implements ClientPacketHandlerGame {

    public ClientProtocolGame(Main context) {
        super(context);
    }

}
