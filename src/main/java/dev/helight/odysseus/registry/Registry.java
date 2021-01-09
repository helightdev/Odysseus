package dev.helight.odysseus.registry;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.MultimapBuilder;
import com.google.common.collect.Multimaps;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

public class Registry<R extends Registrable> {

    private final List<R> instanceList = Collections.synchronizedList(new ArrayList<>());
    private final ListMultimap<String, R> idIndexedMap = Multimaps.synchronizedListMultimap(ArrayListMultimap.create());
    private final Map<UUID, R> uuidIndexedMap = new ConcurrentHashMap<>();
    
    public void register(R r) {
        instanceList.add(r);
        idIndexedMap.put(r.registeredId(), r);
        uuidIndexedMap.put(r.registeredUuid(), r);
    }

    public void unregister(R r) {
        instanceList.remove(r);
        idIndexedMap.remove(r.registeredId(), r);
        uuidIndexedMap.put(r.registeredUuid(), r);
    }

    public boolean containsValue(R r) {
        return instanceList.contains(r);
    }

    public R findByUuid(UUID uuid) {
        return uuidIndexedMap.get(uuid);
    }

    public List<R> findById(String id) {
        return idIndexedMap.get(id);
    }

    public List<R> all() {
        return instanceList;
    }

    public Stream<R> stream() {
        return instanceList.stream();
    }
    
}
