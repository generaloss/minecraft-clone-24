package generaloss.mc24.server.network.packet2s;

import generaloss.mc24.server.chunk.ChunkPos;
import generaloss.mc24.server.network.protocol.IServerProtocolGame;
import jpize.util.io.ExtDataInputStream;
import jpize.util.io.ExtDataOutputStream;
import jpize.util.net.packet.NetPacket;

import java.io.IOException;

public class SetBlockStatePacket2S extends NetPacket<IServerProtocolGame> {

    private ChunkPos chunkPosition;
    private int localX, localY, localZ;
    private int blockstateID;

    public SetBlockStatePacket2S(ChunkPos chunkPosition, int localX, int localY, int localZ, int blockstateID) {
        this.chunkPosition = chunkPosition;
        this.localX = localX;
        this.localY = localY;
        this.localZ = localZ;
        this.blockstateID = blockstateID;
    }

    public SetBlockStatePacket2S() { }


    public ChunkPos getChunkPosition() {
        return chunkPosition;
    }

    public int getLocalX() {
        return localX;
    }

    public int getLocalY() {
        return localY;
    }

    public int getLocalZ() {
        return localZ;
    }

    public int getBlockStateID() {
        return blockstateID;
    }


    @Override
    public void write(ExtDataOutputStream stream) throws IOException {
        stream.writeInts(chunkPosition.getX(), chunkPosition.getY(), chunkPosition.getZ());
        stream.writeInts(localX, localY, localZ, blockstateID);
    }

    @Override
    public void read(ExtDataInputStream stream) throws IOException {
        chunkPosition = new ChunkPos(stream.readInt(), stream.readInt(), stream.readInt());
        localX = stream.readInt();
        localY = stream.readInt();
        localZ = stream.readInt();
        blockstateID = stream.readInt();
    }

    @Override
    public void handle(IServerProtocolGame handler) {
        handler.handleSetBlockState(this);
    }

}