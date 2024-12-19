package generaloss.mc24.client.network;

import generaloss.mc24.client.Main;
import generaloss.mc24.client.screen.TitleScreen;
import generaloss.mc24.server.network.packet2c.PublicKeyPacket2C;
import generaloss.mc24.server.network.packet2c.ServerInfoResponsePacket2C;
import generaloss.mc24.server.network.packet2s.EncodeKeyPacket2S;
import generaloss.mc24.server.network.packet2s.SessionIDPacket2S;
import generaloss.mc24.server.network.protocol.IClientProtocolLogin;
import jpize.util.net.tcp.TCPConnection;
import jpize.util.security.AESKey;

import java.util.UUID;

public class ClientProtocolLogin extends ClientProtocol implements IClientProtocolLogin {

    public ClientProtocolLogin(Main context, TCPConnection tcpConnection) {
        super(context, tcpConnection);
    }

    @Override
    public void handleServerInfoResponse(ServerInfoResponsePacket2C packet) {
        final long ping = (System.currentTimeMillis() - packet.getTimestamp());
        final TitleScreen screen = super.context().screens().get("title");
        screen.onServerInfo(packet.getMotd(), packet.getVersion(), ping);
        super.disconnect();
    }

    @Override
    public void handlePublicKey(PublicKeyPacket2C packet) {
        // encode connection
        final AESKey key = new AESKey(128);
        final byte[] keyBytes = key.getKey().getEncoded();
        final byte[] encryptedKeyBytes = packet.getPublicKey().encrypt(keyBytes);
        super.sendPacket(new EncodeKeyPacket2S(encryptedKeyBytes));
        super.encode(key);
        // send sessionID
        final UUID sessionID = super.context().session().getID();
        super.sendPacket(new SessionIDPacket2S(sessionID == null ? UUID.randomUUID() : sessionID));
    }

}
