package generaloss.mc24.accountservice.network;

import generaloss.mc24.accountservice.network.packet.PublicKeyPacket2C;
import generaloss.mc24.accountservice.network.packet.ResponsePacket2C;
import generaloss.mc24.accountservice.network.packet.EncodeKeyPacket2S;
import generaloss.mc24.accountservice.network.packet.RequestPacket2S;
import jpize.util.io.DataStreamWriter;
import jpize.util.net.tcp.TCPClient;
import jpize.util.net.tcp.TCPConnection;
import jpize.util.net.packet.NetPacketDispatcher;
import jpize.util.security.AESKey;
import jpize.util.time.TimeUtils;

import java.util.UUID;

public class Request {

    private final RequestType requestType;
    private final byte[] requestData;
    private final Response response;

    private final NetPacketDispatcher packetDispatcher;
    private final TCPClient tcpClient;

    private Request(String host, RequestType requestType, byte[] requestData) {
        this.requestType = requestType;
        this.requestData = requestData;
        this.response = new Response();

        this.packetDispatcher = new NetPacketDispatcher();
        this.packetDispatcher.register(PublicKeyPacket2C.class, ResponsePacket2C.class);

        this.tcpClient = new TCPClient();
        this.tcpClient.setOnReceive(this::onReceive);
        try{
            this.tcpClient.connect(host, RequestListener.PORT);
        }catch(Exception ignored){ }
    }

    public boolean isConnectionClosed() {
        return tcpClient.isClosed();
    }

    public Response getResponse() {
        return response;
    }


    private void onReceive(TCPConnection tcpConnection, byte[] bytes) {
        packetDispatcher.readPacket(bytes, this);
        packetDispatcher.handlePackets();
    }

    public void handlePublicKey(PublicKeyPacket2C packet) {
        final AESKey key = new AESKey(256);
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


    public static Response send(String host, RequestType requestType, DataStreamWriter dataFactory) {
        Request request = new Request(host, requestType, DataStreamWriter.writeBytes(dataFactory));
        TimeUtils.waitFor(request::isConnectionClosed, 5000);
        if(!request.getResponse().getCode().noError()) {
            System.out.println("try to send account-service request to localhost");
            request = new Request("localhost", requestType, DataStreamWriter.writeBytes(dataFactory));
            TimeUtils.waitFor(request::isConnectionClosed, 1000);
        }
        return request.getResponse();
    }


    public static Response sendCreateAccount(String host, String nickname, String password) { // bool
        return Request.send(host, RequestType.CREATE_ACCOUNT, stream -> {
            stream.writeUTFString(nickname);
            stream.writeUTFString(password);
        });
    }

    public static Response sendDeleteAccount(String host, String nickname, String password) { // bool
        return Request.send(host, RequestType.DELETE_ACCOUNT, stream -> {
            stream.writeUTFString(nickname);
            stream.writeUTFString(password);
        });
    }

    public static Response sendLogin(String host, String nickname, String password) { // sessionID
        return Request.send(host, RequestType.LOG_IN, stream -> {
            stream.writeUTFString(nickname);
            stream.writeUTFString(password);
        });
    }

    public static Response sendLogout(String host, String nickname, String password) { // bool
        return Request.send(host, RequestType.LOG_OUT, stream -> {
            stream.writeUTFString(nickname);
            stream.writeUTFString(password);
        });
    }

    public static Response sendHasSession(String host, UUID uuid) { // bool
        return Request.send(host, RequestType.HAS_SESSION, stream -> stream.writeUUID(uuid));
    }

    public static Response sendGetSessionInfo(String host, UUID uuid) { // nickname, creation_date
        return Request.send(host, RequestType.GET_SESSION_INFO, stream -> stream.writeUUID(uuid));
    }

}
