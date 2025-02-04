package generaloss.mc24.server.network.packet2c;

import generaloss.mc24.server.chunk.*;
import generaloss.mc24.server.network.protocol.IClientProtocolGame;
import jpize.util.io.ExtDataInputStream;
import jpize.util.io.ExtDataOutputStream;
import jpize.util.net.packet.NetPacket;

import java.io.IOException;

public class ChunkPacket2C extends NetPacket<IClientProtocolGame> {

    private long positionPacked;
    private byte[] blockstateIDs;
    private byte[] blockLight;

    public ChunkPacket2C(Chunk<?> chunk) {
        this.positionPacked = chunk.position().pack();
        this.blockstateIDs = chunk.getBlockStateIDs().array();
        this.blockLight = chunk.getBlockLight().array();
    }

    public ChunkPacket2C() { }

    public ChunkPos getPosition() {
        return new ChunkPos(positionPacked);
    }

    public ByteNibbleArray getBlockstateIDs() {
        return new ByteNibbleArray(blockstateIDs);
    }

    public ByteMultiNibbleArray getBlockLight() {
        return new ByteMultiNibbleArray(3, blockLight);
    }

    @Override
    public void write(ExtDataOutputStream stream) throws IOException {
        stream.writeLong(positionPacked);
        stream.writeByteArray(blockstateIDs);
        stream.writeByteArray(blockLight);
        if(blockstateIDs.length != Chunk.SIZE * Chunk.SIZE * Chunk.SIZE)
            System.err.println("bsi: " + blockstateIDs.length);
        if(blockLight.length != Chunk.SIZE * Chunk.SIZE * Chunk.SIZE * 3)
            System.err.println("bl: " + blockLight.length);
    }

    @Override
    public void read(ExtDataInputStream stream) throws IOException {
        positionPacked = stream.readLong();
        blockstateIDs = stream.readByteArray();
        blockLight = stream.readByteArray();
        if(blockstateIDs.length != Chunk.SIZE * Chunk.SIZE * Chunk.SIZE)
            System.err.println("bsi: " + blockstateIDs.length);
        if(blockLight.length != Chunk.SIZE * Chunk.SIZE * Chunk.SIZE * 3)
            System.err.println("bl: " + blockLight.length);
    }

    @Override
    public void handle(IClientProtocolGame handler) {
        handler.handleChunk(this);
    }

}
