package generaloss.mc24.server.block;

import jpize.util.Utils;
import jpize.util.math.vector.Vec3i;
import org.json.JSONArray;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public enum BlockProperty {

    OPACITY ("opacity", 15, object -> object),
    GLOWING ("glowing", new Vec3i(), object -> {
        final JSONArray array = (JSONArray) object;
        return new Vec3i(array.getInt(0), array.getInt(1), array.getInt(2));
    });

    private final String name;
    private final Object defaultValue;
    private final Function<Object, Object> jsonLoader;

    BlockProperty(String name, Object defaultValue, Function<Object, Object> jsonLoader) {
        this.name = name;
        this.defaultValue = defaultValue;
        this.jsonLoader = jsonLoader;
    }

    public String getName() {
        return name;
    }

    public Object getDefaultValue() {
        return defaultValue;
    }

    public Object loadFromJSON(Object object) {
        return jsonLoader.apply(object);
    }


    public static BlockProperty byName(String name) {
        return BY_NAME.get(name);
    }

    private static final Map<String, BlockProperty> BY_NAME = Utils.make(new HashMap<>(), map -> {
        for(BlockProperty value : values())
            map.put(value.getName(), value);
    });

}
