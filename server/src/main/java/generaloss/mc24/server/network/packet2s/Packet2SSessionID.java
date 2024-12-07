package generaloss.mc24.server.network.packet2s;

import generaloss.mc24.server.network.protocol.IServerProtocolLogin;
import jpize.util.io.ExtDataInputStream;
import jpize.util.io.ExtDataOutputStream;
import jpize.util.net.tcp.packet.IPacket;

import java.io.IOException;
import java.util.UUID;

public class Packet2SSessionID extends IPacket<IServerProtocolLogin> {

    private UUID sessionID;

    public Packet2SSessionID(UUID sessionID) {
        this.sessionID = sessionID;
    }

    public Packet2SSessionID() { }

    public UUID getSessionID() {
        return sessionID;
    }

    @Override
    public void write(ExtDataOutputStream stream) throws IOException {
        stream.writeUUID(sessionID);
    }

    @Override
    public void read(ExtDataInputStream stream) throws IOException {
        sessionID = stream.readUUID();
    }

    @Override
    public void handle(IServerProtocolLogin handler) {
        handler.handleSessionID(this);
    }

}