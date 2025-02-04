package generaloss.mc24.server.network.packet2c;

import generaloss.mc24.server.entity.AbstractEntity;
import generaloss.mc24.server.network.protocol.IClientProtocolGame;
import jpize.util.io.ExtDataInputStream;
import jpize.util.io.ExtDataOutputStream;
import jpize.util.math.vector.Vec3f;
import jpize.util.net.packet.NetPacket;

import java.io.IOException;
import java.util.UUID;

public class InsertEntityPacket2C extends NetPacket<IClientProtocolGame> {

    private AbstractEntity entity;

    public InsertEntityPacket2C(AbstractEntity entity) {
        this.entity = entity;
    }

    public InsertEntityPacket2C() { }

    public AbstractEntity getEntity() {
        return entity;
    }

    @Override
    public void write(ExtDataOutputStream stream) throws IOException {
        stream.writeUTF(entity.getTypeID());
        stream.writeUUID(entity.getUUID());
        stream.writeVec3f(entity.position());
        stream.writeUTF(entity.getDisplayName());
    }

    @Override
    public void read(ExtDataInputStream stream) throws IOException {
        final String entityTypeID = stream.readUTF();
        final UUID uuid = stream.readUUID();
        final Vec3f position = stream.readVec3f();
        final String displayName = stream.readUTF();

        entity = new AbstractEntity(uuid, entityTypeID);
        entity.position().set(position);
        entity.setDisplayName(displayName);
    }

    @Override
    public void handle(IClientProtocolGame handler) {
        handler.handleInsertEntity(this);
    }

}
