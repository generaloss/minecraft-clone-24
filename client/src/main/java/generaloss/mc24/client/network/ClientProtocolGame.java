package generaloss.mc24.client.network;

import generaloss.mc24.client.Main;
import generaloss.mc24.server.network.protocol.IClientProtocolGame;
import jpize.util.net.tcp.TcpConnection;

public class ClientProtocolGame extends ClientProtocol implements IClientProtocolGame {

    public ClientProtocolGame(Main context, TcpConnection tcpConnection) {
        super(context, tcpConnection);
    }

}
