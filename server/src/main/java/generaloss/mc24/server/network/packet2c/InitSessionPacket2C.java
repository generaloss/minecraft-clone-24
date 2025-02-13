package generaloss.mc24.server.network.packet2c;

import generaloss.mc24.server.network.protocol.IClientProtocolLogin;
import jpize.util.io.ExtDataInputStream;
import jpize.util.io.ExtDataOutputStream;
import jpize.util.net.packet.NetPacket;

import java.io.IOException;

public class InitSessionPacket2C extends NetPacket<IClientProtocolLogin> {

    @Override
    public void write(ExtDataOutputStream stream) throws IOException { }

    @Override
    public void read(ExtDataInputStream stream) throws IOException { }

    @Override
    public void handle(IClientProtocolLogin handler) {
        handler.handleInitSession(this);
    }

}