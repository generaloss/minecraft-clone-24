package generaloss.mc24.client.network;

import generaloss.mc24.client.Main;
import jpize.util.net.tcp.TcpClient;
import jpize.util.net.tcp.TcpConnection;
import jpize.util.net.tcp.packet.IPacket;
import jpize.util.net.tcp.packet.PacketDispatcher;
import jpize.util.net.tcp.packet.PacketHandler;

public class ClientConnection implements PacketHandler {

    protected final Main context;
    private final TcpClient tcpClient;
    private final PacketDispatcher packetDispatcher;

    public ClientConnection(Main context) {
        this.context = context;
        this.tcpClient = new TcpClient()
            .setOnConnect(this::onConnect)
            .setOnDisconnect(this::onDisconnect)
            .setOnReceive(this::onReceive);
        this.packetDispatcher = new PacketDispatcher().register();
    }

    public void connect(String host, int port) {
        System.out.println("connect");
        tcpClient.connect(host, port);
        System.out.println("closed : " + tcpClient.isClosed());
        if(!tcpClient.isConnected())
            throw new IllegalStateException("Invalid server address");
    }

    public void disconnect() {
        tcpClient.disconnect();
    }

    private void onConnect(TcpConnection connection) { }

    private void onDisconnect(TcpConnection connection) { }

    private void onReceive(TcpConnection connection, byte[] bytes) {
        packetDispatcher.readPacket(bytes, this);
        packetDispatcher.handlePackets();
    }

    public void sendPacket(IPacket<?> packet) {
        tcpClient.send(packet);
    }

}
