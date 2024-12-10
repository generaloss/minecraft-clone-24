package generaloss.mc24.server.network.connection;

import generaloss.mc24.accountservice.network.Request;
import generaloss.mc24.accountservice.network.Response;
import generaloss.mc24.server.Server;
import generaloss.mc24.server.ServerPropertiesHolder;
import generaloss.mc24.server.network.AccountSession;
import generaloss.mc24.server.network.packet2c.DisconnectPacket2C;
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

import java.util.UUID;

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
        final String serverVersion = super.server().properties().getString("version");
        final String clientVersion = packet.getClientVersion();
        if(!serverVersion.equals(clientVersion))
            super.sendPacket(new DisconnectPacket2C("Client version '" + clientVersion + "' does not match server version '" + serverVersion + "'"));

        final PublicRSA publicKey = super.server().net().getEncryptionKey().getPublic();
        super.sendPacket(new PublicKeyPacket2C(publicKey));
    }

    @Override
    public void handleConnectionKey(EncodeKeyPacket2S packet) {
        final PrivateRSA privateKey = super.server().net().getEncryptionKey().getPrivate();
        final byte[] keyBytes = privateKey.decrypt(packet.getEncryptedKeyBytes());
        final KeyAES key = new KeyAES(keyBytes);
        super.encode(key);
    }

    @Override
    public void handleSessionID(SessionIDPacket2S packet) {
        // validate session
        final Response hasSessionResponse = Request.sendHasSession(packet.getSessionID());
        if(!hasSessionResponse.getCode().noError() || !hasSessionResponse.readBoolean()){
            super.sendPacket(new DisconnectPacket2C("session expired"));
        }else{
            final UUID sessionID = packet.getSessionID();
            final Response sessionInfoResponse = Request.sendGetSessionInfo(sessionID);
            if(sessionInfoResponse.getCode().noError()) {
                // set game protocol
                final String nickname = sessionInfoResponse.readString();
                final AccountSession session = new AccountSession();
                session.set(sessionID, nickname);
                super.setProtocol(new ServerConnectionGame(super.server(), super.tcpConnection(), session));
            }else{
                super.sendPacket(new DisconnectPacket2C("session expired"));
            }
        }
    }

}
