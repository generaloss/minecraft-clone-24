package generaloss.mc24.server.network.protocol;

import generaloss.mc24.server.Server;
import generaloss.mc24.server.network.packet2c.Packet2CServerInfoResponse;
import generaloss.mc24.server.network.packet2s.Packet2SServerInfoRequest;
import jpize.util.net.tcp.TcpConnection;
import jpize.util.net.tcp.packet.PacketHandler;

public abstract class ServerConnection implements PacketHandler {

    protected final Server server;
    protected final TcpConnection connection;

    public ServerConnection(Server server, TcpConnection connection) {
        this.server = server;
        this.connection = connection;
    }


    public void onServerInfoRequest(Packet2SServerInfoRequest packet) {
        connection.send(new Packet2CServerInfoResponse(
                server.properties().getString("motd"),
                server.properties().getString("version"),
                packet.getTimestamp()
        ));
    }

}
