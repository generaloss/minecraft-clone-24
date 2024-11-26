package generaloss.mc24.server.registry;

import generaloss.mc24.server.ObjectIntMap;

public class IntRegistry <Value> {

    private final ObjectIntMap<Value> identityMap;

    public IntRegistry() {
        this.identityMap = new ObjectIntMap<>();
    }

    public Value register(Value object) {
        identityMap.add(object);
        return object;
    }

    public Value get(int ID) {
        return identityMap.getByID(ID);
    }

    public int getID(Value object) {
        final int ID = identityMap.getID(object);
        return (ID == -1 ? 0 : ID);
    }

}
