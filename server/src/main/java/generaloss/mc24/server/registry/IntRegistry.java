package generaloss.mc24.server.registry;

import generaloss.mc24.server.block.ObjectIntMap;

public class IntRegistry <T> {

    private final ObjectIntMap<T> identityMap;

    public IntRegistry() {
        this.identityMap = new ObjectIntMap<>();
    }

    public void register(T object) {
        identityMap.add(object);
    }

    public T get(int ID) {
        return identityMap.getByID(ID);
    }

    public int getID(T object) {
        final int ID = identityMap.getID(object);
        return (ID == -1 ? 0 : ID);
    }

}
