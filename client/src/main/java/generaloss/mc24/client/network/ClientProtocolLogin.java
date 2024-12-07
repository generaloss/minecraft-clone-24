package generaloss.mc24.client.network;

import generaloss.mc24.client.Main;
import generaloss.mc24.client.screen.TitleScreen;
import generaloss.mc24.server.network.packet2c.Packet2CPublicKey;
import generaloss.mc24.server.network.packet2c.Packet2CServerInfoResponse;
import generaloss.mc24.server.network.packet2s.Packet2SConnectionKey;
import generaloss.mc24.server.network.packet2s.Packet2SSessionID;
import generaloss.mc24.server.network.protocol.IClientProtocolLogin;
import jpize.util.net.tcp.TcpConnection;
import jpize.util.security.KeyAES;

import java.util.UUID;

public class ClientProtocolLogin extends ClientProtocol implements IClientProtocolLogin {

    public ClientProtocolLogin(Main context, TcpConnection tcpConnection) {
        super(context, tcpConnection);
    }

    @Override
    public void handleServerInfoResponse(Packet2CServerInfoResponse packet) {
        final long ping = (System.currentTimeMillis() - packet.getTimestamp());
        final TitleScreen screen = super.context().screens().get("title");
        screen.onServerInfo(packet.getMotd(), packet.getVersion(), ping);
        super.disconnect();
    }

    @Override
    public void handlePublicKey(Packet2CPublicKey packet) {
        // encode connection
        final KeyAES key = new KeyAES(128);
        final byte[] keyBytes = key.getKey().getEncoded();
        final byte[] encryptedKeyBytes = packet.getPublicKey().encrypt(keyBytes);
        super.sendPacket(new Packet2SConnectionKey(encryptedKeyBytes));
        super.encode(key);
        System.out.println("[INFO]: Client connection encrypted with key " + key.getKey().hashCode());
        // send sessionID
        final UUID sessionID = UUID.randomUUID();
        super.sendPacket(new Packet2SSessionID(sessionID));
    }

}
