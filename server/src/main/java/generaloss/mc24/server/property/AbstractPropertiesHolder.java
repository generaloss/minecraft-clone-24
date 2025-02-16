package generaloss.mc24.server.property;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractPropertiesHolder<T extends AbstractProperty> {

    private final Map<T, Object> values;

    public AbstractPropertiesHolder() {
        this.values = new HashMap<>();
    }

    protected abstract T getProperty(String name);


    public AbstractPropertiesHolder<T> set(T property, Object value) {
        values.put(property, value);
        return this;
    }

    public AbstractPropertiesHolder<T> set(String name, Object value) {
        return this.set(this.getProperty(name), value);
    }


    public <O> O get(T property) {
        if(property == null)
            return null;
        return (O) values.getOrDefault(property, property.getDefaultValue());
    }

    public <O> O get(String name) {
        final T property = this.getProperty(name);
        return this.get(property);
    }

}
