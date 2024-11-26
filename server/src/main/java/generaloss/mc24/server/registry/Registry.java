package generaloss.mc24.server.registry;

import java.util.HashMap;
import java.util.Map;

public class Registry <Key, Value> {

    private final Map<Key, Value> map;

    public Registry() {
        this.map = new HashMap<>();
    }

    public <K extends Key, V extends Value> Registry<K, V> register(Key ID, Value object) {
        map.put(ID, object);
        return (Registry<K, V>) this;
    }

    public Value get(Key ID) {
        if(!map.containsKey(ID))
            throw new IllegalStateException("Block with ID '" + ID + "' not loaded.");

        return map.get(ID);
    }

}
