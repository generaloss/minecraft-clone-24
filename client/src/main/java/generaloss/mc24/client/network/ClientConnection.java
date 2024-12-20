package generaloss.mc24.client.network;

import generaloss.mc24.client.Main;
import generaloss.mc24.server.network.packet2c.DisconnectPacket2C;
import generaloss.mc24.server.network.packet2c.PublicKeyPacket2C;
import generaloss.mc24.server.network.packet2c.ServerInfoResponsePacket2C;
import jpize.util.net.tcp.TCPClient;
import jpize.util.net.tcp.TCPConnection;
import jpize.util.net.tcp.packet.IPacket;
import jpize.util.net.tcp.packet.PacketDispatcher;

public class ClientConnection {

    protected final Main context;
    private final TCPClient tcpClient;
    private final PacketDispatcher packetDispatcher;
    private ClientProtocol protocol;

    public ClientConnection(Main context) {
        this.context = context;
        this.tcpClient = new TCPClient()
            .setOnConnect(this::onConnect)
            .setOnDisconnect(this::onDisconnect)
            .setOnReceive(this::onReceive);
        this.packetDispatcher = new PacketDispatcher().register(
            ServerInfoResponsePacket2C.class,
            PublicKeyPacket2C.class,
            DisconnectPacket2C.class
        );
    }

    public void connect(String host, int port) {
        tcpClient.connect(host, port);
        protocol = new ClientProtocolLogin(context, tcpClient.connection());
        if(!tcpClient.isConnected())
            throw new IllegalStateException("Invalid server address");
    }

    public void disconnect() {
        tcpClient.disconnect();
        protocol = null;
    }

    private void onConnect(TCPConnection tcpConnection) { }

    private void onDisconnect(TCPConnection tcpConnection) { }

    private void onReceive(TCPConnection tcpConnection, byte[] bytes) {
        packetDispatcher.readPacket(bytes, protocol);
        packetDispatcher.handlePackets();
    }

    public void sendPacket(IPacket<?> packet) {
        tcpClient.send(packet);
    }

}
