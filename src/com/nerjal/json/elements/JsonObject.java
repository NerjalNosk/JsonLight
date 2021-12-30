package com.nerjal.json.elements;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static com.nerjal.json.JsonError.*;

public class JsonObject extends JsonElement {
    private int stringifyKeyQuotes;
    private final Map<String,JsonElement> map;
    private final Set<JsonNode> nodeSet;
    private final Set<JsonComment> commentSet;

    public JsonObject() {
        this.stringifyKeyQuotes = 2;
        this.map = new HashMap<>();
        this.nodeSet = new HashSet<>();
        this.commentSet = new HashSet<>();
    }

    public JsonObject(int i) throws IllegalQuoteValue {
        if (i > 2 || i < 0) throw new IllegalQuoteValue(String.format("Illegal quote value %d",i));
        this.stringifyKeyQuotes = i;
        this.map = new HashMap<>();
        this.nodeSet = new HashSet<>();
        this.commentSet = new HashSet<>();
    }

    // get

    /**
     * Returns the element mapped for the specified key, or throws an error if
     * no associated child can be found.
     * @param key the key whose associated child is to be returned
     * @return the child mapped for the specified key.
     * @throws ChildNotFoundException when there is
     * no such child in the object
     */
    public JsonElement get(String key) throws ChildNotFoundException {
        try {
            return Objects.requireNonNull(this.map.get(key));
        } catch (NullPointerException e) {
            throw new ChildNotFoundException(
                    String.format("JsonObject has no such child '%s'",key));
        }
    }

    public boolean isEmpty() {
        return this.map.isEmpty();
    }

    public int size() {
        return this.map.size();
    }

    public boolean contains(String child) {
        return this.map.containsKey(child);
    }

    // edit

    public boolean add(String key, JsonElement element) {
        if (key == null && element.isComment()) {
            this.map.put(UUID.randomUUID().toString(),element);
            this.commentSet.add((JsonComment) element);
        }
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

    public JsonElement remove(String key) throws ChildNotFoundException {
        try {
            JsonElement j = this.map.remove(key);
            if (j.isComment()) this.commentSet.removeIf(e -> {
                try {
                    return e.getAsString().equals(j.getAsJsonComment().getAsString());
                } catch (JsonElementTypeException ex) {
                    ex.printStackTrace();
                    return false;
                }
            });
            else this.nodeSet.remove(new JsonNode(key,j));
            return j;
        } catch (NullPointerException e) {
            throw new ChildNotFoundException("");
        }
    }

    public boolean remove(String key, JsonElement j) {
        boolean b = this.map.remove(key, j);
        if (b) {
            if (j.isComment()) this.commentSet.removeIf(e -> {
                try {
                    return e.getAsString().equals(j.getAsJsonComment().getAsString());
                } catch (JsonElementTypeException ex) {
                    ex.printStackTrace();
                    return false;
                }
            });
            else this.nodeSet.remove(new JsonNode(key, j));
        }
        return b;
    }

    public void clear() {
        this.map.clear();
        this.nodeSet.clear();
    }

    // stringify

    public void setStringifyQuotes(int i) throws IllegalQuoteValue {
        if (i > 2 || i < 0) throw new IllegalQuoteValue(String.format("Illegal quote value %d",i));
        this.stringifyKeyQuotes = i;
    }

    // JsonElement overrides

    @Override
    public boolean isJsonObject() {
        return true;
    }
    @Override
    public String typeToString() {
        return "Object";
    }
    @Override
    public JsonObject getAsJsonObject() {
        return this;
    }

    // iteration

    public Set<JsonNode> entrySet() {
        return Set.copyOf(this.nodeSet);
    }

    public Set<Map.Entry<String,JsonElement>> allEntriesSet() {
        return this.map.entrySet();
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

    /**
     * Performs the given action for each comment element in this JsonObject
     * until all elements have been processed or the action throws an
     * exception, which then is relayed to the caller.
     * @param action The action to be performed for each comment element
     * @throws NullPointerException if the specified action is null
     * @throws ConcurrentModificationException if an element is found to be
     * added or removed during iteration
     */
    public void forAllComments(Consumer<JsonElement> action) {
        Objects.requireNonNull(action);
        this.commentSet.forEach(action);
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

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            JsonNode jsonNode = (JsonNode) o;
            return Objects.equals(key, jsonNode.key) && Objects.equals(value, jsonNode.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(key, value);
        }
    }
}
