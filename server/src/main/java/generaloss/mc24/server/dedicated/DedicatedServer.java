package generaloss.mc24.server.dedicated;

import generaloss.mc24.server.Config;
import generaloss.mc24.server.Server;

public class DedicatedServer extends Server {

    public DedicatedServer(Config config) {
        super(config.getInt("port"));
    }

}
