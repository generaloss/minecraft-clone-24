package generaloss.mc24.client.network;

import generaloss.mc24.client.Main;
import generaloss.mc24.server.network.packet2c.*;
import jpize.context.Jpize;
import jpize.util.net.tcp.TCPClient;
import jpize.util.net.tcp.TCPConnection;
import jpize.util.net.packet.NetPacket;
import jpize.util.net.packet.NetPacketDispatcher;

public class ClientNetwork {

    protected final Main context;
    private final TCPClient tcpClient;
    private final NetPacketDispatcher packetDispatcher;

    public ClientNetwork(Main context) {
        this.context = context;
        this.tcpClient = new TCPClient()
            .setOnConnect(this::onConnect)
            .setOnDisconnect(this::onDisconnect)
            .setOnReceive(this::onReceive);

        this.packetDispatcher = new NetPacketDispatcher().register(
            ServerInfoResponsePacket2C.class,
            PublicKeyPacket2C.class,
            DisconnectPacket2C.class,
            ChunkPacket2C.class,
            SetBlockStatePacket2C.class,
            InitPlayerPacket2C.class,
            InsertEntityPacket2C.class,
            RemoveEntityPacket2C.class,
            MoveEntityPacket2C.class,
            LoginStatePacket2C.class,
            InitSessionPacket2C.class
        );
    }

    public void connect(String host, int port) {
        try{
            tcpClient.connect(host, port, 1000L);
        }catch(Exception e) {
            throw new IllegalStateException("Failed to connect to server: " + e.getMessage());
        }

        final TCPConnection tcpConnection = tcpClient.connection();
        final ClientConnectionLogin loginConnection = new ClientConnectionLogin(context, tcpConnection);
        tcpConnection.attach(loginConnection);
    }

    public void disconnect() {
        tcpClient.disconnect();
    }

    public void sendPacket(NetPacket<?> packet) {
        tcpClient.send(packet);
    }


    private void onConnect(TCPConnection tcpConnection) {
        tcpConnection.options().setTcpNoDelay(true);
        System.out.println("[INFO]: Connected to server");
    }

    private void onDisconnect(TCPConnection tcpConnection) {
        context.closeSession();
    }

    private void onReceive(TCPConnection tcpConnection, byte[] bytes) {
        if(!packetDispatcher.readPacket(bytes, tcpConnection.attachment())){
            System.err.println("[WARN]: Cannot read packet.");
            Jpize.syncExecutor().exec(context::closeSession);
        }
        if(packetDispatcher.handlePackets() == 0)
            System.err.println("[WARN]: Cannot handle packets.");
    }

}
