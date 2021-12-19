package com.nerjal.json.elements;

import com.nerjal.json.JsonError;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;

public class JsonObject extends JsonElement {
    private Map<String,JsonElement> map;

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
    public void forEach(BiConsumer<String, JsonElement> action) {
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
