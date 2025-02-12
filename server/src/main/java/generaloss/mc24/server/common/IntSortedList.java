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
        if(this.isIndexValid(index))
            return list.get(index);
        return null;
    }

    public void put(T object) {
        final int index = this.indexOf(object);
        if(this.contains(index, object)){
            System.err.println("Object '" + object + "' already contains at " + index);
            return;
        }
        list.add(index, object);
    }


    public T remove(int value) {
        final int index = this.searchValueIndex(value);
        if(this.isIndexValid(index)){
            return list.remove(index);
        }else{
            System.err.println("Index '" + index + "' is invalid, nothing to remove.");
            return null;
        }
    }

    public T remove(int value, T object) {
        final int index = this.searchValueIndex(value);
        if(this.contains(index, object)){
            return list.remove(index);
        }else{
            System.err.println("No object '" + object + "' to remove.");
            return null;
        }
    }

    public T remove(T object) {
        final int value = valueFunc.apply(object);
        return this.remove(value, object);
    }


    public int indexOf(T object) {
        final int value = valueFunc.apply(object);
        return this.searchValueIndex(value);
    }


    public boolean contains(T object) {
        return list.contains(object);
    }

    public boolean contains(int index, T object) {
        return (this.isIndexValid(index) && list.get(index) == object);
    }

    public boolean isIndexValid(int index) {
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
