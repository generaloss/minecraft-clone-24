package generaloss.mc24.server.network.packet2s;

import generaloss.mc24.server.network.protocol.ServerConnection;
import jpize.util.io.ExtDataInputStream;
import jpize.util.io.ExtDataOutputStream;
import jpize.util.net.tcp.packet.IPacket;

import java.io.IOException;

public class Packet2SServerInfoRequest extends IPacket<ServerConnection> {

    private long timestamp;

    public Packet2SServerInfoRequest(long timestamp) {
        this.timestamp = timestamp;
    }

    public Packet2SServerInfoRequest() { }

    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public void write(ExtDataOutputStream stream) throws IOException {
        stream.writeLong(timestamp);
    }

    @Override
    public void read(ExtDataInputStream stream) throws IOException {
        timestamp = stream.readLong();
    }

    @Override
    public void handle(ServerConnection handler) {
        handler.onServerInfoRequest(this);
    }

}
