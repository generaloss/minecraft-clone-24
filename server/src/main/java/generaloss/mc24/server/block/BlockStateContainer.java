package generaloss.mc24.server.block;

import generaloss.mc24.server.registry.ServerRegistries;

import java.util.*;

public class BlockStateContainer implements Iterable<BlockState> {

    private final Block block;
    private final List<BlockState> blockstates;

    public BlockStateContainer(Block block) {
        this.block = block;
        this.blockstates = new ArrayList<>();
    }


    public Collection<BlockState> collection() {
        return blockstates;
    }

    public BlockState getDefaultState() {
        return blockstates.get(0);
    }

    public BlockState getState(StatePropertyHolder properties) {
        loop:
        for(BlockState blockstate: blockstates){
            for(Map.Entry<StateProperty<?>, Object> property: properties)
                if(!property.getValue().equals(blockstate.getStatePropertyValue(property.getKey())))
                    continue loop;
            return blockstate;
        }
        return null;
    }

    @Override
    public Iterator<BlockState> iterator() {
        return blockstates.iterator();
    }


    public void create(StateProperty<?>... stateProperties) {
        final List<StateProperty<?>> propertiesList = new ArrayList<>();
        final List<Collection<?>> valuesList = new ArrayList<>();

        for(StateProperty<?> property: stateProperties){
            propertiesList.add(property);
            valuesList.add(property.getAllowedValues());
        }

        final List<List<?>> cartesianProduct = cartesianProduct(valuesList);
        for(List<?> products: cartesianProduct){

            final StatePropertyHolder properties = new StatePropertyHolder();
            for(int i = 0; i < products.size(); i++){

                final StateProperty<?> property = propertiesList.get(i);
                final Object value = products.get(i);
                properties.set(property, value);
            }
            blockstates.add(new BlockState(block, properties));
        }

        if(propertiesList.isEmpty())
            blockstates.add(new BlockState(block, new StatePropertyHolder()));

        // register all blockstates
        for(BlockState state: blockstates)
            ServerRegistries.BLOCK_STATE.register(state);
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
