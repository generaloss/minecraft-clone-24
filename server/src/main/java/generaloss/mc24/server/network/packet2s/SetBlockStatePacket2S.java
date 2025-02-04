package generaloss.mc24.server.network.packet2s;

import generaloss.mc24.server.chunk.ChunkPos;
import generaloss.mc24.server.network.protocol.IServerProtocolGame;
import jpize.util.io.ExtDataInputStream;
import jpize.util.io.ExtDataOutputStream;
import jpize.util.net.packet.NetPacket;

import java.io.IOException;

public class SetBlockStatePacket2S extends NetPacket<IServerProtocolGame> {

    private long chunkPositionPacked;
    private int localX, localY, localZ;
    private int blockstateID;

    public SetBlockStatePacket2S(ChunkPos chunkPosition, int localX, int localY, int localZ, int blockstateID) {
        this.chunkPositionPacked = chunkPosition.pack();
        this.localX = localX;
        this.localY = localY;
        this.localZ = localZ;
        this.blockstateID = blockstateID;
    }

    public SetBlockStatePacket2S() { }


    public long getChunkPositionPacked() {
        return chunkPositionPacked;
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
        stream.writeLong(chunkPositionPacked);
        stream.writeInts(localX, localY, localZ, blockstateID);
    }

    @Override
    public void read(ExtDataInputStream stream) throws IOException {
        chunkPositionPacked = stream.readLong();
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