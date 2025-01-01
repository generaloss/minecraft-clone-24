package generaloss.mc24.server.network.packet2c;

import generaloss.mc24.server.chunk.*;
import generaloss.mc24.server.network.protocol.IClientProtocolGame;
import jpize.util.io.ExtDataInputStream;
import jpize.util.io.ExtDataOutputStream;
import jpize.util.net.tcp.packet.IPacket;

import java.io.IOException;

public class ChunkPacket2C extends IPacket<IClientProtocolGame> {

    private long positionPacked;
    private byte[] blockstateIndices;
    private byte[] blockLight;

    public ChunkPacket2C(Chunk<?> chunk) {
        this.positionPacked = chunk.position().pack();
        this.blockstateIndices = chunk.getBlockStateIndices().array();
        this.blockLight = chunk.getBlockLight().array();
    }

    public ChunkPacket2C() { }

    public ChunkPos getPosition() {
        return new ChunkPos(positionPacked);
    }

    public ByteNibbleArray getBlockstateIndices() {
        return new ByteNibbleArray(blockstateIndices);
    }

    public ByteMultiNibbleArray getBlockLight() {
        return new ByteMultiNibbleArray(3, blockLight);
    }

    @Override
    public void write(ExtDataOutputStream stream) throws IOException {
        stream.writeLong(positionPacked);
        stream.writeByteArray(blockstateIndices);
        stream.writeByteArray(blockLight);
        if(blockstateIndices.length != Chunk.SIZE * Chunk.SIZE * Chunk.SIZE)
            System.err.println("bsi: " + blockstateIndices.length);
        if(blockLight.length != Chunk.SIZE * Chunk.SIZE * Chunk.SIZE * 3)
            System.err.println("bl: " + blockLight.length);
    }

    @Override
    public void read(ExtDataInputStream stream) throws IOException {
        positionPacked = stream.readLong();
        blockstateIndices = stream.readByteArray();
        blockLight = stream.readByteArray();
        if(blockstateIndices.length != Chunk.SIZE * Chunk.SIZE * Chunk.SIZE)
            System.err.println("bsi: " + blockstateIndices.length);
        if(blockLight.length != Chunk.SIZE * Chunk.SIZE * Chunk.SIZE * 3)
            System.err.println("bl: " + blockLight.length);
    }

    @Override
    public void handle(IClientProtocolGame handler) {
        handler.handleChunk(this);
    }

}
