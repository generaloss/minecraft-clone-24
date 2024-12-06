package generaloss.mc24.server;

import generaloss.mc24.server.properties.AbstractPropertiesHolder;

public class ServerPropertiesHolder extends AbstractPropertiesHolder<ServerProperty> {

    @Override
    protected ServerProperty getProperty(String name) {
        final ServerProperty property = ServerProperty.get(name);
        if(property == null)
            System.err.println("Server property '" + name + "' not registered.");
        return property;
    }

}
