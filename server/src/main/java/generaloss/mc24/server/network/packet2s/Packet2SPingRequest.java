package generaloss.mc24.server.network.packet2s;

import generaloss.mc24.server.network.protocol.ServerConnectionPing;
import jpize.util.io.ExtDataInputStream;
import jpize.util.io.ExtDataOutputStream;
import jpize.util.net.tcp.packet.IPacket;

import java.io.IOException;

public class Packet2SPingRequest extends IPacket<ServerConnectionPing> {

    private long timestamp0;

    public Packet2SPingRequest() {
        this.timestamp0 = System.currentTimeMillis();
    }

    @Override
    public void write(ExtDataOutputStream stream) throws IOException {
        stream.writeLong(timestamp0);
    }

    @Override
    public void read(ExtDataInputStream stream) throws IOException {
        timestamp0 = stream.readLong();
    }

    @Override
    public void handle(ServerConnectionPing handler) {

    }

}
