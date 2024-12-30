package generaloss.mc24.client.network;

import generaloss.mc24.client.Main;
import generaloss.mc24.server.network.packet2c.*;
import jpize.util.net.tcp.TCPClient;
import jpize.util.net.tcp.TCPConnection;
import jpize.util.net.tcp.packet.IPacket;
import jpize.util.net.tcp.packet.PacketDispatcher;

public class ClientConnection {

    protected final Main context;
    private final TCPClient tcpClient;
    private final PacketDispatcher packetDispatcher;

    public ClientConnection(Main context) {
        this.context = context;
        this.tcpClient = new TCPClient()
            .setOnConnect(this::onConnect)
            .setOnDisconnect(this::onDisconnect)
            .setOnReceive(this::onReceive);
        this.packetDispatcher = new PacketDispatcher().register(
            ServerInfoResponsePacket2C.class,
            PublicKeyPacket2C.class,
            DisconnectPacket2C.class,
            ChunkPacket2C.class,
            SetBlockStatePacket2C.class
        );
    }

    public void connect(String host, int port) {
        try{
            tcpClient.connect(host, port);
        }catch(Exception e) {
            throw new IllegalStateException("Invalid server address: " + e.getMessage());
        }

        final TCPConnection tcpConnection = tcpClient.connection();
        final ClientProtocolLogin protocol = new ClientProtocolLogin(context, tcpConnection);
        tcpConnection.attach(protocol);
    }

    public void disconnect() {
        tcpClient.disconnect();
    }

    private void onConnect(TCPConnection tcpConnection) {
        tcpConnection.setTcpNoDelay(true);
    }

    private void onDisconnect(TCPConnection tcpConnection) { }

    private void onReceive(TCPConnection tcpConnection, byte[] bytes) {
        if(!packetDispatcher.readPacket(bytes, tcpConnection.attachment()))
            System.err.println("Cannot read packet.");
        if(packetDispatcher.handlePackets() == 0)
            System.err.println("Cannot handle packets.");
    }

    public void sendPacket(IPacket<?> packet) {
        tcpClient.send(packet);
    }

}
