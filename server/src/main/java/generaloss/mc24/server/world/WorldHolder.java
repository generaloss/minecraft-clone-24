package generaloss.mc24.server.world;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class WorldHolder {

    private final Map<String, ServerWorld> map;

    public WorldHolder() {
        this.map = new HashMap<>();
    }

    public Collection<ServerWorld> getWorlds() {
        return map.values();
    }

    public ServerWorld getWorld(String ID) {
        return map.get(ID);
    }

    public void putWorld(ServerWorld world) {
        map.put(world.getID(), world);
    }

    public void removeWorld(ServerWorld world) {
        map.put(world.getID(), world);
    }

}
