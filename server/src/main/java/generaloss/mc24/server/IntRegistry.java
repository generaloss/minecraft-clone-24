package generaloss.mc24.server;

import java.util.Collection;

public class IntRegistry<Value> {

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

    public int size() {
        return identityMap.size();
    }

    public Collection<Value> getValues() {
        return identityMap.getValues();
    }

}
