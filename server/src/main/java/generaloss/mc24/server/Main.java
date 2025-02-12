package generaloss.mc24.server;

import generaloss.mc24.server.common.ArgsMap;
import generaloss.mc24.server.common.Config;
import generaloss.mc24.server.resourcepack.ResourcePackManager;
import jpize.util.io.FastReader;
import jpize.util.res.FileResource;
import jpize.util.res.Resource;

public class Main {

    public static void main(String[] args) {
        final ArgsMap argsMap = new ArgsMap(args);

        final Config config = new Config();
        final FileResource configRes = Resource.file("./config.txt");
        if(configRes.exists())
            config.load(configRes);

        config.putIfAbsent("port", 22854);
        config.save(configRes);

        final ResourcePackManager resourcePackManager = new ResourcePackManager();
        final Server server = new Server(resourcePackManager, true);
        server.init();
        server.run(config.getInt("port"));

        final FastReader reader = new FastReader();
        while(!Thread.interrupted() && !server.isClosed()) {
            if(reader.hasNext()){
                final String line = reader.nextLine();
                if(line.equals("quit"))
                    break;
            }
        }
        server.stop();
        reader.close();
    }

}