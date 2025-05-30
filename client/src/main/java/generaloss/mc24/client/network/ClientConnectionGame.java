package generaloss.mc24.client.network;

import generaloss.mc24.client.Main;
import generaloss.mc24.client.level.LevelChunk;
import generaloss.mc24.client.level.WorldLevel;
import generaloss.mc24.client.screen.ScreenGameSession;
import generaloss.mc24.server.block.BlockState;
import generaloss.mc24.server.chunk.ChunkPos;
import generaloss.mc24.server.entity.AbstractEntity;
import generaloss.mc24.server.network.packet2c.*;
import generaloss.mc24.server.network.protocol.IClientProtocolGame;
import generaloss.mc24.server.registry.ServerRegistries;
import jpize.context.Jpize;
import jpize.util.net.tcp.TCPConnection;
import jpize.util.screen.ScreenManager;

public class ClientConnectionGame extends ClientConnection implements IClientProtocolGame {

    public ClientConnectionGame(Main context, TCPConnection tcpConnection) {
        super(context, tcpConnection);
    }

    @Override
    public void handleChunkPacket(ChunkPacket2C packet) {
        final ChunkPos position = packet.getPosition();
        final WorldLevel level = super.context().level();
        level.createChunk(packet.getPosition(), packet.getStorage());
    }

    @Override
    public void handleSetBlockStatePacket(SetBlockStatePacket2C packet) {
        final BlockState blockstate = ServerRegistries.BLOCK_STATE.get(packet.getBlockStateID());
        final LevelChunk chunk = super.context().level().getChunk(packet.getChunkPosition());
        chunk.setBlockState(packet.getLocalX(), packet.getLocalY(), packet.getLocalZ(), blockstate);
        chunk.world().tesselators().tesselate(chunk);
    }

    @Override
    public void handleInitPlayerPacket(InitPlayerPacket2C packet) {
        packet.applyTo(super.context().player());
        // set session screen
        final ScreenManager<String> screens = super.context().screens();
        Jpize.syncExecutor().exec(() ->
            screens.setCurrent(ScreenGameSession.SCREEN_ID)
        );
    }

    @Override
    public void handleInsertEntityPacket(InsertEntityPacket2C packet) {
        System.out.println("[INFO]: Entity " + packet.getEntity().getUUID() + " spawn");
        super.context().entities().insert(packet.getEntity());
    }

    @Override
    public void handleRemoveEntityPacket(RemoveEntityPacket2C packet) {
        System.out.println("[INFO]: Entity " + packet.getEntityUUID() + " despawn");
        super.context().entities().remove(packet.getEntityUUID());
    }

    @Override
    public void handleMoveEntityPacket(MoveEntityPacket2C packet) {
        final AbstractEntity entity = super.context().entities().get(packet.getEntityUUID());
        entity.position().set(packet.getPosition());
    }
}
