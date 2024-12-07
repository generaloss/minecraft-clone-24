package generaloss.mc24.client.network;

import generaloss.mc24.client.Main;
import generaloss.mc24.server.network.protocol.IClientProtocol;
import jpize.util.net.tcp.TcpConnection;
import jpize.util.net.tcp.packet.IPacket;
import jpize.util.security.KeyAES;

public abstract class ClientProtocol implements IClientProtocol {

    private final Main context;
    private final TcpConnection tcpConnection;

    public ClientProtocol(Main context, TcpConnection tcpConnection) {
        this.context = context;
        this.tcpConnection = tcpConnection;
    }

    public Main context() {
        return context;
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
