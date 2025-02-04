package generaloss.mc24.server.network.packet2s;

import generaloss.mc24.server.network.protocol.IServerProtocolLogin;
import jpize.util.io.ExtDataInputStream;
import jpize.util.io.ExtDataOutputStream;
import jpize.util.net.packet.NetPacket;

import java.io.IOException;

public class ServerInfoRequestPacket2S extends NetPacket<IServerProtocolLogin> {

    private long timestamp;

    public ServerInfoRequestPacket2S(long timestamp) {
        this.timestamp = timestamp;
    }

    public ServerInfoRequestPacket2S() { }

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
    public void handle(IServerProtocolLogin handler) {
        handler.handleServerInfoRequest(this);
    }

}
