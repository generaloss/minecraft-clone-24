package generaloss.mc24.accountservice;

import generaloss.mc24.accountservice.packet.Packet2CPublicKey;
import generaloss.mc24.accountservice.packet.Packet2SConnectionKey;
import jpize.util.net.tcp.TcpClient;
import jpize.util.net.tcp.TcpConnection;
import jpize.util.net.tcp.packet.PacketDispatcher;
import jpize.util.net.tcp.packet.PacketHandler;
import jpize.util.security.KeyAES;

public class AccountServiceClientConnection implements PacketHandler {

    private final TcpClient tcpClient;
    private final PacketDispatcher packetDispatcher;

    public AccountServiceClientConnection() {
        this.tcpClient = new TcpClient()
            .setOnConnect(this::onConnect)
            .setOnDisconnect(this::onDisconnect)
            .setOnReceive(this::onReceive)
            .connect("arch.webhop.me", 8888);
        this.packetDispatcher = new PacketDispatcher().register(
            Packet2CPublicKey.class
        );
    }

    private void onConnect(TcpConnection tcpConnection) { }

    private void onDisconnect(TcpConnection tcpConnection) { }

    private void onReceive(TcpConnection tcpConnection, byte[] bytes) {
        packetDispatcher.readPacket(bytes, this);
        packetDispatcher.handlePackets();
    }


    public void handlePublicKey(Packet2CPublicKey packet) {
        final KeyAES key = new KeyAES(128);
        final byte[] keyBytes = key.getKey().getEncoded();
        final byte[] encryptedKeyBytes = packet.getPublicKey().encrypt(keyBytes);
        tcpClient.send(new Packet2SConnectionKey(encryptedKeyBytes));
        tcpClient.encode(key);
        System.out.println("[INFO]: Connection encrypted with key " + key.getKey().hashCode());
    }

}
