package generaloss.mc24.accountservice.network;

import generaloss.mc24.accountservice.network.packet.ResponsePacket2C;
import generaloss.mc24.accountservice.network.packet.EncodeKeyPacket2S;
import generaloss.mc24.accountservice.network.packet.RequestPacket2S;
import jpize.util.Utils;
import jpize.util.io.DataStreamWriter;
import jpize.util.io.ExtDataInputStream;
import jpize.util.net.tcp.TcpConnection;
import jpize.util.net.tcp.packet.PacketHandler;
import jpize.util.security.KeyAES;
import jpize.util.security.PrivateRSA;

import java.util.UUID;

public class Connection implements PacketHandler {

    private final RequestListener requestListener;
    private final TcpConnection tcpConnection;

    protected Connection(RequestListener requestListener, TcpConnection tcpConnection) {
        this.requestListener = requestListener;
        this.tcpConnection = tcpConnection;
    }

    public void sendResponse(ResponseCode code, byte[] response) {
        tcpConnection.send(new ResponsePacket2C(code, response));
    }

    public void sendResponse(ResponseCode code, DataStreamWriter dataFactory) {
        tcpConnection.send(new ResponsePacket2C(code, DataStreamWriter.writeBytes(dataFactory)));
    }

    public void sendResponse(ResponseCode code, boolean response) {
        this.sendResponse(code, stream -> stream.writeBoolean(response));
    }

    public void sendResponse(ResponseCode code, String response) {
        this.sendResponse(code, stream -> stream.writeStringUTF(response));
    }

    public void sendResponse(ResponseCode code, UUID response) {
        this.sendResponse(code, stream -> stream.writeUUID(response));
    }

    public void handleConnectionKey(EncodeKeyPacket2S packet) {
        final PrivateRSA privateKey = requestListener.getKey().getPrivate();
        final byte[] keyBytes = privateKey.decrypt(packet.getEncryptedKeyBytes());
        final KeyAES key = new KeyAES(keyBytes);
        tcpConnection.encode(key);
    }

    public void handleRequest(RequestPacket2S packet) {
        final ExtDataInputStream extStream = new ExtDataInputStream(packet.getData());
        requestListener.onRequest(this, packet.getType(), extStream);
        Utils.close(extStream);
    }

}
