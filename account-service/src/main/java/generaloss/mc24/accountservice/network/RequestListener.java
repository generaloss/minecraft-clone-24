package generaloss.mc24.accountservice.network;

import generaloss.mc24.accountservice.Main;
import generaloss.mc24.accountservice.network.packet.PublicKeyPacket2C;
import generaloss.mc24.accountservice.network.packet.EncodeKeyPacket2S;
import generaloss.mc24.accountservice.network.packet.RequestPacket2S;
import jpize.util.io.ExtDataInputStream;
import jpize.util.net.tcp.TcpConnection;
import jpize.util.net.tcp.TcpServer;
import jpize.util.net.tcp.packet.PacketDispatcher;
import jpize.util.security.KeyRSA;

import java.io.Closeable;
import java.io.IOException;

public class RequestListener implements Closeable {

    public static final int PORT = 8888;

    private final Main context;
    private final KeyRSA key;
    private final PacketDispatcher packetDispatcher;
    private final TcpServer tcpServer;

    public RequestListener(Main context) {
        this.context = context;
        this.key = new KeyRSA(1024);

        this.packetDispatcher = new PacketDispatcher()
            .register(EncodeKeyPacket2S.class, RequestPacket2S.class);

        this.tcpServer = new TcpServer()
            .setOnConnect(this::onConnect)
            .setOnReceive(this::onReceive)
            .run(PORT);
    }

    public KeyRSA getKey() {
        return key;
    }

    public boolean isClosed() {
        return tcpServer.isClosed();
    }


    private void onConnect(TcpConnection tcpConnection) {
        tcpConnection.attach(new Connection(this, tcpConnection));
        tcpConnection.send(new PublicKeyPacket2C(key.getPublic()));
    }

    private void onReceive(TcpConnection tcpConnection, byte[] bytes) {
        final Connection connection = tcpConnection.attachment();
        packetDispatcher.readPacket(bytes, connection);
        packetDispatcher.handlePackets();
    }

    @Override
    public void close() {
        tcpServer.close();
    }


    public void onRequest(Connection connection, RequestType type, ExtDataInputStream stream) {
        try{
            switch(type){
                case CREATE_ACCOUNT   -> context.createAccount(connection, stream);
                case DELETE_ACCOUNT   -> context.deleteAccount(connection, stream);
                case LOG_IN           -> context.logInAccount(connection, stream);
                case LOG_OUT          -> context.logOutAccount(connection, stream);
                case HAS_SESSION      -> context.hasSession(connection, stream);
                case GET_SESSION_INFO -> context.getSessionInfo(connection, stream);
            }
        }catch(IOException e){
            connection.sendResponse(ResponseCode.ERROR, "Invalid request format.");
        }catch(IllegalArgumentException e) {
            connection.sendResponse(ResponseCode.ERROR, e.getMessage());
        }
    }

}
