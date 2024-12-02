package generaloss.mc24.server.network.packet2c;

import generaloss.mc24.server.network.protocol.ClientPacketHandlerPing;
import jpize.util.io.ExtDataInputStream;
import jpize.util.io.ExtDataOutputStream;
import jpize.util.net.tcp.packet.IPacket;

import java.io.IOException;

public class Packet2CPingResponse extends IPacket<ClientPacketHandlerPing> {

    private String motd;
    private String version;
    //! add icon

    @Override
    public void write(ExtDataOutputStream stream) throws IOException {
        stream.writeUTF(motd);
        stream.writeUTF(version);
    }

    @Override
    public void read(ExtDataInputStream stream) throws IOException {
        motd = stream.readUTF();
        version = stream.readUTF();
    }

    @Override
    public void handle(ClientPacketHandlerPing handler) {

    }

}
