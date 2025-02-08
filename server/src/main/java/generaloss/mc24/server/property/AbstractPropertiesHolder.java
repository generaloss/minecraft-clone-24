package generaloss.mc24.server.property;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractPropertiesHolder<T extends AbstractProperty> {

    private final Map<T, Object> values;

    public AbstractPropertiesHolder() {
        this.values = new HashMap<>();
    }

    protected abstract T getProperty(String name);


    public AbstractPropertiesHolder<T> set(String name, Object value) {
        values.put(this.getProperty(name), value);
        return this;
    }

    public Object get(String name) {
        final T property = this.getProperty(name);
        return values.getOrDefault(property, property.getDefaultValue());
    }


    public byte getByte(String name) {
        return (byte) this.get(name);
    }

    public short getShort(String name) {
        return (short) this.get(name);
    }

    public int getInt(String name) {
        return (int) this.get(name);
    }
    
    public long getLong(String name) {
        return (long) this.get(name);
    }

    public float getFloat(String name) {
        return (float) this.get(name);
    }

    public double getDouble(String name) {
        return (double) this.get(name);
    }

    public char getChar(String name) {
        return (char) this.get(name);
    }
    
    public boolean getBool(String name) {
        return (boolean) this.get(name);
    }

    public String getString(String name) {
        return (String) this.get(name);
    }

    public int[] getIntArray(String name) {
        return (int[]) this.get(name);
    }

}
