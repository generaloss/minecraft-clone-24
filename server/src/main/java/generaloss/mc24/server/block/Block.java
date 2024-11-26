package generaloss.mc24.server.block;

import generaloss.mc24.server.registry.IntRegistry;

import java.util.*;

public class Block {

    private final String ID;
    private final Map<String, StateProperty<?>> propertiesValues;
    private final List<BlockState> states;

    public Block(String ID) {
        this.ID = ID;
        this.propertiesValues = new HashMap<>(); // key1:[r, g, b], key2:[x, y], key3:[a, b, c]
        this.states = new ArrayList<>();
    }

    public String getID() {
        return ID;
    }

    public Block addStateProperty(StateProperty<?> property) {
        propertiesValues.put(property.getName(), property);
        return this;
    }

    public Collection<BlockState> getStates() {
        return states;
    }

    public BlockState getDefaultState() {
        return states.get(0);
    }


    public Block buildStates(IntRegistry<BlockState> blockStateRegistry) {
        final List<StateProperty<?>> properties = new ArrayList<>();
        final List<Collection<?>> values = new ArrayList<>();

        for(StateProperty<?> property: propertiesValues.values()){
            properties.add(property);
            values.add(property.getAllowedValues());
        }

        final List<List<?>> cartesianProduct = cartesianProduct(values);
        for(List<?> products: cartesianProduct){

            final Map<StateProperty<?>, Object> statePropertyValues = new HashMap<>();
            for(int i = 0; i < products.size(); i++){

                final StateProperty<?> property = properties.get(i);
                final Object value = products.get(i);
                statePropertyValues.put(property, value);
            }
            states.add(new BlockState(this, statePropertyValues));
        }

        if(properties.isEmpty())
            states.add(new BlockState(this, Map.of()));

        // register states
        for(BlockState state: states)
            blockStateRegistry.register(state);

        return this;
    }

    private static List<List<?>> cartesianProduct(List<Collection<?>> lists) {
        List<List<?>> result = new ArrayList<>();
        if(lists == null || lists.isEmpty())
            return result;

        result.add(new ArrayList<>());

        // add elements
        for(Collection<?> list: lists){
            final List<List<?>> temp = new ArrayList<>();

            for(List<?> combination: result){
                // add new element
                for(Object element: list){
                    final List<Object> newCombination = new ArrayList<>(combination);
                    newCombination.add(element);
                    temp.add(newCombination);
                }
            }

            // update result
            result = temp;
        }
        return result;
    }

}
