package generaloss.mc24.server;

import generaloss.mc24.server.protocol.ServerConnection;
import jpize.util.net.tcp.TcpConnection;
import jpize.util.net.tcp.packet.PacketDispatcher;

import java.util.HashMap;
import java.util.Map;

public class ServerConnections {

    private final Map<TcpConnection, ServerConnection> connections;
    private final PacketDispatcher packetDispatcher;

    public ServerConnections(Server server) {
        this.connections = new HashMap<>();
        this.packetDispatcher = new PacketDispatcher().register();
    }


    public void onConnect(TcpConnection connection) {
        connections.put(connection, null);
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
