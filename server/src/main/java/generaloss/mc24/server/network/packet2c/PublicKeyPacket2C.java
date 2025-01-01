package generaloss.mc24.server.network.packet2c;

import generaloss.mc24.server.network.protocol.IClientProtocolLogin;
import jpize.util.io.ExtDataInputStream;
import jpize.util.io.ExtDataOutputStream;
import jpize.util.net.tcp.packet.NetPacket;
import jpize.util.security.PublicRSA;

import java.io.IOException;

public class PublicKeyPacket2C extends NetPacket<IClientProtocolLogin> {

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
    public void handle(IClientProtocolLogin handler) {
        handler.handlePublicKey(this);
    }

}
