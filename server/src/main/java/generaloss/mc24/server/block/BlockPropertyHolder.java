package generaloss.mc24.server.block;

import generaloss.mc24.server.common.AbstractPropertyHolder;

public class BlockPropertyHolder extends AbstractPropertyHolder<BlockProperty> {

    public BlockPropertyHolder() { }

    public BlockPropertyHolder(BlockPropertyHolder properties) {
        super(properties);
    }

    @Override
    protected BlockProperty getProperty(String name) {
        final BlockProperty property = BlockProperty.get(name);
        if(property == null)
            System.err.println("Block property '" + name + "' is not registered.");
        return property;
    }

    public BlockPropertyHolder copy() {
        return new BlockPropertyHolder(this);
    }

}
