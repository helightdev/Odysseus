package dev.helight.odysseus.inventory.routes;

import dev.helight.odysseus.inventory.InteractivePoint;
import dev.helight.odysseus.inventory.Route;

import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

public class BarRoute implements Route {

    private Map<Integer, InteractivePoint> map = new ConcurrentHashMap<>();
    private Map<Integer, InteractivePoint> bar = new ConcurrentHashMap<>();

    private final int rows;

    //Exclusive
    private final int maxContent;

    public BarRoute(int rows) {
        this.rows = rows;
        this.maxContent = (rows - 1) * 9 - 1;
    }

    public void addBar(int i, InteractivePoint node) {
        bar.put(i, node);
    }

    @Override
    public Map<Integer, InteractivePoint> asMap() {
        return map;
    }

    @Override
    public void put(int i, InteractivePoint node) {
        map.put(i, node);
    }

    @Override
    public InteractivePoint get(int absolute, int relative, int page) {
        if (relative > maxContent) return bar.get(relative - maxContent);
        int offset = page * (rows - 1) * 9;
        return map.get(offset + relative);
    }

    public int pages() {
        TreeSet<Integer> set = new TreeSet<>(map.keySet());
        int highestIndex = set.last();
        return (int) Math.ceil(highestIndex / (double)((rows - 1) * 9));
    }

}
