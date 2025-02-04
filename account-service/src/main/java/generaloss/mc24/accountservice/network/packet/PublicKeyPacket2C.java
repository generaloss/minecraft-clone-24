package generaloss.mc24.accountservice.network.packet;

import generaloss.mc24.accountservice.network.Request;
import jpize.util.io.ExtDataInputStream;
import jpize.util.io.ExtDataOutputStream;
import jpize.util.net.packet.NetPacket;
import jpize.util.security.PublicRSA;

import java.io.IOException;

public class PublicKeyPacket2C extends NetPacket<Request> {

    private PublicRSA publicKey;

    public PublicKeyPacket2C(PublicRSA publicKey) {
        this.publicKey = publicKey;
    }

    public PublicKeyPacket2C() { }

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
    public void handle(Request handler) {
        handler.handlePublicKey(this);
    }

}
