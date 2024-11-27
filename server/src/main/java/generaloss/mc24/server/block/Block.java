package generaloss.mc24.server.block;

import generaloss.mc24.server.registry.Identifiable;
import generaloss.mc24.server.registry.IntRegistry;

import java.util.*;

public class Block implements Identifiable<String> {

    private String ID;
    private final BlockPropertiesHolder properties;
    private BlockStatesHolder states;

    public Block() {
        this.properties = new BlockPropertiesHolder();
    }

    @Override
    public String getID() {
        return ID;
    }

    public Block setID(String ID) {
        this.ID = ID;
        return this;
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

    public Block createStates(Map<String, StateProperty<?>> stateProperties, IntRegistry<BlockState> blockStateRegistry) {
        states = new BlockStatesHolder(this, stateProperties, blockStateRegistry);
        return this;
    }

}
