package generaloss.mc24.server.chunk;

import org.jetbrains.annotations.NotNull;

import java.util.*;

public class IntIDMap <T> implements Iterable<T>{

    private final Map<T, Integer> idMap;
    private final List<T> list;

    public IntIDMap() {
        this.idMap = new HashMap<>();
        this.list = new ArrayList<>();
    }

    public int getID(T item) {
        return idMap.get(item);
    }

    public void add(T item) {
        final int ID = list.size();
        idMap.put(item, ID);
        list.add(item);
    }

    @Override
    public @NotNull Iterator<T> iterator() {
        return list.iterator();
    }
}
