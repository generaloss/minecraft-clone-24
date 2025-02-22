package generaloss.mc24.server;

import generaloss.mc24.server.common.AbstractPropertyHolder;

public class ServerPropertyHolder extends AbstractPropertyHolder<ServerProperty> {

    public ServerPropertyHolder() { }

    public ServerPropertyHolder(ServerPropertyHolder properties) {
        super(properties);
    }

    @Override
    protected ServerProperty getProperty(String name) {
        final ServerProperty property = ServerProperty.get(name);
        if(property == null)
            System.err.println("[ERROR]: Server property '" + name + "' is not registered.");
        return property;
    }

    public ServerPropertyHolder copy() {
        return new ServerPropertyHolder(this);
    }

}
