package generaloss.mc24.server.network.connection;

import generaloss.mc24.server.Server;
import generaloss.mc24.server.network.protocol.IServerProtocolGame;
import jpize.util.net.tcp.TcpConnection;

public class ServerConnectionGame extends ServerConnection implements IServerProtocolGame {

    public ServerConnectionGame(Server server, TcpConnection tcpConnection) {
        super(server, tcpConnection);
    }

}
