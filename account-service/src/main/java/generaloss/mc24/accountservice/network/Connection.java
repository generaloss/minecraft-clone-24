package generaloss.mc24.accountservice.network;

import generaloss.mc24.accountservice.network.packet.ResponsePacket2C;
import generaloss.mc24.accountservice.network.packet.EncodeKeyPacket2S;
import generaloss.mc24.accountservice.network.packet.RequestPacket2S;
import jpize.util.Utils;
import jpize.util.io.DataStreamWriter;
import jpize.util.io.ExtDataInputStream;
import jpize.util.net.tcp.TCPConnection;
import jpize.util.security.AESKey;
import jpize.util.security.PrivateRSA;

import java.util.UUID;

public class Connection {

    private final RequestListener requestListener;
    private final TCPConnection tcpConnection;

    protected Connection(RequestListener requestListener, TCPConnection tcpConnection) {
        this.requestListener = requestListener;
        this.tcpConnection = tcpConnection;
    }

    public void sendResponse(ResponseCode code, byte[] response) {
        tcpConnection.send(new ResponsePacket2C(code, response));
    }

    public void sendResponse(ResponseCode code, DataStreamWriter dataFactory) {
        this.sendResponse(code, DataStreamWriter.writeBytes(dataFactory));
    }

    public void sendResponse(ResponseCode code, boolean response) {
        this.sendResponse(code, stream -> stream.writeBoolean(response));
    }

    public void sendResponse(ResponseCode code, String response) {
        this.sendResponse(code, stream -> stream.writeUTFString(response));
    }

    public void sendResponse(ResponseCode code, UUID response) {
        this.sendResponse(code, stream -> stream.writeUUID(response));
    }

    public void handleConnectionKey(EncodeKeyPacket2S packet) {
        final PrivateRSA privateKey = requestListener.getKey().getPrivate();
        final byte[] keyBytes = privateKey.decrypt(packet.getEncryptedKeyBytes());
        final AESKey key = new AESKey(keyBytes);
        tcpConnection.encode(key);
    }

    public void handleRequest(RequestPacket2S packet) {
        final ExtDataInputStream extStream = new ExtDataInputStream(packet.getData());
        requestListener.onRequest(this, packet.getType(), extStream);
        Utils.close(extStream);
    }

}
