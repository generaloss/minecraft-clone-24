package generaloss.mc24.accountservice.packet;

import generaloss.mc24.accountservice.AccountServiceClientConnection;
import jpize.util.io.ExtDataInputStream;
import jpize.util.io.ExtDataOutputStream;
import jpize.util.net.tcp.packet.IPacket;
import jpize.util.security.PublicRSA;

import java.io.IOException;

public class Packet2CPublicKey extends IPacket<AccountServiceClientConnection> {

    private PublicRSA publicKey;

    public Packet2CPublicKey(PublicRSA publicKey) {
        this.publicKey = publicKey;
    }

    public Packet2CPublicKey() { }

    public PublicRSA getPublicKey() {
        return publicKey;
    }

    @Override
    public void write(ExtDataOutputStream stream) throws IOException {
        stream.writeByteArray(publicKey.getKey().getEncoded());
    }

    @Override
    public void read(ExtDataInputStream stream) throws IOException {
        publicKey = new PublicRSA(stream.readByteArray());
    }

    @Override
    public void handle(AccountServiceClientConnection handler) {
        handler.handlePublicKey(this);
    }

}
