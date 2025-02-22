package generaloss.mc24.server.block;

import generaloss.mc24.server.common.AbstractProperty;
import generaloss.mc24.server.common.Facing;

import java.util.*;

public class StateProperty<T> extends AbstractProperty {

    private final Class<?> valueClass;
    private final List<T> allowedValues;

    @SafeVarargs
    public StateProperty(String name, T defaultValue, Class<?> valueClass, T... allowedValues) {
        super(name, defaultValue);
        this.valueClass = valueClass;
        this.allowedValues = new ArrayList<>();

        if(allowedValues.length != 0){
            Collections.addAll(this.allowedValues, allowedValues);
            return;
        }

        if(valueClass.isEnum()) {
            for(Object enumConstant: valueClass.getEnumConstants())
                this.allowedValues.add((T) enumConstant);

        }else if(valueClass == Boolean.class) {
            this.allowedValues.add((T) Boolean.FALSE);
            this.allowedValues.add((T) Boolean.TRUE);
        }
    }

    public Class<?> getValueClass() {
        return valueClass;
    }

    public Collection<T> getAllowedValues() {
        return allowedValues;
    }


    private static final Map<String, StateProperty<?>> PROPERTIES = new HashMap<>();

    public static <T> StateProperty<T> get(String name) {
        return (StateProperty<T>) PROPERTIES.get(name);
    }

    @SafeVarargs
    public static <T> StateProperty<T> register(String name, T defaultValue, Class<?> valueClass, T... allowedValues) {
        final StateProperty<T> property = new StateProperty<>(name, defaultValue, valueClass, allowedValues);
        PROPERTIES.put(name, property);
        return property;
    }


    public static final StateProperty<Boolean> SNOWY  = register("snowy",  false,       Boolean.class);
    public static final StateProperty<Boolean> LIT    = register("lit",    false,       Boolean.class);
    public static final StateProperty<Facing>  FACING = register("facing", Facing.WEST, Facing.class );
    public static final StateProperty<String>  HALF   = register("half",   "bottom",    String.class,  "bottom", "top");
    public static final StateProperty<String>  SHAPE  = register("shape",  "straight",  String.class,  "inner_left", "inner_right", "outer_left", "outer_right", "straight");
    public static final StateProperty<Integer> BITES  = register("bites",  0,           Integer.class, 0, 1, 2, 3, 4);

}
