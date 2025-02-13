package generaloss.mc24.server.network.connection;

import generaloss.mc24.accountservice.network.Request;
import generaloss.mc24.accountservice.network.Response;
import generaloss.mc24.server.Server;
import generaloss.mc24.server.ServerPropertiesHolder;
import generaloss.mc24.server.SharedConstants;
import generaloss.mc24.server.network.AccountSession;
import generaloss.mc24.server.network.packet2c.*;
import generaloss.mc24.server.network.packet2s.EncodeKeyPacket2S;
import generaloss.mc24.server.network.packet2s.LoginRequestPacket2S;
import generaloss.mc24.server.network.packet2s.ServerInfoRequestPacket2S;
import generaloss.mc24.server.network.packet2s.SessionIDPacket2S;
import generaloss.mc24.server.network.protocol.IServerProtocolLogin;
import jpize.util.net.tcp.TCPConnection;
import jpize.util.security.AESKey;
import jpize.util.security.PrivateRSA;
import jpize.util.security.PublicRSA;

import java.util.UUID;

public class ServerConnectionLogin extends ServerConnection implements IServerProtocolLogin {

    public ServerConnectionLogin(Server server, TCPConnection tcpConnection) {
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
        if(!serverVersion.equals(clientVersion)){
            super.sendPacket(new DisconnectPacket2C("Client version '" + clientVersion + "' does not match server version '" + serverVersion + "'"));
            super.disconnect();
        }

        super.sendPacket(new LoginStatePacket2C("Encrypt connection.."));
        final PublicRSA publicKey = super.server().net().getEncryptionKey().getPublic();
        super.sendPacket(new PublicKeyPacket2C(publicKey));
    }

    @Override
    public void handleConnectionKey(EncodeKeyPacket2S packet) {
        final PrivateRSA privateKey = super.server().net().getEncryptionKey().getPrivate();
        final byte[] keyBytes = privateKey.decrypt(packet.getEncryptedKeyBytes());
        final AESKey key = new AESKey(keyBytes);
        super.encode(key);
    }

    @Override
    public void handleSessionID(SessionIDPacket2S packet) {
        // validate session
        final Response hasSessionResponse = Request.sendHasSession(SharedConstants.ACCOUNTS_HOST, packet.getSessionID());
        if(!hasSessionResponse.getCode().noError() || !hasSessionResponse.readBoolean()){
            super.sendPacket(new DisconnectPacket2C("invalid session"));
            super.disconnect();
            return;
        }

        super.sendPacket(new LoginStatePacket2C("Validate session.."));

        final UUID sessionID = packet.getSessionID();
        final Response sessionInfoResponse = Request.sendGetSessionInfo(SharedConstants.ACCOUNTS_HOST, sessionID);
        if(sessionInfoResponse.getCode().noError()) {
            // set game protocol
            final String username = sessionInfoResponse.readString();
            final AccountSession session = new AccountSession(sessionID, username);

            final ServerConnectionGame gameProtocol = new ServerConnectionGame(super.server(), super.tcpConnection(), session);
            super.setProtocol(gameProtocol);

            super.sendPacket(new InitSessionPacket2C());
        }else{
            super.sendPacket(new DisconnectPacket2C("invalid session"));
            super.disconnect();
        }
    }

}
