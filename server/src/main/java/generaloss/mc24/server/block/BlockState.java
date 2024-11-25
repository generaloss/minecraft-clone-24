package generaloss.mc24.server.block;

import java.util.*;

public class BlockState {

    private final Block block;
    private final Map<AbstractProperty<?>, Object> propertyValuesMap;

    public BlockState(Block block, Map<AbstractProperty<?>, Object> propertyValuesMap) {
        this.block = block;
        this.propertyValuesMap = propertyValuesMap;
    }

    public Block getBlock() {
        return block;
    }

    public Collection<AbstractProperty<?>> getProperties(){
        return propertyValuesMap.keySet();
    }

    public Collection<Object> getPropertyValues() {
        return propertyValuesMap.values();
    }

}
