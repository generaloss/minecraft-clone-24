package generaloss.mc24.server.network.connection;

import generaloss.mc24.server.Server;
import generaloss.mc24.server.network.NetSession;
import generaloss.mc24.server.network.protocol.IServerProtocolGame;
import jpize.util.net.tcp.TcpConnection;

public class ServerConnectionGame extends ServerConnection implements IServerProtocolGame {

    private final NetSession session;

    public ServerConnectionGame(Server server, TcpConnection tcpConnection, NetSession session) {
        super(server, tcpConnection);
        this.session = session;
        System.out.println("[INFO]: Created net session '" + session.getNickname() + "'");
    }

}
