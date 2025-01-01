package generaloss.mc24.server;

import generaloss.mc24.server.registry.Registries;
import generaloss.mc24.server.resourcepack.ResourcePack;
import jpize.util.io.FastReader;
import jpize.util.res.ExternalResource;
import jpize.util.res.Resource;

public class Main {

    public static void main(String[] args) {
        final ArgsMap argsMap = new ArgsMap(args);

        final Config config = new Config();
        final ExternalResource configRes = Resource.external("./config.txt");
        if(configRes.exists())
            config.load(configRes);

        config.putIfAbsent("port", 22854);
        config.save(configRes);

        final Server server = new Server(new Registries(new ResourcePack("vanilla-pack.zip")), true);
        server.init();
        server.run(config.getInt("port"));

        final FastReader reader = new FastReader();
        while(!Thread.interrupted() && !server.isClosed()) {
            final String line = reader.nextLine();
            if(line.equals("quit"))
                break;
        }
        server.stop();
        reader.close();
    }

}