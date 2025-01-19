package generaloss.mc24.client.network;

import generaloss.mc24.client.Main;
import generaloss.mc24.client.level.LevelChunk;
import generaloss.mc24.client.level.WorldLevel;
import generaloss.mc24.server.block.BlockState;
import generaloss.mc24.server.network.packet2c.AbilitiesPacket2C;
import generaloss.mc24.server.network.packet2c.ChunkPacket2C;
import generaloss.mc24.server.network.packet2c.EntityMovePacket2C;
import generaloss.mc24.server.network.packet2c.SetBlockStatePacket2C;
import generaloss.mc24.server.network.protocol.IClientProtocolGame;
import generaloss.mc24.server.registry.ServerRegistries;
import jpize.app.Jpize;
import jpize.util.net.tcp.TCPConnection;

public class ClientProtocolGame extends ClientProtocol implements IClientProtocolGame {

    public ClientProtocolGame(Main context, TCPConnection tcpConnection) {
        super(context, tcpConnection);
    }

    @Override
    public void handleChunk(ChunkPacket2C packet) {
        final WorldLevel level = super.context().level();
        final LevelChunk chunk = new LevelChunk(level, packet);
        level.putChunk(chunk);
    }

    @Override
    public void handleSetBlockState(SetBlockStatePacket2C packet) {
        final BlockState blockstate = ServerRegistries.BLOCK_STATE.get(packet.getBlockStateID());
        final LevelChunk chunk = super.context().level().getChunk(packet.getChunkPositionPacked());
        chunk.setBlockState(packet.getLocalX(), packet.getLocalY(), packet.getLocalZ(), blockstate);
        chunk.world().tesselators().tesselate(chunk);
    }

    @Override
    public void handleAbilitiesPacket(AbilitiesPacket2C packet) {
        Jpize.syncExecutor().exec(() -> super.context().screens().show("session"));
    }

    @Override
    public void handleEntityMove(EntityMovePacket2C packet) {
        //! entity moves on client
    }

}
