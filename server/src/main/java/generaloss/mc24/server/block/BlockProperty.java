package generaloss.mc24.server.block;

import generaloss.mc24.server.property.AbstractProperty;
import jpize.util.math.vector.Vec3i;
import org.json.JSONArray;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class BlockProperty extends AbstractProperty {

    private final Function<Object, Object> jsonLoader;

    private BlockProperty(String name, Object defaultValue, Function<Object, Object> jsonLoader) {
        super(name, defaultValue);
        this.jsonLoader = jsonLoader;
    }

    public Object loadFromJSON(Object object) {
        return jsonLoader.apply(object);
    }


    private static final Map<String, BlockProperty> PROPERTIES = new HashMap<>();

    public static void register(String name, Object defaultValue, Function<Object, Object> jsonLoader) {
        PROPERTIES.put(name, new BlockProperty(name, defaultValue, jsonLoader));
    }

    public static BlockProperty get(String name) {
        return PROPERTIES.get(name);
    }

    static {
        register("opacity", 15, object -> object);
        register("glowing", new Vec3i(), object -> {
            final JSONArray array = (JSONArray) object;
            return new Vec3i(array.getInt(0), array.getInt(1), array.getInt(2));
        });
    }

}
