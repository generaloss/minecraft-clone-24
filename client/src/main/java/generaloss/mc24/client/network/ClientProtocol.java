package generaloss.mc24.client.network;

import generaloss.mc24.client.Main;
import generaloss.mc24.client.screen.DisconnectScreen;
import generaloss.mc24.server.network.packet2c.DisconnectPacket2C;
import generaloss.mc24.server.network.protocol.IClientProtocol;
import jpize.app.Jpize;
import jpize.util.net.tcp.TCPConnection;
import jpize.util.net.packet.NetPacket;
import jpize.util.security.AESKey;

public abstract class ClientProtocol implements IClientProtocol {

    private final Main context;
    private final TCPConnection tcpConnection;

    public ClientProtocol(Main context, TCPConnection tcpConnection) {
        this.context = context;
        this.tcpConnection = tcpConnection;
    }

    public Main context() {
        return context;
    }

    public TCPConnection tcpConnection() {
        return tcpConnection;
    }


    public void setProtocol(IClientProtocol protocol) {
        tcpConnection.attach(protocol);
    }

    public boolean sendPacket(NetPacket<?> packet) {
        return tcpConnection.send(packet);
    }

    public void encode(AESKey key) {
        tcpConnection.encode(key);
    }

    public void disconnect() {
        tcpConnection.close();
    }


    @Override
    public void handleDisconnect(DisconnectPacket2C packet) {
        final DisconnectScreen screen = context.screens().get(DisconnectScreen.SCREEN_ID);
        Jpize.syncExecutor().exec(() -> {
            context.disconnectSession();
            screen.setMessage(packet.getMessage());
            screen.setCurrent();
        });
    }

}
