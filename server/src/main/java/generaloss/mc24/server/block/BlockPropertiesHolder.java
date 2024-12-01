package generaloss.mc24.server.block;

import jpize.util.math.vector.Vec3i;

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

    public Float getFloat(String propertyName) {
        return (Float) this.get(propertyName);
    }


    public int getInt(BlockProperty property) {
        return (int) this.get(property);
    }

    public int getInt(String propertyName) {
        return (int) this.get(propertyName);
    }


    public String getString(BlockProperty property) {
        return (String) this.get(property);
    }

    public String getString(String propertyName) {
        return (String) this.get(propertyName);
    }


    public boolean getBool(BlockProperty property) {
        return (boolean) this.get(property);
    }

    public boolean getBool(String propertyName) {
        return (boolean) this.get(propertyName);
    }


    public Vec3i getVec3i(BlockProperty property) {
        return (Vec3i) this.get(property);
    }

    public Vec3i getVec3i(String propertyName) {
        return (Vec3i) this.get(propertyName);
    }

}
