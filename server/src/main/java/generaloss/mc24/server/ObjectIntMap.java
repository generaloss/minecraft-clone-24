package generaloss.mc24.server;

import org.jetbrains.annotations.NotNull;

import java.util.*;

public class ObjectIntMap<T> implements Iterable<T> {

    private final IdentityHashMap<T, Integer> identityMap;
    private final List<T> objectList;
    private int nextID;

    public ObjectIntMap(int expectedSize) {
        this.identityMap = new IdentityHashMap<>(expectedSize);
        this.objectList = new ArrayList<>(expectedSize);
    }

    public ObjectIntMap() {
        this(512);
    }

    public int size() {
        return identityMap.size();
    }

    private void put(T object, int ID) {
        // fill with nulls
        while(objectList.size() <= ID)
            objectList.add(null);

        // put
        identityMap.put(object, ID);
        objectList.set(ID, object);

        // increase next ID
        if(nextID <= ID)
            nextID = (ID + 1);
    }

    public void add(T object) {
        this.put(object, nextID);
    }

    public int getID(T object) {
        final Integer ID = identityMap.get(object);
        return (ID == null ? -1 : ID);
    }

    public final T getByID(int ID) {
        if(ID < 0 || ID >= objectList.size())
            return null;

        return objectList.get(ID);
    }

    public Collection<T> getValues() {
        return objectList;
    }

    @Override
    public @NotNull Iterator<T> iterator() {
        return objectList.iterator();
    }

}
