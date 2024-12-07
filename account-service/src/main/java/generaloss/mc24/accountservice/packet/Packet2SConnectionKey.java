package generaloss.mc24.accountservice.packet;

import generaloss.mc24.accountservice.AccountServiceServerConnection;
import jpize.util.io.ExtDataInputStream;
import jpize.util.io.ExtDataOutputStream;
import jpize.util.net.tcp.packet.IPacket;

import java.io.IOException;

public class Packet2SConnectionKey extends IPacket<AccountServiceServerConnection> {

    private byte[] encryptedKeyBytes;

    public Packet2SConnectionKey(byte[] encryptedKeyBytes) {
        this.encryptedKeyBytes = encryptedKeyBytes;
    }

    public Packet2SConnectionKey() { }

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
    public void handle(AccountServiceServerConnection handler) {
        handler.handleConnectionKey(this);
    }

}