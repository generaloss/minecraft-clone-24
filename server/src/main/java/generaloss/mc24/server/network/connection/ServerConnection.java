package generaloss.mc24.server.network.connection;

import generaloss.mc24.server.Server;
import generaloss.mc24.server.network.protocol.IServerProtocol;
import jpize.util.net.tcp.TCPConnection;
import jpize.util.net.tcp.packet.IPacket;
import jpize.util.security.AESKey;

public abstract class ServerConnection implements IServerProtocol {

    private final Server server;
    private final TCPConnection tcpConnection;

    public ServerConnection(Server server, TCPConnection tcpConnection) {
        this.server = server;
        this.tcpConnection = tcpConnection;
    }

    public void setProtocol(IServerProtocol protocol) {
        tcpConnection.attach(protocol);
    }

    public Server server() {
        return server;
    }

    public TCPConnection tcpConnection() {
        return tcpConnection;
    }


    public void disconnect() {
        tcpConnection.close();
    }

    public void sendPacket(IPacket<?> packet) {
        tcpConnection.send(packet);
    }

    public void encode(AESKey key) {
        tcpConnection.encode(key);
    }

}
