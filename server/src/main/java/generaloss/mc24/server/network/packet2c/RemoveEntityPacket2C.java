package generaloss.mc24.server.network.packet2c;

import generaloss.mc24.server.network.protocol.IClientProtocolGame;
import jpize.util.io.ExtDataInputStream;
import jpize.util.io.ExtDataOutputStream;
import jpize.util.net.packet.NetPacket;

import java.io.IOException;
import java.util.UUID;

public class RemoveEntityPacket2C extends NetPacket<IClientProtocolGame> {

    private UUID uuid;

    public RemoveEntityPacket2C(UUID uuid) {
        this.uuid = uuid;
    }

    public RemoveEntityPacket2C() { }

    public UUID getEntityUUID() {
        return uuid;
    }

    @Override
    public void write(ExtDataOutputStream stream) throws IOException {
        stream.writeUUID(uuid);
    }

    @Override
    public void read(ExtDataInputStream stream) throws IOException {
        uuid = stream.readUUID();
    }

    @Override
    public void handle(IClientProtocolGame handler) {
        handler.handleRemoveEntity(this);
    }

}
