package generaloss.mc24.server.network.connection;

import generaloss.mc24.server.Server;
import generaloss.mc24.server.block.BlockState;
import generaloss.mc24.server.network.AccountSession;
import generaloss.mc24.server.network.packet2c.ChunkPacket2C;
import generaloss.mc24.server.network.packet2c.MoveEntityPacket2C;
import generaloss.mc24.server.network.packet2c.SetBlockStatePacket2C;
import generaloss.mc24.server.network.packet2s.PlayerMovePacket2S;
import generaloss.mc24.server.network.packet2s.SetBlockStatePacket2S;
import generaloss.mc24.server.network.protocol.IServerProtocolGame;
import generaloss.mc24.server.entity.player.ServerPlayer;
import generaloss.mc24.server.registry.ServerRegistries;
import generaloss.mc24.server.world.ServerWorld;
import jpize.util.net.tcp.TCPConnection;

public class ServerConnectionGame extends ServerConnection implements IServerProtocolGame {

    private final ServerPlayer player;

    public ServerConnectionGame(Server server, TCPConnection tcpConnection, AccountSession session) {
        super(server, tcpConnection);
        this.player = server.players().createPlayer(session, this);
    }

    public ServerPlayer player() {
        return player;
    }

    public void sendAllChunks() {
        super.server().worldHolder().getWorld("overworld").forEachChunk(chunk ->
            super.sendPacket(new ChunkPacket2C(chunk))
        );
    }


    @Override
    public void handleSetBlockState(SetBlockStatePacket2S packet) {
        final Server server = super.server();
        final ServerWorld world = server.worldHolder().getWorld("overworld");
        final BlockState blockstate = ServerRegistries.BLOCK_STATE.get(packet.getBlockStateID());
        if(blockstate == null)
            return;
        world.getChunk(packet.getChunkPosition()).setBlockState(
            packet.getLocalX(), packet.getLocalY(), packet.getLocalZ(), blockstate
        );
        // add an stack
        super.server().net().tcpServer().broadcast(super.tcpConnection(), new SetBlockStatePacket2C(
            packet.getChunkPosition(),
            packet.getLocalX(), packet.getLocalY(), packet.getLocalZ(),
            packet.getBlockStateID()
        ));
    }

    @Override
    public void handlePlayerMove(PlayerMovePacket2S packet) {
        player.position().set(packet.getPosition());
        super.server().players().broadcastPacket(player,
                new MoveEntityPacket2C(player.getUUID(), player.position()));
    }

}
