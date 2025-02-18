package generaloss.mc24.server.block;

import generaloss.mc24.server.common.AbstractPropertiesHolder;

public class BlockPropertiesHolder extends AbstractPropertiesHolder<BlockProperty> {

    @Override
    protected BlockProperty getProperty(String name) {
        final BlockProperty property = BlockProperty.get(name);
        if(property == null)
            System.err.println("Block property '" + name + "' not registered.");
        return property;
    }

}
