package generaloss.mc24.server.block;

import generaloss.mc24.server.common.AbstractPropertyHolder;

public class StatePropertyHolder extends AbstractPropertyHolder<StateProperty<?>> {

    public StatePropertyHolder() { }

    public StatePropertyHolder(StatePropertyHolder properties) {
        super(properties);
    }

    @Override
    protected StateProperty<?> getProperty(String name) {
        final StateProperty<?> property = StateProperty.get(name);
        if(property == null)
            System.err.println("BlockState property '" + name + "' is not registered.");
        return property;
    }

    public StatePropertyHolder copy() {
        return new StatePropertyHolder(this);
    }

}
