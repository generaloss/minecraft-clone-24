package generaloss.mc24.server.block;

import java.util.*;

public class BlockState {

    private final Block block;
    private final Map<StateProperty<?>, Object> propertyValues;

    public BlockState(Block block, Map<StateProperty<?>, Object> propertyValues) {
        this.block = block;
        this.propertyValues = propertyValues;
    }

    public Block getBlock() {
        return block;
    }

    public Collection<StateProperty<?>> getProperties(){
        return propertyValues.keySet();
    }

    public Collection<Object> getPropertyValues() {
        return propertyValues.values();
    }


    public String getBlockID() {
        return block.getID();
    }

    public boolean isBlockID(String string) {
        return this.getBlockID().equals(string);
    }


    public BlockPropertiesHolder properties() {
        return block.properties();
    }

}
