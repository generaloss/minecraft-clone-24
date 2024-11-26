package generaloss.mc24.server.registry;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Registry <Key, Value> implements Iterable<Value> {

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

    public Value get(Key ID) {
        if(!map.containsKey(ID))
            throw new IllegalStateException("Block with ID '" + ID + "' not loaded.");

        return map.get(ID);
    }

    @Override
    public @NotNull Iterator<Value> iterator() {
        return map.values().iterator();
    }
}
