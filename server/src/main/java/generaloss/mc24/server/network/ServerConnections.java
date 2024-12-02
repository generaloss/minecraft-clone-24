package generaloss.mc24.server.network;

import generaloss.mc24.server.Server;
import generaloss.mc24.server.network.protocol.ServerConnection;
import generaloss.mc24.server.network.protocol.ServerConnectionPing;
import jpize.util.net.tcp.TcpConnection;
import jpize.util.net.tcp.packet.PacketDispatcher;

import java.util.HashMap;
import java.util.Map;

public class ServerConnections {

    private final Server server;
    private final Map<TcpConnection, ServerConnection> connections;
    private final PacketDispatcher packetDispatcher;

    public ServerConnections(Server server) {
        this.server = server;
        this.connections = new HashMap<>();
        this.packetDispatcher = new PacketDispatcher().register();
    }


    public void onConnect(TcpConnection connection) {
        connections.put(connection, new ServerConnectionPing(server));
    }

    public void onDisconnect(TcpConnection connection) {
        connections.remove(connection);
    }

    public void onReceive(TcpConnection tcpConnection, byte[] bytes) {
        final ServerConnection serverConnection = connections.get(tcpConnection);
        if(serverConnection == null)
            return;

        packetDispatcher.readPacket(bytes, serverConnection);
        packetDispatcher.handlePackets();
    }

}
