package generaloss.mc24.server.network.packet2c;

import generaloss.mc24.server.network.protocol.IClientProtocolGame;
import jpize.util.io.ExtDataInputStream;
import jpize.util.io.ExtDataOutputStream;
import jpize.util.math.vector.Vec3f;
import jpize.util.net.tcp.packet.NetPacket;

import java.io.IOException;
import java.util.UUID;

public class MoveEntityPacket2C extends NetPacket<IClientProtocolGame> {

    private UUID entityUUID;
    private Vec3f position;

    public MoveEntityPacket2C(UUID entityUUID, Vec3f position) {
        this.entityUUID = entityUUID;
        this.position = position;
    }

    public MoveEntityPacket2C() { }

    public UUID getEntityUUID() {
        return entityUUID;
    }

    public Vec3f getPosition() {
        return position;
    }

    @Override
    public void write(ExtDataOutputStream stream) throws IOException {
        stream.writeUUID(entityUUID);
        stream.writeVec3f(position);
    }

    @Override
    public void read(ExtDataInputStream stream) throws IOException {
        entityUUID = stream.readUUID();
        position = stream.readVec3f();
    }

    @Override
    public void handle(IClientProtocolGame handler) {
        handler.handleMoveEntity(this);
    }

}