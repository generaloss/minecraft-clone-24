package generaloss.mc24.server.block;

import generaloss.mc24.server.registry.ServerRegistries;

import java.util.*;

public class BlockState {

    private final Block block;
    private final Map<StateProperty<?>, Object> stateProperties;

    public BlockState(Block block, Map<StateProperty<?>, Object> stateProperties) {
        this.block = block;
        this.stateProperties = stateProperties;
    }

    public Block getBlock() {
        return block;
    }

    public Map<StateProperty<?>, Object> getStateProperties(){
        return stateProperties;
    }

    public Object getStatePropertyValue(StateProperty<?> property) {
        return stateProperties.get(property);
    }

    public Object getStatePropertyValue(String propertyName) {
        final StateProperty<?> property = ServerRegistries.STATE_PROPERTY.get(propertyName);
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


    public BlockPropertiesHolder blockProperties() {
        return block.properties();
    }


    public BlockState withProperty(StateProperty<?> property, Object value) {
        if(property == null)
            return this;
        final Map<StateProperty<?>, Object> modifiedStateProperties = new HashMap<>(stateProperties);
        modifiedStateProperties.put(property, value);
        final BlockState modifiedBlockState = block.states().getState(modifiedStateProperties);
        if(modifiedBlockState == null)
            return this;
        return modifiedBlockState;
    }

    public BlockState withProperty(String propertyName, Object value) {
        return this.withProperty(ServerRegistries.STATE_PROPERTY.get(propertyName), value);
    }

}
