package generaloss.mc24.server.network.connection;

import generaloss.mc24.server.Server;
import generaloss.mc24.server.ServerPropertiesHolder;
import generaloss.mc24.server.network.packet2c.PublicKeyPacket2C;
import generaloss.mc24.server.network.packet2c.ServerInfoResponsePacket2C;
import generaloss.mc24.server.network.packet2s.EncodeKeyPacket2S;
import generaloss.mc24.server.network.packet2s.LoginRequestPacket2S;
import generaloss.mc24.server.network.packet2s.ServerInfoRequestPacket2S;
import generaloss.mc24.server.network.packet2s.SessionIDPacket2S;
import generaloss.mc24.server.network.protocol.IServerProtocolLogin;
import jpize.util.net.tcp.TcpConnection;
import jpize.util.security.KeyAES;
import jpize.util.security.PrivateRSA;
import jpize.util.security.PublicRSA;

public class ServerConnectionLogin extends ServerConnection implements IServerProtocolLogin {

    public ServerConnectionLogin(Server server, TcpConnection tcpConnection) {
        super(server, tcpConnection);
    }


    @Deprecated
    public void handleServerInfoRequest(ServerInfoRequestPacket2S packet) {
        final ServerPropertiesHolder serverProperties = super.server().properties();
        super.sendPacket(new ServerInfoResponsePacket2C(
            serverProperties.getString("motd"),
            serverProperties.getString("version"),
            packet.getTimestamp()
        ));
    }

    @Override
    public void handleLoginRequest(LoginRequestPacket2S packet) {
        final PublicRSA publicKey = super.server().net().getEncryptionKey().getPublic();
        super.sendPacket(new PublicKeyPacket2C(publicKey));
    }

    @Override
    public void handleConnectionKey(EncodeKeyPacket2S packet) {
        final PrivateRSA privateKey = super.server().net().getEncryptionKey().getPrivate();
        final byte[] keyBytes = privateKey.decrypt(packet.getEncryptedKeyBytes());
        final KeyAES key = new KeyAES(keyBytes);
        super.encode(key);
        System.out.println("[INFO]: Server connection encrypted with key " + key.getKey().hashCode());
    }

    @Override
    public void handleSessionID(SessionIDPacket2S packet) {
        //! validate session UUID

    }

}
