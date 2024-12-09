package generaloss.mc24.server.network.connection;

import generaloss.mc24.server.Server;
import generaloss.mc24.server.network.packet2s.EncodeKeyPacket2S;
import generaloss.mc24.server.network.packet2s.LoginRequestPacket2S;
import generaloss.mc24.server.network.packet2s.ServerInfoRequestPacket2S;
import generaloss.mc24.server.network.packet2s.SessionIDPacket2S;
import generaloss.mc24.server.network.protocol.IServerProtocolGame;
import jpize.util.net.tcp.TcpConnection;
import jpize.util.net.tcp.TcpServer;
import jpize.util.net.tcp.packet.PacketDispatcher;
import jpize.util.security.KeyRSA;

public class NetServer {

    private final Server server;
    private final KeyRSA encryptionKey;
    private final PacketDispatcher packetDispatcher;
    private final TcpServer tcpServer;

    public NetServer(Server server) {
        this.server = server;
        this.encryptionKey = new KeyRSA(1024);
        this.packetDispatcher = new PacketDispatcher().register(
            ServerInfoRequestPacket2S.class,
            LoginRequestPacket2S.class,
            EncodeKeyPacket2S.class,
            SessionIDPacket2S.class
        );
        this.tcpServer = new TcpServer()
            .setOnConnect(this::onConnect)
            .setOnReceive(this::onReceive);
    }

    public KeyRSA getEncryptionKey() {
        return encryptionKey;
    }

    public TcpServer tcpServer() {
        return tcpServer;
    }


    public void onConnect(TcpConnection tcpConnection) {
        tcpConnection.attach(new ServerConnectionLogin(server, tcpConnection));
    }

    public void onReceive(TcpConnection tcpConnection, byte[] bytes) {
        final ServerConnection serverConnection = tcpConnection.attachment();
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
