package generaloss.mc24.server.network.packet2s;

import generaloss.mc24.server.network.protocol.IServerProtocolLogin;
import jpize.util.io.ExtDataInputStream;
import jpize.util.io.ExtDataOutputStream;
import jpize.util.net.tcp.packet.NetPacket;

import java.io.IOException;

public class LoginRequestPacket2S extends NetPacket<IServerProtocolLogin> {

    private String clientVersion;

    public LoginRequestPacket2S(String clientVersion) {
        this.clientVersion = clientVersion;
    }

    public LoginRequestPacket2S() { }

    public String getClientVersion() {
        return clientVersion;
    }

    @Override
    public void write(ExtDataOutputStream stream) throws IOException {
        stream.writeStringUTF(clientVersion);
    }

    @Override
    public void read(ExtDataInputStream stream) throws IOException {
        clientVersion = stream.readStringUTF();
    }

    @Override
    public void handle(IServerProtocolLogin handler) {
        handler.handleLoginRequest(this);
    }

}