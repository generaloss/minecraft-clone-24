package generaloss.mc24.server;

import generaloss.mc24.server.property.AbstractProperty;

import java.util.HashMap;
import java.util.Map;

public class ServerProperty extends AbstractProperty {

    private ServerProperty(String name, Object defaultValue) {
        super(name, defaultValue);
    }


    private static final Map<String, ServerProperty> PROPERTIES = new HashMap<>();

    public static void register(String name, Object defaultValue) {
        PROPERTIES.put(name, new ServerProperty(name, defaultValue));
    }

    public static ServerProperty get(String name) {
        return PROPERTIES.get(name);
    }

    static {
        register("port", 0);
        register("motd", Server.DEFAULT_MOTD);
        register("version", Server.VERSION);
        register("dedicated", true);
    }

}
