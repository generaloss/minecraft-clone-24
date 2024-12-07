package generaloss.mc24.accountservice;

import generaloss.mc24.accountservice.packet.Packet2SConnectionKey;
import jpize.util.net.tcp.TcpConnection;
import jpize.util.net.tcp.packet.PacketHandler;
import jpize.util.security.KeyAES;
import jpize.util.security.KeyRSA;
import jpize.util.security.PrivateRSA;

public class AccountServiceServerConnection implements PacketHandler {

    private final TcpConnection tcpConnection;
    private final KeyRSA key;

    public AccountServiceServerConnection(TcpConnection tcpConnection, KeyRSA key) {
        this.tcpConnection = tcpConnection;
        this.key = key;
    }

    public void handleConnectionKey(Packet2SConnectionKey packet) {
        final PrivateRSA privateKey = key.getPrivate();
        final byte[] keyBytes = privateKey.decrypt(packet.getEncryptedKeyBytes());
        final KeyAES key = new KeyAES(keyBytes);
        tcpConnection.encode(key);
        System.out.println("[INFO]: Connection encrypted with key " + key.getKey().hashCode());
    }

}
