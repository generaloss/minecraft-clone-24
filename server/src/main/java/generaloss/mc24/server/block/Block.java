package generaloss.mc24.server.block;

import generaloss.mc24.server.registry.IntRegistry;

import java.util.*;

public class Block {

    private final String ID;
    private final BlockPropertiesHolder properties;
    private final BlockStatesHolder states;

    public Block(String ID, Map<String, StateProperty<?>> stateProperties, IntRegistry<BlockState> blockStateRegistry) {
        this.ID = ID;
        this.properties = new BlockPropertiesHolder();
        this.states = new BlockStatesHolder(this, stateProperties, blockStateRegistry);
    }

    public String getID() {
        return ID;
    }

    public BlockPropertiesHolder properties() {
        return properties;
    }

    public BlockStatesHolder states() {
        return states;
    }

    public BlockState getDefaultState() {
        return states.getDefaultState();
    }

}
