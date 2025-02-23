package generaloss.mc24.server.network.packet2c;

import generaloss.mc24.server.network.protocol.IClientProtocolGame;
import generaloss.mc24.server.player.AbstractPlayer;
import generaloss.mc24.server.player.ServerPlayer;
import jpize.util.io.ExtDataInputStream;
import jpize.util.io.ExtDataOutputStream;
import jpize.util.math.EulerAngles;
import jpize.util.math.vector.Vec3f;
import jpize.util.net.packet.NetPacket;

import java.io.IOException;

public class InitPlayerPacket2C extends NetPacket<IClientProtocolGame> {

    private Vec3f position;
    private float yaw, pitch;

    public InitPlayerPacket2C(ServerPlayer player) {
        this.position = player.position();
        final EulerAngles rotation = player.rotation();
        this.yaw = rotation.getYaw();
        this.pitch = rotation.getPitch();
    }

    public void applyTo(AbstractPlayer player) {
        player.position().set(position);
        player.rotation().set(yaw, pitch);
    }


    public InitPlayerPacket2C() { }

    @Override
    public void write(ExtDataOutputStream stream) throws IOException {
        stream.writeVec3f(position);
        stream.writeFloats(yaw, pitch);
    }

    @Override
    public void read(ExtDataInputStream stream) throws IOException {
        position = stream.readVec3f();
        yaw = stream.readFloat();
        pitch = stream.readFloat();
    }

    @Override
    public void handle(IClientProtocolGame handler) {
        handler.handleInitPlayerPacket(this);
    }

}
