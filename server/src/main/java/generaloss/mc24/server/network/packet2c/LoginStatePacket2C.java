package generaloss.mc24.server.network.packet2c;

import generaloss.mc24.server.network.protocol.IClientProtocolLogin;
import jpize.util.io.ExtDataInputStream;
import jpize.util.io.ExtDataOutputStream;
import jpize.util.net.packet.NetPacket;

import java.io.IOException;

public class LoginStatePacket2C extends NetPacket<IClientProtocolLogin> {

    private String message;

    public LoginStatePacket2C(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }


    public LoginStatePacket2C() { }

    @Override
    public void write(ExtDataOutputStream stream) throws IOException {
        stream.writeUTFString(message);
    }

    @Override
    public void read(ExtDataInputStream stream) throws IOException {
        message = stream.readUTFString();
    }

    @Override
    public void handle(IClientProtocolLogin handler) {
        handler.handleLoginStatePacket(this);
    }

}
