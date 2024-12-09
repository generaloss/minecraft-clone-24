package generaloss.mc24.accountservice.network.packet;

import generaloss.mc24.accountservice.network.Request;
import generaloss.mc24.accountservice.network.ResponseCode;
import jpize.util.io.ExtDataInputStream;
import jpize.util.io.ExtDataOutputStream;
import jpize.util.net.tcp.packet.IPacket;
import java.io.IOException;

public class ResponsePacket2C extends IPacket<Request> {

    private ResponseCode code;
    private byte[] data;

    public ResponsePacket2C(ResponseCode code, byte[] data) {
        this.code = code;
        this.data = data;
    }

    public ResponsePacket2C() { }

    public ResponseCode getCode() {
        return code;
    }

    public byte[] getData() {
        return data;
    }

    @Override
    public void write(ExtDataOutputStream stream) throws IOException {
        stream.writeByte(code.ordinal());
        stream.writeByteArray(data);
    }

    @Override
    public void read(ExtDataInputStream stream) throws IOException {
        code = ResponseCode.values()[stream.readByte()];
        data = stream.readByteArray();
    }

    @Override
    public void handle(Request handler) {
        handler.handleResponse(this);
    }

}