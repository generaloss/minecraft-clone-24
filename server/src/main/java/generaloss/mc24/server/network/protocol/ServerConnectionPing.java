package generaloss.mc24.server.network.protocol;

import generaloss.mc24.server.Server;
import generaloss.mc24.server.network.packet2s.Packet2SPingRequest;

public class ServerConnectionPing extends ServerConnection {

    public ServerConnectionPing(Server server) {
        super(server);
    }

    public void onPingRequest(Packet2SPingRequest packet) {

    }

}
