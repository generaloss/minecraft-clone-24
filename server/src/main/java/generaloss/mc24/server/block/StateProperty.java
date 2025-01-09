package generaloss.mc24.server.block;

import java.util.*;

public class StateProperty<T> {

    private final String name;
    private final Class<?> valueClass;
    private final List<T> allowedValues;

    @SafeVarargs
    public StateProperty(String name, Class<?> valueClass, T... allowedValues) {
        this.name = name;
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

    public String getName() {
        return name;
    }

    public Class<?> getValueClass() {
        return valueClass;
    }

    public Collection<T> getAllowedValues() {
        return allowedValues;
    }

}
