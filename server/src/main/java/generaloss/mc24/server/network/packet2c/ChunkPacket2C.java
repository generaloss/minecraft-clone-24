package generaloss.mc24.server.network.packet2c;

import generaloss.mc24.server.chunk.*;
import generaloss.mc24.server.network.protocol.IClientProtocolGame;
import jpize.util.io.ExtDataInputStream;
import jpize.util.io.ExtDataOutputStream;
import jpize.util.net.packet.NetPacket;

import java.io.IOException;

public class ChunkPacket2C extends NetPacket<IClientProtocolGame> {

    private ChunkPos position;
    private ChunkStorage storage;

    public ChunkPacket2C(Chunk chunk) {
        this.position = chunk.position();
        this.storage = chunk.storage();
    }

    public ChunkPacket2C() { }

    public ChunkPos getPosition() {
        return position;
    }

    public ChunkStorage getStorage() {
        return storage;
    }

    @Override
    public void write(ExtDataOutputStream stream) throws IOException {
        // position
        stream.writeInts(position.getX(), position.getY(), position.getZ());
        // storage
        stream.writeByteArray(storage.blockstates().array());
        stream.writeByteArray(storage.blocklight().array());

        final boolean hasSkylight = storage.hasSkylight();
        stream.writeBoolean(hasSkylight);
        if(hasSkylight)
            stream.writeByteArray(storage.skylight().array());
    }

    @Override
    public void read(ExtDataInputStream stream) throws IOException {
        // position
        position = new ChunkPos(stream.readInt(), stream.readInt(), stream.readInt());
        // storage
        final ChunkByteArray blockstates = new ChunkByteArray(stream.readByteArray());
        final ChunkMultiByteArray blocklight = new ChunkMultiByteArray(3, stream.readByteArray());

        final boolean hasSkylight = stream.readBoolean();
        final ChunkByteArray skylight = (hasSkylight ? new ChunkByteArray(stream.readByteArray()) : null);

        storage = new ChunkStorage(blockstates, blocklight, skylight);
    }

    @Override
    public void handle(IClientProtocolGame handler) {
        handler.handleChunk(this);
    }

}
