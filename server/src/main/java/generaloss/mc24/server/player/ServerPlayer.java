package generaloss.mc24.server.player;

import generaloss.mc24.server.network.AccountSession;
import generaloss.mc24.server.network.connection.ServerConnectionGame;

import java.util.UUID;

public class ServerPlayer extends AbstractPlayer {

    private final AccountSession session;
    private final ServerConnectionGame connection;

    public ServerPlayer(AccountSession session, ServerConnectionGame connection) {
        super(UUID.randomUUID());
        this.session = session;
        this.connection = connection;
    }

    public AccountSession session() {
        return session;
    }

    public ServerConnectionGame getConnection() {
        return connection;
    }

}
