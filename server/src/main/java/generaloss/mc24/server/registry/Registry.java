package generaloss.mc24.server.registry;

import java.util.HashMap;
import java.util.Map;

public class Registry <K, E extends RegistryElement<K>> {

    private final Map<K, E> map;

    public Registry() {
        this.map = new HashMap<>();
    }

    public void register(E value) {
        map.put(value.getKey(), value);
    }

    public E get(K key) {
        return map.get(key);
    }

}
