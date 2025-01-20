package generaloss.mc24.server.entity;

import org.jetbrains.annotations.NotNull;

import java.util.*;

public class EntityContainer implements Iterable<AbstractEntity> {

    private final Map<UUID, AbstractEntity> map;

    public EntityContainer() {
        this.map = new HashMap<>();
    }


    public void insert(AbstractEntity entity) {
        map.put(entity.getUUID(), entity);
    }

    public AbstractEntity get(UUID uuid) {
        return map.get(uuid);
    }

    public void remove(UUID uuid) {
        map.remove(uuid);
    }

    public void clear() {
        map.clear();
    }


    @Override
    public @NotNull Iterator<AbstractEntity> iterator() {
        return map.values().iterator();
    }

}
