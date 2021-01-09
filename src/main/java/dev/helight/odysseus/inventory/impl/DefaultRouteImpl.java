package dev.helight.odysseus.inventory.impl;


import dev.helight.odysseus.inventory.InteractivePoint;
import dev.helight.odysseus.inventory.Route;

import java.util.HashMap;
import java.util.Map;

public class DefaultRouteImpl implements Route {

    private Map<Integer, InteractivePoint> map = new HashMap<>();

    @Override
    public Map<Integer, InteractivePoint> asMap() {
        return map;
    }

    @Override
    public void build() {

    }

}
