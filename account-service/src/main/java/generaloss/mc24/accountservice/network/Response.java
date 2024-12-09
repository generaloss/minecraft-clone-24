package generaloss.mc24.accountservice.network;

import jpize.util.io.DataStreamReader;
import jpize.util.io.ExtDataInputStream;

import java.io.IOException;

public class Response {

    private ResponseCode code;
    private byte[] data;

    public Response() {
        this.code = ResponseCode.NO_RESPONSE;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public ResponseCode getCode() {
        return code;
    }

    public void setCode(ResponseCode code) {
        this.code = code;
    }


    public void readData(DataStreamReader streamReader) {
        if(data == null)
            return;
        try(final ExtDataInputStream extStream = new ExtDataInputStream(data)){
            streamReader.read(extStream);
        }catch(IOException ignored) { }
    }

}
