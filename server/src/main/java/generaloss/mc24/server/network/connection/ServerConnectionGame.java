package generaloss.mc24.server.network.connection;

import generaloss.mc24.server.Server;
import generaloss.mc24.server.block.BlockState;
import generaloss.mc24.server.network.AccountSession;
import generaloss.mc24.server.network.packet2c.MoveEntityPacket2C;
import generaloss.mc24.server.network.packet2c.SetBlockStatePacket2C;
import generaloss.mc24.server.network.packet2s.InitSessionPacket2S;
import generaloss.mc24.server.network.packet2s.PlayerMovePacket2S;
import generaloss.mc24.server.network.packet2s.SetBlockStatePacket2S;
import generaloss.mc24.server.network.protocol.IServerProtocolGame;
import generaloss.mc24.server.player.ServerPlayer;
import generaloss.mc24.server.registry.ServerRegistries;
import generaloss.mc24.server.world.ServerWorld;
import jpize.util.net.tcp.TCPConnection;

public class ServerConnectionGame extends ServerConnection implements IServerProtocolGame {

    private final AccountSession session;
    private ServerPlayer player;

    public ServerConnectionGame(Server server, TCPConnection tcpConnection, AccountSession session) {
        super(server, tcpConnection);
        this.session = session;
    }

    public ServerPlayer player() {
        return player;
    }

    @Override
    public void handleInitSessionPacket(InitSessionPacket2S packet) {
        this.player = super.server().players().createPlayer(session, this);
    }

    @Override
    public void handleSetBlockStatePacket(SetBlockStatePacket2S packet) {
        final Server server = super.server();
        final ServerWorld world = server.worldHolder().getWorld("overworld");
        final BlockState blockstate = ServerRegistries.BLOCK_STATE.get(packet.getBlockStateID());
        if(blockstate == null)
            return;
        world.getChunk(packet.getChunkPosition()).setBlockState(
            packet.getLocalX(), packet.getLocalY(), packet.getLocalZ(), blockstate
        );
        // add an stack
        super.server().network().tcpServer().broadcast(super.tcpConnection(), new SetBlockStatePacket2C(
            packet.getChunkPosition(),
            packet.getLocalX(), packet.getLocalY(), packet.getLocalZ(),
            packet.getBlockStateID()
        ));
    }

    @Override
    public void handlePlayerMovePacket(PlayerMovePacket2S packet) {
        player.position().set(packet.getPosition());
        super.server().players().broadcastPacket(player,
                new MoveEntityPacket2C(player.getUUID(), player.position()));
    }

}
