package generaloss.mc24.server.registry;

import generaloss.mc24.server.block.StateProperty;

public class RegistryStateProperty extends Registry<String, StateProperty<?>> {

    public <P> StateProperty<P> get(String ID) {
        return (StateProperty<P>) super.getValue(ID);
    }

    public StateProperty<?> register(StateProperty<?> stateProperty) {
        return super.register(stateProperty.getName(), stateProperty);
    }

}
