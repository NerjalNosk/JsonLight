package com.nerjal.json.elements;

import com.nerjal.json.JsonError;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.ConcurrentModificationException;

public class JsonObject extends JsonElement {
    private final Map<String,JsonElement> map;

    public JsonObject() {
        this.map = new HashMap<>();
    }

    public JsonElement get(String child) throws JsonError.JsonObjectChildNotFoundException {
        try {
            return this.map.get(child);
        } catch (NullPointerException e) {
            throw new JsonError.JsonObjectChildNotFoundException(
                    String.format("JsonObject has no such child '%s'",child));
        }
    }
    public boolean add(String key, JsonElement element) {
        if (this.map.containsKey(key)) {
            return false;
        }
        this.map.put(key, element);
        return true;
    }
    public void put(String key, JsonElement element) {
        this.map.put(key, element);
    }

    public boolean isEmpty() {
        return this.map.isEmpty();
    }
    public int size() {
        return this.map.size();
    }

    /**
     * Performs the given action for each non-comment element in this
     * JsonObject until all element have been processed or the action
     * throws an exception, which then is relayed to the caller.
     * @param action The action to be performed for each element
     * @throws NullPointerException if the specified action is null
     * @throws ConcurrentModificationException if an element is found to be
     * removed during iteration
     */
    public void forEach(BiConsumer<String, JsonElement> action) {
        Objects.requireNonNull(action);
        this.map.forEach((key,elem) ->{
            if (!elem.isComment()) action.accept(key,elem);
        });
    }

    /**
     * Performs the given action for each element in this JsonObject,
     * comments included, until all element have been processed or the
     * action throws an exception, which then is relayed to the caller.
     * @param action The action to be performed for each element
     * @throws NullPointerException if the specified action is null
     * @throws ConcurrentModificationException if an element is found to be
     * removed during iteration
     */
    public void forAll(BiConsumer<String, JsonElement> action) {
        Objects.requireNonNull(action);
        this.map.forEach(action);
    }
    public Set<Map.Entry<String, JsonElement>> entrySet() {
        return this.map.entrySet();
    }
    public void clear() {
        this.map.clear();
    }
    public boolean contains(String child) {
        return this.map.containsKey(child);
    }


    @Override
    public boolean isJsonObject() {
        return true;
    }
    @Override
    public JsonObject getAsJsonObject() {
        return this;
    }
}
