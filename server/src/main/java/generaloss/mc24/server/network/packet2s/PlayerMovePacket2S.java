package generaloss.mc24.server.network.packet2s;

import generaloss.mc24.server.network.protocol.IServerProtocolGame;
import jpize.util.io.ExtDataInputStream;
import jpize.util.io.ExtDataOutputStream;
import jpize.util.math.vector.Vec3f;
import jpize.util.net.packet.NetPacket;

import java.io.IOException;

public class PlayerMovePacket2S extends NetPacket<IServerProtocolGame> {

    private final Vec3f position;

    public PlayerMovePacket2S() {
        this.position = new Vec3f();
    }

    public PlayerMovePacket2S(Vec3f position) {
        this();
        this.position.set(position);
    }

    public Vec3f getPosition() {
        return position;
    }

    @Override
    public void write(ExtDataOutputStream stream) throws IOException {
        stream.writeVec3f(position);
    }

    @Override
    public void read(ExtDataInputStream stream) throws IOException {
        stream.readVec3f(position);
    }

    @Override
    public void handle(IServerProtocolGame handler) {
        handler.handlePlayerMove(this);
    }

}