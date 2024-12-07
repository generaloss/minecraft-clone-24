package generaloss.mc24.server.network.connection;

import generaloss.mc24.server.Server;
import generaloss.mc24.server.network.protocol.IServerProtocol;
import jpize.util.net.tcp.TcpConnection;
import jpize.util.net.tcp.packet.IPacket;
import jpize.util.security.KeyAES;

public abstract class ServerConnection implements IServerProtocol {

    private final Server server;
    private final TcpConnection tcpConnection;

    public ServerConnection(Server server, TcpConnection tcpConnection) {
        this.server = server;
        this.tcpConnection = tcpConnection;
    }

    public Server server() {
        return server;
    }

    public void disconnect() {
        tcpConnection.close();
    }

    public void sendPacket(IPacket<?> packet) {
        tcpConnection.send(packet);
    }

    public void encode(KeyAES key) {
        tcpConnection.encode(key);
    }

}
