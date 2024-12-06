package generaloss.mc24.client.network;

import generaloss.mc24.client.Main;
import generaloss.mc24.client.screen.TitleScreen;
import generaloss.mc24.server.network.packet2c.Packet2CServerInfoResponse;
import generaloss.mc24.server.network.protocol.ClientPacketHandlerLogin;

public class ClientProtocolLogin extends ClientProtocol implements ClientPacketHandlerLogin {

    public ClientProtocolLogin(Main context) {
        super(context);
    }

    @Override
    public void onServerInfoResponse(Packet2CServerInfoResponse packet) {
        final double ping = (System.currentTimeMillis() - packet.getTimestamp()) / 1000F;
        final TitleScreen screen = context.screens().get("title");
        screen.onServerInfo(packet.getMotd(), packet.getVersion(), ping);
        super.disconnect();
    }

}
