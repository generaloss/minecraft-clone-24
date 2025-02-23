package generaloss.mc24.server.network.packet2s;

import generaloss.mc24.server.network.protocol.IServerProtocolLogin;
import jpize.util.io.ExtDataInputStream;
import jpize.util.io.ExtDataOutputStream;
import jpize.util.net.packet.NetPacket;

import java.io.IOException;

public class EncodeKeyPacket2S extends NetPacket<IServerProtocolLogin> {

    private byte[] encryptedKeyBytes;

    public EncodeKeyPacket2S(byte[] encryptedKeyBytes) {
        this.encryptedKeyBytes = encryptedKeyBytes;
    }

    public EncodeKeyPacket2S() { }

    public byte[] getEncryptedKeyBytes() {
        return encryptedKeyBytes;
    }

    @Override
    public void write(ExtDataOutputStream stream) throws IOException {
        stream.writeByteArray(encryptedKeyBytes);
    }

    @Override
    public void read(ExtDataInputStream stream) throws IOException {
        encryptedKeyBytes = stream.readByteArray();
    }

    @Override
    public void handle(IServerProtocolLogin handler) {
        handler.handleConnectionKeyPacket(this);
    }

}