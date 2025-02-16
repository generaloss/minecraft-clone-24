package generaloss.mc24.server.block;

import generaloss.mc24.server.property.AbstractProperty;
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

    public static BlockProperty get(String name) {
        return PROPERTIES.get(name);
    }

    public static BlockProperty register(String name, Object defaultValue, Function<Object, Object> jsonLoader) {
        final BlockProperty property = new BlockProperty(name, defaultValue, jsonLoader);
        PROPERTIES.put(name, property);
        return property;
    }


    public static final BlockProperty OPACITY = register("opacity", 15, object -> object);
    public static final BlockProperty GLOWING = register("glowing", new int[3], object -> {
        final JSONArray array = (JSONArray) object;
        return new int[] { array.getInt(0), array.getInt(1), array.getInt(2) };
    });

}
