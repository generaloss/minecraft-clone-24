package generaloss.mc24.server.network.packet2c;

import generaloss.mc24.server.network.protocol.IClientProtocolLogin;
import jpize.util.io.ExtDataInputStream;
import jpize.util.io.ExtDataOutputStream;
import jpize.util.net.packet.NetPacket;

import java.io.IOException;

public class ServerInfoResponsePacket2C extends NetPacket<IClientProtocolLogin> {

    private String motd;
    private String version;
    private long timestamp;
    //! server icon

    public ServerInfoResponsePacket2C(String motd, String version, long timestamp) {
        this.motd = motd;
        this.version = version;
        this.timestamp = timestamp;
    }

    public ServerInfoResponsePacket2C() { }

    public String getMotd() {
        return motd;
    }

    public String getVersion() {
        return version;
    }

    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public void write(ExtDataOutputStream stream) throws IOException {
        stream.writeUTF(motd);
        stream.writeUTF(version);
        stream.writeLong(timestamp);
    }

    @Override
    public void read(ExtDataInputStream stream) throws IOException {
        motd = stream.readUTF();
        version = stream.readUTF();
        timestamp = stream.readLong();
    }

    @Override
    public void handle(IClientProtocolLogin handler) {
        handler.handleServerInfoResponse(this);
    }

}
