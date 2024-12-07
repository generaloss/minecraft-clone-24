package generaloss.mc24.server.network.connection;

import generaloss.mc24.server.Server;
import generaloss.mc24.server.ServerPropertiesHolder;
import generaloss.mc24.server.network.packet2c.Packet2CPublicKey;
import generaloss.mc24.server.network.packet2c.Packet2CServerInfoResponse;
import generaloss.mc24.server.network.packet2s.Packet2SConnectionKey;
import generaloss.mc24.server.network.packet2s.Packet2SLoginRequest;
import generaloss.mc24.server.network.packet2s.Packet2SServerInfoRequest;
import generaloss.mc24.server.network.packet2s.Packet2SSessionID;
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
    public void handleServerInfoRequest(Packet2SServerInfoRequest packet) {
        final ServerPropertiesHolder serverProperties = super.server().properties();
        super.sendPacket(new Packet2CServerInfoResponse(
            serverProperties.getString("motd"),
            serverProperties.getString("version"),
            packet.getTimestamp()
        ));
    }

    @Override
    public void handleLoginRequest(Packet2SLoginRequest packet) {
        final PublicRSA publicKey = super.server().connections().getEncryptionKey().getPublic();
        super.sendPacket(new Packet2CPublicKey(publicKey));
    }

    @Override
    public void handleConnectionKey(Packet2SConnectionKey packet) {
        final PrivateRSA privateKey = super.server().connections().getEncryptionKey().getPrivate();
        final byte[] keyBytes = privateKey.decrypt(packet.getEncryptedKeyBytes());
        final KeyAES key = new KeyAES(keyBytes);
        super.encode(key);
        System.out.println("[INFO]: Server connection encrypted with key " + key.getKey().hashCode());
    }

    @Override
    public void handleSessionID(Packet2SSessionID packet) {
        //! validate session UUID

    }

}
