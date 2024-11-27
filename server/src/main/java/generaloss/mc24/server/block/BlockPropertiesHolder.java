package generaloss.mc24.server.block;

import java.util.HashMap;
import java.util.Map;

public class BlockPropertiesHolder {

    private final Map<BlockProperty, Object> valuesMap;

    public BlockPropertiesHolder() {
        this.valuesMap = new HashMap<>();
        this.setDefaults();
    }

    public BlockPropertiesHolder setDefaults() {
        for(BlockProperty property: BlockProperty.values())
            valuesMap.put(property, property.getDefaultValue());
        return this;
    }


    public BlockPropertiesHolder set(BlockProperty property, Object value) {
        valuesMap.put(property, value);
        return this;
    }

    public BlockPropertiesHolder set(String propertyName, Object value) {
        return this.set(BlockProperty.byName(propertyName), value);
    }


    public Object get(BlockProperty property) {
        return valuesMap.get(property);
    }

    public Object get(String propertyName) {
        return this.get(BlockProperty.byName(propertyName));
    }


    public Float getFloat(BlockProperty property) {
        return (Float) this.get(property);
    }

    public int getInt(BlockProperty property) {
        return (int) this.get(property);
    }

    public String getString(BlockProperty property) {
        return (String) this.get(property);
    }

    public boolean getBool(BlockProperty property) {
        return (boolean) this.get(property);
    }

}
