package generaloss.mc24.accountservice.network.packet;

import generaloss.mc24.accountservice.network.Connection;
import generaloss.mc24.accountservice.network.RequestType;
import jpize.util.io.ExtDataInputStream;
import jpize.util.io.ExtDataOutputStream;
import jpize.util.net.packet.NetPacket;

import java.io.IOException;

public class RequestPacket2S extends NetPacket<Connection> {

    private RequestType type;
    private byte[] data;

    public RequestPacket2S(RequestType type, byte[] data) {
        this.type = type;
        this.data = data;
    }

    public RequestPacket2S() { }

    public RequestType getType() {
        return type;
    }

    public byte[] getData() {
        return data;
    }

    @Override
    public void write(ExtDataOutputStream stream) throws IOException {
        stream.writeByte(type.ordinal());
        stream.writeByteArray(data);
    }

    @Override
    public void read(ExtDataInputStream stream) throws IOException {
        type = RequestType.values()[stream.readByte()];
        data = stream.readByteArray();
    }

    @Override
    public void handle(Connection handler) {
        handler.handleRequest(this);
    }

}