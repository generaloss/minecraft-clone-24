package generaloss.mc24.server.common;

import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

public class IntSortedList<T> implements Iterable<T> {

    private final List<T> list;
    private final Function<T, Integer> valueFunc;

    public IntSortedList(List<T> list, Function<T, Integer> valueFunc) {
        this.list = list;
        this.valueFunc = valueFunc;
    }
    
    public List<T> list() {
        return list;
    }
    
    public int size() {
        return list.size();
    }

    
    public T get(int value) {
        final int index = this.searchValueIndex(value);
        if(this.isIndexValid(index) && this.valueByIndex(index) == value)
            return list.get(index);
        return null;
    }

    public void put(T object) {
        final int index = this.indexOf(object);
        if(this.isIndexValid(index) && list.get(index) == object){
            list.set(index, object);
        }else{
            list.add(index, object);
        }
    }


    public T remove(int value, T object) {
        final int index = this.searchValueIndex(value);
        if(this.isIndexValid(index) && list.get(index) == object)
            return list.remove(index);
        return null;
    }

    public T remove(T object) {
        final int value = valueFunc.apply(object);
        return this.remove(value, object);
    }

    public T remove(int value) {
        final T object = this.get(value);
        if(object != null)
            return this.remove(object);
        return null;
    }


    public int indexOf(T object) {
        final int value = valueFunc.apply(object);
        return this.searchValueIndex(value);
    }

    private int valueByIndex(int index) {
        return valueFunc.apply(list.get(index));
    }


    public boolean contains(T object) {
        return list.contains(object);
    }

    private boolean isIndexValid(int index) {
        return (index < list.size());
    }


    public List<T> sublist(int startValue, int endValue) {
        final int startIndex = this.searchValueIndex(startValue);
        final int endIndex = this.searchValueIndex(endValue);
        return list.subList(startIndex, endIndex);
    }

    @Override
    public Iterator<T> iterator() {
        return list.iterator();
    }

    
    private int searchValueIndex(int targetValue) {
        int lowIndex = 0;
        int highIndex = (list.size() - 1);

        while(lowIndex <= highIndex){
            final int midIndex = (lowIndex + highIndex) / 2;
            final T object = list.get(midIndex);
            final int value = valueFunc.apply(object);

            if(value < targetValue){
                lowIndex = (midIndex + 1);
            }else if(value > targetValue){
                highIndex = (midIndex - 1);
            }else{
                return midIndex;
            }
        }
        return lowIndex;
    }
    
}
