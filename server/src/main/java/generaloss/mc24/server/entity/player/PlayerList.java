package generaloss.mc24.server.entity.player;

import generaloss.mc24.server.Server;
import generaloss.mc24.server.network.AccountSession;
import generaloss.mc24.server.network.connection.ServerConnectionGame;
import generaloss.mc24.server.network.packet2c.AbilitiesPacket2C;
import generaloss.mc24.server.network.packet2c.InsertEntityPacket2C;
import generaloss.mc24.server.network.packet2c.MoveEntityPacket2C;
import generaloss.mc24.server.network.packet2c.RemoveEntityPacket2C;
import generaloss.mc24.server.network.protocol.IClientProtocolGame;
import jpize.util.math.vector.Vec3i;
import jpize.util.net.tcp.packet.NetPacket;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class PlayerList {

    private final Server server;
    private final List<ServerPlayer> players;

    public PlayerList(Server server) {
        this.server = server;
        this.players = new CopyOnWriteArrayList<>();
    }

    public Collection<ServerPlayer> getPlayers() {
        return players;
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

        for(ServerPlayer anotherPlayer : players){
            connection.sendPacket(new InsertEntityPacket2C(anotherPlayer));
            anotherPlayer.getConnection().sendPacket(new InsertEntityPacket2C(player));
        }

        players.add(player);
        System.out.println("[INFO]: '" + player.session().getUsername() + "' joined the game.");
        return player;
    }

    public void removePlayer(ServerPlayer player) {
        System.out.println("[INFO]: '" + player.session().getUsername() + "' leave the game.");
        players.remove(player);

        for(ServerPlayer otherPlayer : players)
            otherPlayer.getConnection().sendPacket(new RemoveEntityPacket2C(player.getUUID()));
    }


    public void broadcastPacket(NetPacket<IClientProtocolGame> packet) {
        for(ServerPlayer player: players)
            player.getConnection().sendPacket(packet);
    }

    public void broadcastPacket(ServerPlayer exclude, NetPacket<?> packet) {
        for(ServerPlayer player: players)
            if(player != exclude)
                player.getConnection().sendPacket(packet);
    }

}
