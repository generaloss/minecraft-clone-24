package generaloss.mc24.server.network.packet2s;

import generaloss.mc24.server.network.protocol.IServerProtocolGame;
import jpize.util.io.ExtDataInputStream;
import jpize.util.io.ExtDataOutputStream;
import jpize.util.net.tcp.packet.IPacket;

import java.io.IOException;

public class SetBlockStatePacket2S extends IPacket<IServerProtocolGame> {

    private int x, y, z;
    private int blockStateID;

    public SetBlockStatePacket2S(int x, int y, int z, int blockStateID) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.blockStateID = blockStateID;
    }

    public SetBlockStatePacket2S() { }


    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public int getBlockStateID() {
        return blockStateID;
    }


    @Override
    public void write(ExtDataOutputStream stream) throws IOException {
        stream.writeInts(x, y, z, blockStateID);
    }

    @Override
    public void read(ExtDataInputStream stream) throws IOException {
        x = stream.readInt();
        y = stream.readInt();
        z = stream.readInt();
        blockStateID = stream.readInt();
    }

    @Override
    public void handle(IServerProtocolGame handler) {
        handler.handleSetBlockState(this);
    }

}