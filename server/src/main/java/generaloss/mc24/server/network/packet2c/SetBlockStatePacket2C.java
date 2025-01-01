package generaloss.mc24.server.network.packet2c;

import generaloss.mc24.server.network.protocol.IClientProtocolGame;
import jpize.util.io.ExtDataInputStream;
import jpize.util.io.ExtDataOutputStream;
import jpize.util.net.tcp.packet.IPacket;

import java.io.IOException;

public class SetBlockStatePacket2C extends IPacket<IClientProtocolGame> {

    private long chunkPositionPacked;
    private int localX, localY, localZ;
    private int blockstateID;

    public SetBlockStatePacket2C(long chunkPositionPacked, int localX, int localY, int localZ, int blockstateID) {
        this.chunkPositionPacked = chunkPositionPacked;
        this.localX = localX;
        this.localY = localY;
        this.localZ = localZ;
        this.blockstateID = blockstateID;
    }

    public SetBlockStatePacket2C() { }


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
    public void handle(IClientProtocolGame handler) {
        handler.handleSetBlockState(this);
    }

}