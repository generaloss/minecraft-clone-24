package generaloss.mc24.client.network;

import generaloss.mc24.client.Main;
import jpize.util.net.tcp.TcpClient;

public class Connection {

    private final TcpClient tcpClient;

    public Connection(Main context) {
        this.tcpClient = new TcpClient();
    }

    public void connect(String host, int port) {
        tcpClient.connect(host, port);
    }

}
