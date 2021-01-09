package dev.helight.odysseus.inventory;

import java.util.Map;

public interface Route {


    Map<Integer, InteractivePoint> asMap();

    default void build() {

    }

    default void put(int i, InteractivePoint node) {
        asMap().put(i,node);
    };

    default InteractivePoint get(int absolute, int relative, int page) {
        return asMap().get(absolute);
    };

    /*

    default int pageAmount(int rows){
        double d = (double)new TreeSet<>(asMap().keySet()).last() / (double)(rows * 9);
        return (int)Math.ceil(d);
    };

    default int pageIndex(int offset, int rows) {
        double d = (double)offset / (double)(rows*9);
        return (int)Math.ceil(d);
    };

     */
}
