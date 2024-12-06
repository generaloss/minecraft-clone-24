package generaloss.mc24.server.network.protocol;

import generaloss.mc24.server.Server;
import jpize.util.net.tcp.TcpConnection;

public class ServerConnectionLogin extends ServerConnection {

    public ServerConnectionLogin(Server server, TcpConnection connection) {
        super(server, connection);
    }

}
