package generaloss.mc24.client.network;

import generaloss.mc24.client.Main;
import generaloss.mc24.client.level.LevelChunk;
import generaloss.mc24.client.level.WorldLevel;
import generaloss.mc24.server.block.BlockState;
import generaloss.mc24.server.network.packet2c.ChunkPacket2C;
import generaloss.mc24.server.network.packet2c.SetBlockStatePacket2C;
import generaloss.mc24.server.network.protocol.IClientProtocolGame;
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
        level.tesselator().tesselate(chunk);
        System.out.println(level.getChunks().size() + " created chunk: " + chunk.position());
    }

    @Override
    public void handleSetBlockState(SetBlockStatePacket2C packet) {
        final BlockState blockState = super.context().registries().BLOCK_STATES.get(packet.getBlockStateID());
        super.context().level().getChunk(packet.getChunkPositionPacked()).setBlockState(
            packet.getLocalX(), packet.getLocalY(), packet.getLocalZ(), blockState
        );
    }

}
