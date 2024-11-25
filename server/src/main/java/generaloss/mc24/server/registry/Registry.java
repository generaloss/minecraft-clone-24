package generaloss.mc24.server.registry;

import java.util.HashMap;
import java.util.Map;

public class Registry<K, V> {

    private final Map<K, V> map;

    public Registry() {
        this.map = new HashMap<>();
    }

    public void register(K ID, V object) {
        map.put(ID, object);
    }

    public V get(K ID) {
        return map.get(ID);
    }

}
