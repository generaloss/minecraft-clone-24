package generaloss.mc24.client.network;

import generaloss.mc24.client.Main;
import generaloss.mc24.server.network.protocol.ClientPacketHandlerLogin;

public class ClientConnectionLogin extends ClientConnection implements ClientPacketHandlerLogin {

    public ClientConnectionLogin(Main context) {
        super(context);
    }

}
