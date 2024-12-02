package generaloss.mc24.server.network.protocol;

import generaloss.mc24.server.Server;
import jpize.util.net.tcp.packet.PacketHandler;

public abstract class ServerConnection implements PacketHandler {

    protected final Server server;

    public ServerConnection(Server server) {
        this.server = server;
    }

}
