package generaloss.mc24.server.block;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class StateProperty<T> {

    private final String name;
    private final Class<?> valueClass;
    private final List<T> allowedValues;

    public StateProperty(String name, Class<?> valueClass) {
        this.name = name;
        this.valueClass = valueClass;
        this.allowedValues = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public Class<?> getValueClass() {
        return valueClass;
    }

    public Collection<T> getAllowedValues() {
        return allowedValues;
    }

}
