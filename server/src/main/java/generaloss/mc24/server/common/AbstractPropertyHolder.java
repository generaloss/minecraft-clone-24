package generaloss.mc24.server.common;

import java.util.*;

public abstract class AbstractPropertyHolder<T extends AbstractProperty> implements Iterable<Map.Entry<T, Object>> {

    private final Map<T, Object> map;

    public AbstractPropertyHolder() {
        this.map = new LinkedHashMap<>(); // in blockstate properties it's important to save a values order
    }

    public AbstractPropertyHolder(AbstractPropertyHolder<T> propertyHolder) {
        this.map = new HashMap<>(propertyHolder.map);
    }

    public Set<T> keySet() {
        return map.keySet();
    }

    public Collection<Object> values() {
        return map.values();
    }


    protected abstract T getProperty(String name);


    public AbstractPropertyHolder<T> set(T property, Object value) {
        map.put(property, value);
        return this;
    }

    public AbstractPropertyHolder<T> set(String name, Object value) {
        return this.set(this.getProperty(name), value);
    }


    public <O> O get(T property) {
        if(property == null)
            return null;
        return (O) map.getOrDefault(property, property.getDefaultValue());
    }

    public <O> O get(String name) {
        final T property = this.getProperty(name);
        return this.get(property);
    }

    public <O> O get(T property, O customDefaultValue) {
        if(property == null)
            return null;
        return (O) map.getOrDefault(property, customDefaultValue);
    }

    public <O> O get(String name, O customDefaultValue) {
        final T property = this.getProperty(name);
        return this.get(property, customDefaultValue);
    }


    @Override
    public Iterator<Map.Entry<T, Object>> iterator() {
        return map.entrySet().iterator();
    }

}
