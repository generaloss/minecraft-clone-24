package generaloss.mc24.server.registry;

import generaloss.mc24.server.Identifiable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Registry<Key, Value> implements Iterable<Value> {

    private final Map<Key, Value> map;

    public Registry() {
        this.map = new HashMap<>();
    }

    public Value register(Key key, Value object) {
        map.put(key, object);
        return object;
    }

    public <V extends Identifiable<Key>> Value register(V object) {
        return this.register(object.getID(), (Value) object);
    }

    public Value getValue(Key ID) {
        return map.get(ID);
    }

    public Collection<Value> getValues() {
        return map.values();
    }

    @Override
    public Iterator<Value> iterator() {
        return map.values().iterator();
    }

}
