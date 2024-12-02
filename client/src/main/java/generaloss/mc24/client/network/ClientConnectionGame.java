package generaloss.mc24.client.network;

import generaloss.mc24.client.Main;
import generaloss.mc24.server.network.protocol.ClientPacketHandlerGame;

public class ClientConnectionGame extends ClientConnection implements ClientPacketHandlerGame {

    public ClientConnectionGame(Main context) {
        super(context);
    }

}
