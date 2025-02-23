package generaloss.mc24.server.network.packet2c;

import generaloss.mc24.server.network.protocol.IClientProtocol;
import jpize.util.io.ExtDataInputStream;
import jpize.util.io.ExtDataOutputStream;
import jpize.util.net.packet.NetPacket;

import java.io.IOException;

public class DisconnectPacket2C extends NetPacket<IClientProtocol> {

    private String message;

    public DisconnectPacket2C(String message) {
        this.message = message;
    }

    public DisconnectPacket2C() { }

    public String getMessage() {
        return message;
    }

    @Override
    public void write(ExtDataOutputStream stream) throws IOException {
        stream.writeUTFString(message);
    }

    @Override
    public void read(ExtDataInputStream stream) throws IOException {
        message = stream.readUTFString();
    }

    @Override
    public void handle(IClientProtocol handler) {
        handler.handleDisconnectPacket(this);
    }

}
