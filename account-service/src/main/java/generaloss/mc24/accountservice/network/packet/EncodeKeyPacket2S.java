package generaloss.mc24.accountservice.network.packet;

import generaloss.mc24.accountservice.network.Connection;
import jpize.util.io.ExtDataInputStream;
import jpize.util.io.ExtDataOutputStream;
import jpize.util.net.tcp.packet.IPacket;

import java.io.IOException;

public class EncodeKeyPacket2S extends IPacket<Connection> {

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
    public void handle(Connection handler) {
        handler.handleConnectionKey(this);
    }

}