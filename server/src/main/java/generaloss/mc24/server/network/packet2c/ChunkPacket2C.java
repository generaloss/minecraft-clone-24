package generaloss.mc24.server.network.packet2c;

import generaloss.mc24.server.chunk.*;
import generaloss.mc24.server.network.protocol.IClientProtocolGame;
import jpize.util.io.ExtDataInputStream;
import jpize.util.io.ExtDataOutputStream;
import jpize.util.net.tcp.packet.IPacket;

import java.io.IOException;

public class ChunkPacket2C extends IPacket<IClientProtocolGame> {

    private long positionPacked;
    private byte[] blockStateIndices;
    private byte[] blockLight;

    public ChunkPacket2C(Chunk<?> chunk) {
        this.positionPacked = chunk.position().pack();
        this.blockStateIndices = chunk.getBlockStateIndices().array();
        this.blockLight = chunk.getBlockLight().array();
    }

    public ChunkPacket2C() { }

    public ChunkPos getPosition() {
        return new ChunkPos(positionPacked);
    }

    public ByteNibbleArray getBlockStateIndices() {
        return new ByteNibbleArray(blockStateIndices);
    }

    public ByteMultiNibbleArray getBlockLight() {
        return new ByteMultiNibbleArray(3, blockLight);
    }

    @Override
    public void write(ExtDataOutputStream stream) throws IOException {
        stream.writeLong(positionPacked);
        stream.writeByteArray(blockStateIndices);
        stream.writeByteArray(blockLight);
        if(blockStateIndices.length != 16 * 16 * 16)
            System.err.println("bsi: " + blockStateIndices.length);
        if(blockLight.length != 16 * 16 * 16 * 3)
            System.err.println("bl: " + blockLight.length);
    }

    @Override
    public void read(ExtDataInputStream stream) throws IOException {
        positionPacked = stream.readLong();
        blockStateIndices = stream.readByteArray();
        blockLight = stream.readByteArray();
        if(blockStateIndices.length != 16 * 16 * 16)
            System.err.println("bsi: " + blockStateIndices.length);
        if(blockLight.length != 16 * 16 * 16 * 3)
            System.err.println("bl: " + blockLight.length);
    }

    @Override
    public void handle(IClientProtocolGame handler) {
        handler.handleChunk(this);
    }

}
