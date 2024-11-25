package generaloss.mc24.server.dedicated;

import generaloss.mc24.server.ArgsMap;
import generaloss.mc24.server.Config;
import generaloss.mc24.server.Server;
import generaloss.mc24.server.registry.Registries;
import jpize.util.res.ExternalResource;
import jpize.util.res.Resource;
import jpize.util.time.TimeUtils;

public class Main {

    public static void main(String[] args) {
        final ArgsMap argsMap = new ArgsMap(args);

        final Config config = new Config();
        final ExternalResource configRes = Resource.external("./config.txt");
        if(configRes.exists())
            config.load(configRes);

        config.putIfAbsent("port", 22854);
        config.save(configRes);

        final Server server = new Server(new Registries());
        server.run(config.getInt("port"));
        TimeUtils.waitFor(server.tcpServer()::isClosed);
    }

}