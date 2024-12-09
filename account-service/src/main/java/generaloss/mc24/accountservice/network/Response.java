package generaloss.mc24.accountservice.network;

import jpize.util.Utils;
import jpize.util.io.DataStreamReader;
import jpize.util.io.ExtDataInputStream;

import java.io.IOException;
import java.util.UUID;

public class Response {

    private ResponseCode code;
    private byte[] data;
    private ExtDataInputStream readStream;

    public Response() {
        this.code = ResponseCode.NO_RESPONSE;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
        if(data != null)
            readStream = new ExtDataInputStream(data);
    }

    public ResponseCode getCode() {
        return code;
    }

    public void setCode(ResponseCode code) {
        this.code = code;
    }


    public void readData(DataStreamReader streamReader) {
        if(readStream == null)
            return;
        try{
            streamReader.read(readStream);
        }catch(IOException ignored) { }
    }

    public String readString() {
        try{
            return readStream.readStringUTF();
        }catch(Exception ignored) {
            return null;
        }
    }

    public UUID readUUID() {
        try{
            return readStream.readUUID();
        }catch(Exception ignored) {
            return null;
        }
    }

    public Boolean readBoolean() {
        try{
            return readStream.readBoolean();
        }catch(Exception ignored) {
            return null;
        }
    }

    public void close() {
        Utils.close(readStream);
    }

}
