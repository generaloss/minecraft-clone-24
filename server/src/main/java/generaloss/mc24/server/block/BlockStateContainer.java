package generaloss.mc24.server.block;

import generaloss.mc24.server.registry.Registries;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class BlockStateContainer implements Iterable<BlockState> {

    private final Block block;
    private final List<BlockState> states;

    public BlockStateContainer(Block block) {
        this.block = block;
        this.states = new ArrayList<>();
    }


    public Collection<BlockState> collection() {
        return states;
    }

    public BlockState getDefaultState() {
        return states.get(0);
    }

    @Override
    public @NotNull Iterator<BlockState> iterator() {
        return states.iterator();
    }


    public void create(Map<String, StateProperty<?>> properties, Registries registries) {
        final List<StateProperty<?>> propertiesList = new ArrayList<>();
        final List<Collection<?>> valuesList = new ArrayList<>();

        for(StateProperty<?> property: properties.values()){
            propertiesList.add(property);
            valuesList.add(property.getAllowedValues());
        }

        final List<List<?>> cartesianProduct = cartesianProduct(valuesList);
        for(List<?> products: cartesianProduct){

            final Map<StateProperty<?>, Object> statePropertyValues = new HashMap<>();
            for(int i = 0; i < products.size(); i++){

                final StateProperty<?> property = propertiesList.get(i);
                final Object value = products.get(i);
                statePropertyValues.put(property, value);
            }
            states.add(new BlockState(block, statePropertyValues));
        }

        if(propertiesList.isEmpty())
            states.add(new BlockState(block, Map.of()));

        // register states
        for(BlockState state: states)
            registries.BLOCK_STATES.register(state);
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
