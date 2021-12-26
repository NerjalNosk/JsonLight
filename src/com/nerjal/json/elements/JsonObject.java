package com.nerjal.json.elements;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static com.nerjal.json.JsonError.*;

public class JsonObject extends JsonElement {
    private final Map<String,JsonElement> map;
    private final Set<JsonNode> nodeSet;

    public JsonObject() {
        this.map = new HashMap<>();
        this.nodeSet = new HashSet<>();
    }

    /**
     * Returns the element mapped for the specified key, or throws an error if
     * no associated child can be found.
     * @param key the key whose associated child is to be returned
     * @return the child mapped for the specified key.
     * @throws JsonObjectChildNotFoundException when there is
     * no such child in the object
     */
    public JsonElement get(String key) throws JsonObjectChildNotFoundException {
        try {
            return Objects.requireNonNull(this.map.get(key));
        } catch (NullPointerException e) {
            throw new JsonObjectChildNotFoundException(
                    String.format("JsonObject has no such child '%s'",key));
        }
    }
    public boolean add(String key, JsonElement element) {
        if (this.map.containsKey(key)) {
            return false;
        }
        this.map.put(key, element);
        this.nodeSet.add(new JsonNode(key, element));
        return true;
    }
    public void put(String key, JsonElement element) {
        this.map.put(key, element);
        this.nodeSet.add(new JsonNode(key, element));
    }

    public boolean isEmpty() {
        return this.map.isEmpty();
    }
    public int size() {
        return this.map.size();
    }

    /**
     * Performs the given action for each non-comment element in this
     * JsonObject until all elements have been processed or the action
     * throws an exception, which then is relayed to the caller.
     * @param action The action to be performed for each element
     * @throws NullPointerException if the specified action is null
     * @throws ConcurrentModificationException if an element is found to be
     * added or removed during iteration
     */
    public void forEach(BiConsumer<String, JsonElement> action) {
        Objects.requireNonNull(action);
        this.nodeSet.forEach(node -> action.accept(node.key, node.value));
    }

    /**
     * Performs the given action for each element in this JsonObject,
     * comments included, until all elements have been processed or the
     * action throws an exception, which then is relayed to the caller.
     * @param action The action to be performed for each element
     * @throws NullPointerException if the specified action is null
     * @throws ConcurrentModificationException if an element is found to be
     * added or removed during iteration
     */
    public void forAll(BiConsumer<String, JsonElement> action) {
        Objects.requireNonNull(action);
        this.map.forEach(action);
    }
    public Set<JsonNode> entrySet() {
        return Set.copyOf(this.nodeSet);
    }
    public Set<Map.Entry<String,JsonElement>> allEntriesSet() {
        return this.map.entrySet();
    }
    public void clear() {
        this.map.clear();
        this.nodeSet.clear();
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


    public static class JsonNode implements Map.Entry<String, JsonElement> {
        private final String key;
        private JsonElement value;

        public JsonNode(String key, JsonElement value) {
            this.key = key;
            this.value = value;
        }


        @Override
        public String getKey() {
            return this.key;
        }

        @Override
        public JsonElement getValue() {
            return this.value;
        }

        @Override
        public JsonElement setValue(JsonElement value) {
            JsonElement old = this.value;
            this.value = value;
            return old;
        }
    }
}
