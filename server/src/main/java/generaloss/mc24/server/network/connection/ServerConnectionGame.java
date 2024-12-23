package generaloss.mc24.server.network.connection;

import generaloss.mc24.server.Server;
import generaloss.mc24.server.chunk.Chunk;
import generaloss.mc24.server.network.AccountSession;
import generaloss.mc24.server.network.packet2c.ChunkPacket2C;
import generaloss.mc24.server.network.protocol.IServerProtocolGame;
import generaloss.mc24.server.world.ServerWorld;
import jpize.util.net.tcp.TCPConnection;

import java.util.Collection;

public class ServerConnectionGame extends ServerConnection implements IServerProtocolGame {

    private final AccountSession session;

    public ServerConnectionGame(Server server, TCPConnection tcpConnection, AccountSession session) {
        super(server, tcpConnection);
        this.session = session;
        System.out.println("[INFO]: '" + session.getNickname() + "' joined the game.");
    }

    public void sendAllChunks() {
        final Collection<Chunk<ServerWorld>> chunks = super.server()
                .worldHolder().getWorld("overworld").getChunks();

        int i = 0;
        for(Chunk<ServerWorld> chunk : chunks){
            super.sendPacket(new ChunkPacket2C(chunk));
            i++;
            System.out.println(i + " chunk sent");
        }
    }

    public AccountSession session() {
        return session;
    }

}
