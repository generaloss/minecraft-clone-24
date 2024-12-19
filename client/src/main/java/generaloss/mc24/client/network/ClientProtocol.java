package generaloss.mc24.client.network;

import generaloss.mc24.client.Main;
import generaloss.mc24.client.screen.TitleScreen;
import generaloss.mc24.server.network.packet2c.DisconnectPacket2C;
import generaloss.mc24.server.network.protocol.IClientProtocol;
import jpize.util.net.tcp.TCPConnection;
import jpize.util.net.tcp.packet.IPacket;
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

    public void disconnect() {
        tcpConnection.close();
    }

    public void sendPacket(IPacket<?> packet) {
        tcpConnection.send(packet);
    }

    public void encode(AESKey key) {
        tcpConnection.encode(key);
    }


    public void handleDisconnect(DisconnectPacket2C packet) {
        tcpConnection.close();
        final TitleScreen screen = context.screens().get("title");
        screen.onDisconnect(packet.getMessage());
    }

}
