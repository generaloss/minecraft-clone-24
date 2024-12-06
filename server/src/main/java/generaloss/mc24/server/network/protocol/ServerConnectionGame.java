package generaloss.mc24.server.network.protocol;

import generaloss.mc24.server.Server;
import jpize.util.net.tcp.TcpConnection;

public class ServerConnectionGame extends ServerConnection {

    public ServerConnectionGame(Server server, TcpConnection connection) {
        super(server, connection);
    }

}
