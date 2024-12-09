package generaloss.mc24.accountservice.network;

import generaloss.mc24.accountservice.network.packet.PublicKeyPacket2C;
import generaloss.mc24.accountservice.network.packet.ResponsePacket2C;
import generaloss.mc24.accountservice.network.packet.EncodeKeyPacket2S;
import generaloss.mc24.accountservice.network.packet.RequestPacket2S;
import jpize.util.io.DataStreamWriter;
import jpize.util.net.tcp.TcpClient;
import jpize.util.net.tcp.TcpConnection;
import jpize.util.net.tcp.packet.PacketDispatcher;
import jpize.util.net.tcp.packet.PacketHandler;
import jpize.util.security.KeyAES;
import jpize.util.time.TimeUtils;

import java.util.UUID;

public class Request implements PacketHandler {

    private final RequestType requestType;
    private final byte[] requestData;
    private final Response response;

    private final PacketDispatcher packetDispatcher;
    private final TcpClient tcpClient;

    private Request(String host, RequestType requestType, byte[] requestData) {
        this.requestType = requestType;
        this.requestData = requestData;
        this.response = new Response();

        this.packetDispatcher = new PacketDispatcher();
        this.packetDispatcher.register(PublicKeyPacket2C.class, ResponsePacket2C.class);

        this.tcpClient = new TcpClient();
        this.tcpClient.setOnReceive(this::onReceive);
        this.tcpClient.connect("localhost", RequestListener.PORT);
    }

    public boolean isConnectionClosed() {
        return tcpClient.isClosed();
    }

    public Response getResponse() {
        return response;
    }


    private void onReceive(TcpConnection tcpConnection, byte[] bytes) {
        packetDispatcher.readPacket(bytes, this);
        packetDispatcher.handlePackets();
    }

    public void handlePublicKey(PublicKeyPacket2C packet) {
        final KeyAES key = new KeyAES(128);
        final byte[] keyBytes = key.getKey().getEncoded();
        final byte[] encryptedKeyBytes = packet.getPublicKey().encrypt(keyBytes);
        tcpClient.send(new EncodeKeyPacket2S(encryptedKeyBytes));
        tcpClient.encode(key);
        tcpClient.send(new RequestPacket2S(requestType, requestData));
    }

    public void handleResponse(ResponsePacket2C packet) {
        response.setCode(packet.getCode());
        response.setData(packet.getData());
        tcpClient.disconnect();
    }


    public static Response send(RequestType requestType, DataStreamWriter dataFactory) {
        final Request request = new Request("localhost", requestType, DataStreamWriter.writeBytes(dataFactory));
        TimeUtils.waitFor(request::isConnectionClosed, 1000);
        return request.getResponse();
    }


    public static Response sendCreateAccount(String nickname, String password) { // bool
        return Request.send(RequestType.CREATE_ACCOUNT, stream -> {
            stream.writeStringUTF(nickname);
            stream.writeStringUTF(password);
        });
    }

    public static Response sendDeleteAccount(String nickname, String password) { // bool
        return Request.send(RequestType.DELETE_ACCOUNT, stream -> {
            stream.writeStringUTF(nickname);
            stream.writeStringUTF(password);
        });
    }

    public static Response sendLogin(String nickname, String password) { // sessionID
        return Request.send(RequestType.LOG_IN, stream -> {
            stream.writeStringUTF(nickname);
            stream.writeStringUTF(password);
        });
    }

    public static Response sendLogout(UUID uuid) { // bool
        return Request.send(RequestType.LOG_OUT, stream -> stream.writeUUID(uuid));
    }

    public static Response sendHasSession(UUID uuid) { // bool
        return Request.send(RequestType.HAS_SESSION, stream -> stream.writeUUID(uuid));
    }

    public static Response sendGetSessionInfo(UUID uuid) { // nickname, creation_date
        return Request.send(RequestType.GET_SESSION_INFO, stream -> stream.writeUUID(uuid));
    }

}
