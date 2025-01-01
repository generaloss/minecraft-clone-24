package generaloss.mc24.accountservice.network;

import generaloss.mc24.accountservice.Account;
import generaloss.mc24.accountservice.SessionDispatcher;
import generaloss.mc24.accountservice.network.packet.PublicKeyPacket2C;
import generaloss.mc24.accountservice.network.packet.EncodeKeyPacket2S;
import generaloss.mc24.accountservice.network.packet.RequestPacket2S;
import jpize.util.io.ExtDataInputStream;
import jpize.util.net.tcp.TCPConnection;
import jpize.util.net.tcp.TCPServer;
import jpize.util.net.tcp.packet.NetPacketDispatcher;
import jpize.util.res.Resource;
import jpize.util.security.RSAKey;

import java.io.Closeable;
import java.io.IOException;
import java.util.UUID;

public class RequestListener implements Closeable {

    public static final int PORT = 54588;

    private final RSAKey key;
    private final SessionDispatcher sessionDispatcher;
    private final NetPacketDispatcher packetDispatcher;
    private final TCPServer tcpServer;

    public RequestListener() {
        this.key = new RSAKey(1024);
        this.sessionDispatcher = new SessionDispatcher();
        this.packetDispatcher = new NetPacketDispatcher()
            .register(EncodeKeyPacket2S.class, RequestPacket2S.class);

        Resource.external("./accounts/").mkdir();

        this.tcpServer = new TCPServer()
            .setOnConnect(this::onConnect)
            .setOnReceive(this::onReceive)
            .run(PORT);
    }

    public RSAKey getKey() {
        return key;
    }

    public boolean isClosed() {
        return tcpServer.isClosed();
    }


    private void onConnect(TCPConnection tcpConnection) {
        tcpConnection.attach(new Connection(this, tcpConnection));
        tcpConnection.send(new PublicKeyPacket2C(key.getPublic()));
    }

    private void onReceive(TCPConnection tcpConnection, byte[] bytes) {
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
                case CREATE_ACCOUNT   -> this.createAccount(connection, stream);
                case DELETE_ACCOUNT   -> this.deleteAccount(connection, stream);
                case LOG_IN           -> this.logInAccount(connection, stream);
                case LOG_OUT          -> this.logOutAccount(connection, stream);
                case HAS_SESSION      -> this.hasSession(connection, stream);
                case GET_SESSION_INFO -> this.getSessionInfo(connection, stream);
            }
        }catch(IOException e){
            connection.sendResponse(ResponseCode.ERROR, "Invalid request format.");
        }catch(IllegalArgumentException e) {
            connection.sendResponse(ResponseCode.ERROR, e.getMessage());
        }
    }

    public void createAccount(Connection connection, ExtDataInputStream stream) throws IOException, IllegalArgumentException {
        // (nickname, password) -> (bool)
        Account.create(stream.readStringUTF(), stream.readStringUTF());
        connection.sendResponse(ResponseCode.NO_ERROR, "Account created successfully.");
    }

    public void deleteAccount(Connection connection, ExtDataInputStream stream) throws IOException, IllegalArgumentException {
        // (nickname, password) -> (bool)
        final String nickname = stream.readStringUTF();
        final String password = stream.readStringUTF();

        final Account account = Account.load(nickname);
        if(!account.getPassword().equals(password))
            throw new IllegalArgumentException("Wrong username or password.");

        Account.delete(account.getNickname());
        connection.sendResponse(ResponseCode.NO_ERROR, "Account deleted successfully.");
    }

    public void logInAccount(Connection connection, ExtDataInputStream stream) throws IOException, IllegalArgumentException {
        // (nickname, password) -> (sessionID)
        final String nickname = stream.readStringUTF();
        final String password = stream.readStringUTF();

        final Account account = Account.load(nickname);
        if(!account.getPassword().equals(password))
            throw new IllegalArgumentException("Wrong username or password.");

        final UUID sessionID = sessionDispatcher.logIn(account);
        connection.sendResponse(ResponseCode.NO_ERROR, sessionID);
    }

    public void logOutAccount(Connection connection, ExtDataInputStream stream) throws IOException, IllegalArgumentException {
        // (nickname, password) -> (bool)
        final String nickname = stream.readStringUTF();
        final String password = stream.readStringUTF();

        final Account account = Account.load(nickname);
        if(!account.getPassword().equals(password))
            throw new IllegalArgumentException("Wrong username or password.");

        final UUID sessionID = sessionDispatcher.getSessionID(account);
        if(sessionID == null)
            throw new IllegalArgumentException("Session not found.");

        sessionDispatcher.logOut(sessionID);
        connection.sendResponse(ResponseCode.NO_ERROR, "Logged out successfully.");
    }

    public void hasSession(Connection connection, ExtDataInputStream stream) throws IOException, IllegalArgumentException {
        // (sessionID) -> (bool)
        final UUID sessionID = stream.readUUID();
        final Account account = sessionDispatcher.getSessionAccount(sessionID);
        connection.sendResponse(ResponseCode.NO_ERROR, account != null);
    }

    public void getSessionInfo(Connection connection, ExtDataInputStream inStream) throws IOException, IllegalArgumentException {
        // (sessionID) -> (nickname, creation_date)
        final UUID sessionID = inStream.readUUID();
        final Account account = sessionDispatcher.getSessionAccount(sessionID);
        if(account == null)
            throw new IllegalArgumentException("Session does not exist.");

        connection.sendResponse(ResponseCode.NO_ERROR, outStream -> {
            outStream.writeStringUTF(account.getNickname());
            outStream.writeStringUTF(account.getCreationDate());
        });
    }

}
