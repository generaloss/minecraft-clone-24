package generaloss.mc24.server.common;

public abstract class AbstractProperty {

    private final String name;
    private final Object defaultValue;

    protected AbstractProperty(String name, Object defaultValue) {
        this.name = name;
        this.defaultValue = defaultValue;
    }

    public String getName() {
        return name;
    }

    public Object getDefaultValue() {
        return defaultValue;
    }

}
