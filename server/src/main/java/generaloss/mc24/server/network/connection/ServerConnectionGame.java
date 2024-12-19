package generaloss.mc24.server.network.connection;

import generaloss.mc24.server.Server;
import generaloss.mc24.server.network.AccountSession;
import generaloss.mc24.server.network.protocol.IServerProtocolGame;
import jpize.util.net.tcp.TCPConnection;

public class ServerConnectionGame extends ServerConnection implements IServerProtocolGame {

    private final AccountSession session;

    public ServerConnectionGame(Server server, TCPConnection tcpConnection, AccountSession session) {
        super(server, tcpConnection);
        this.session = session;
        System.out.println("[INFO]: '" + session.getNickname() + "' joined the game.");
    }

    public AccountSession session() {
        return session;
    }

}
