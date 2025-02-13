package generaloss.mc24.client.network;

import generaloss.mc24.client.Main;
import generaloss.mc24.client.level.LevelChunk;
import generaloss.mc24.client.level.WorldLevel;
import generaloss.mc24.server.block.BlockState;
import generaloss.mc24.server.entity.AbstractEntity;
import generaloss.mc24.server.network.packet2c.*;
import generaloss.mc24.server.network.protocol.IClientProtocolGame;
import generaloss.mc24.server.registry.ServerRegistries;
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
        final LevelChunk chunk = super.context().level().getChunk(packet.getChunkPosition());
        chunk.setBlockState(packet.getLocalX(), packet.getLocalY(), packet.getLocalZ(), blockstate);
        chunk.world().tesselators().tesselate(chunk);
    }

    @Override
    public void handleInitPlayer(InitPlayerPacket2C packet) {
        packet.applyTo(super.context().player());
    }

    @Override
    public void handleInsertEntity(InsertEntityPacket2C packet) {
        System.out.println("[INFO]: Entity " + packet.getEntity().getUUID() + " spawn");
        super.context().entities().insert(packet.getEntity());
    }

    @Override
    public void handleRemoveEntity(RemoveEntityPacket2C packet) {
        System.out.println("[INFO]: Entity " + packet.getEntityUUID() + " despawn");
        super.context().entities().remove(packet.getEntityUUID());
    }

    @Override
    public void handleMoveEntity(MoveEntityPacket2C packet) {
        final AbstractEntity entity = super.context().entities().get(packet.getEntityUUID());
        entity.position().set(packet.getPosition());
    }
}
