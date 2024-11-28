package generaloss.mc24.server.block;

import jpize.util.Utils;

import java.util.HashMap;
import java.util.Map;

public enum BlockProperty {

    OPACITY ("opacity", 15);

    private final String name;
    private final Object defaultValue;

    BlockProperty(String name, Object defaultValue) {
        this.name = name;
        this.defaultValue = defaultValue;
    }

    public String getName() {
        return name;
    }

    public Object getDefaultValue() {
        return defaultValue;
    }


    public static BlockProperty byName(String name) {
        return BY_NAME.get(name);
    }

    private static final Map<String, BlockProperty> BY_NAME = Utils.make(new HashMap<>(), map -> {
        for(BlockProperty value : values())
            map.put(value.getName(), value);
    });

}
