package generaloss.mc24.server.block;

public class BlockState {

    private final Block block;
    private final StatePropertyHolder properties;

    public BlockState(Block block, StatePropertyHolder properties) {
        this.block = block;
        this.properties = properties;
    }

    public Block getBlock() {
        return block;
    }

    public StatePropertyHolder getStateProperties(){
        return properties;
    }

    public Object getStatePropertyValue(StateProperty<?> property) {
        return properties.get(property);
    }

    public Object getStatePropertyValue(String propertyName) {
        final StateProperty<?> property = StateProperty.get(propertyName);
        return this.getStatePropertyValue(property);
    }


    public String getBlockID() {
        return block.getID();
    }

    public boolean isBlockID(String... identifiers) {
        for(String identifier: identifiers)
            if(this.getBlockID().equals(identifier))
                return true;
        return false;
    }


    public BlockPropertyHolder getBlockProperties() {
        return block.properties();
    }


    public BlockState withProperty(StateProperty<?> property, Object value) {
        if(property == null)
            return this;
        final StatePropertyHolder modifiedProperties = new StatePropertyHolder(properties);
        modifiedProperties.set(property, value);
        final BlockState modifiedBlockState = block.states().getState(modifiedProperties);
        if(modifiedBlockState == null)
            return this;
        return modifiedBlockState;
    }

    public BlockState withProperty(String propertyName, Object value) {
        return this.withProperty(StateProperty.get(propertyName), value);
    }

}
