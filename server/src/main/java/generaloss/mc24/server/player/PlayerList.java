package generaloss.mc24.server.player;

import generaloss.mc24.server.Server;
import generaloss.mc24.server.network.AccountSession;
import generaloss.mc24.server.network.connection.ServerConnectionGame;
import generaloss.mc24.server.network.packet2c.AbilitiesPacket2C;
import jpize.util.math.vector.Vec3i;

public class PlayerList {

    private final Server server;

    public PlayerList(Server server) {
        this.server = server;
    }

    public ServerPlayer createPlayer(AccountSession session, ServerConnectionGame connection) {
        final ServerPlayer player = new ServerPlayer(session, connection);
        // set position
        final Vec3i spawnpoint = server.worldHolder().getWorld("overworld").getSpawnPoint().point();
        player.position().set(spawnpoint);
        // send chunks
        connection.sendAllChunks();
        // send abilities
        connection.sendPacket(new AbilitiesPacket2C());

        System.out.println("[INFO]: '" + player.session().getUsername() + "' joined the game.");
        return player;
    }

    public void removePlayer(ServerPlayer player) {
        System.out.println("[INFO]: '" + player.session().getUsername() + "' leave the game.");
    }

}
