package generaloss.mc24.server.network.packet2s;

import generaloss.mc24.server.network.protocol.IServerProtocolGame;
import jpize.util.io.ExtDataInputStream;
import jpize.util.io.ExtDataOutputStream;
import jpize.util.net.packet.NetPacket;

import java.io.IOException;

public class InitSessionPacket2S extends NetPacket<IServerProtocolGame> {

    @Override
    public void write(ExtDataOutputStream stream) throws IOException { }

    @Override
    public void read(ExtDataInputStream stream) throws IOException { }

    @Override
    public void handle(IServerProtocolGame handler) {
        handler.handleInitSessionPacket(this);
    }

}