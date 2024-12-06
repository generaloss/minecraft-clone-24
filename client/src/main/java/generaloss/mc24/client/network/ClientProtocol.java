package generaloss.mc24.client.network;

import generaloss.mc24.client.Main;
import generaloss.mc24.server.network.protocol.ClientPacketHandler;

public abstract class ClientProtocol implements ClientPacketHandler {

    protected Main context;

    public ClientProtocol(Main context) {
        this.context = context;
    }

}
