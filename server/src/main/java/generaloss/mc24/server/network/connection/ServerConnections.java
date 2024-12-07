package generaloss.mc24.server.network.connection;

import generaloss.mc24.server.Server;
import generaloss.mc24.server.network.packet2s.Packet2SConnectionKey;
import generaloss.mc24.server.network.packet2s.Packet2SLoginRequest;
import generaloss.mc24.server.network.packet2s.Packet2SServerInfoRequest;
import generaloss.mc24.server.network.protocol.IServerProtocolGame;
import jpize.util.net.tcp.TcpConnection;
import jpize.util.net.tcp.packet.PacketDispatcher;
import jpize.util.security.KeyRSA;

import java.util.HashMap;
import java.util.Map;

public class ServerConnections {

    private final Server server;
    private final KeyRSA encryptionKey;
    private final Map<TcpConnection, ServerConnection> connections;
    private final PacketDispatcher packetDispatcher;

    public ServerConnections(Server server) {
        this.server = server;
        this.encryptionKey = new KeyRSA(1024);
        this.connections = new HashMap<>();
        this.packetDispatcher = new PacketDispatcher().register(
            Packet2SServerInfoRequest.class,
            Packet2SLoginRequest.class,
            Packet2SConnectionKey.class
        );
    }

    public KeyRSA getEncryptionKey() {
        return encryptionKey;
    }


    public void onConnect(TcpConnection tcpConnection) {
        connections.put(tcpConnection, new ServerConnectionLogin(server, tcpConnection));
    }

    public void onDisconnect(TcpConnection tcpConnection) {
        connections.remove(tcpConnection);
    }

    public void onReceive(TcpConnection tcpConnection, byte[] bytes) {
        final ServerConnection serverConnection = connections.get(tcpConnection);
        if(serverConnection == null)
            return;

        packetDispatcher.readPacket(bytes, serverConnection);
        try{
            packetDispatcher.handlePackets();
        }catch(ClassCastException e){
            System.err.println("[WARN]: Received illegal packet in current protocol '" + ((serverConnection instanceof IServerProtocolGame) ? "GAME" : "LOGIN") + "'");
            tcpConnection.close();
        }
    }

}
