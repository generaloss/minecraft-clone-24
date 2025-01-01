package generaloss.mc24.server.network.connection;

import generaloss.mc24.server.Server;
import generaloss.mc24.server.network.packet2s.*;
import generaloss.mc24.server.network.protocol.IServerProtocolGame;
import generaloss.mc24.server.player.ServerPlayer;
import jpize.util.net.tcp.TCPConnection;
import jpize.util.net.tcp.TCPServer;
import jpize.util.net.tcp.packet.NetPacketDispatcher;
import jpize.util.security.RSAKey;

public class NetServer {

    private final Server server;
    private final RSAKey encryptionKey;
    private final NetPacketDispatcher packetDispatcher;
    private final TCPServer tcpServer;

    public NetServer(Server server) {
        this.server = server;
        this.encryptionKey = new RSAKey(1024);
        this.packetDispatcher = new NetPacketDispatcher().register(
            ServerInfoRequestPacket2S.class,
            LoginRequestPacket2S.class,
            EncodeKeyPacket2S.class,
            SessionIDPacket2S.class,
            SetBlockStatePacket2S.class
        );
        this.tcpServer = new TCPServer()
            .setOnConnect(this::onConnect)
            .setOnDisconnect(this::onDisconnect)
            .setOnReceive(this::onReceive);
    }

    public RSAKey getEncryptionKey() {
        return encryptionKey;
    }

    public TCPServer tcpServer() {
        return tcpServer;
    }


    public void onConnect(TCPConnection tcpConnection) {
        tcpConnection.setTcpNoDelay(true);
        tcpConnection.attach(new ServerConnectionLogin(server, tcpConnection));
    }

    public void onDisconnect(TCPConnection tcpConnection) {
        if(tcpConnection.attachment() instanceof ServerConnectionGame protocolGame) {
            final ServerPlayer player = protocolGame.player();
            server.players().removePlayer(player);
        }
    }

    public void onReceive(TCPConnection tcpConnection, byte[] bytes) {
        final ServerConnection serverConnection = tcpConnection.attachment();
        if(serverConnection == null)
            return;

        if(!packetDispatcher.readPacket(bytes, serverConnection))
            System.err.println("Cannot read packet.");
        try{
            if(packetDispatcher.handlePackets() == 0)
                System.out.println("Cannot handle packets.");

        }catch(ClassCastException e){
            System.err.println("[WARN]: Received illegal packet in current protocol '" + ((serverConnection instanceof IServerProtocolGame) ? "GAME" : "LOGIN") + "'");
            tcpConnection.close();
        }
    }

}
