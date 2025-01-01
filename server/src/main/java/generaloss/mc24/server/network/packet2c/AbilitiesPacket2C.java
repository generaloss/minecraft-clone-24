package generaloss.mc24.server.network.packet2c;

import generaloss.mc24.server.network.protocol.IClientProtocolGame;
import jpize.util.io.ExtDataInputStream;
import jpize.util.io.ExtDataOutputStream;
import jpize.util.net.tcp.packet.NetPacket;

import java.io.IOException;

public class AbilitiesPacket2C extends NetPacket<IClientProtocolGame> {

    @Override
    public void write(ExtDataOutputStream stream) throws IOException {

    }

    @Override
    public void read(ExtDataInputStream stream) throws IOException {

    }

    @Override
    public void handle(IClientProtocolGame handler) {
        handler.handleAbilitiesPacket(this);
    }

}
