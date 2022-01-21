package com.nerjal.json.elements;

import com.nerjal.json.parser.options.ObjectParseOptions;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

import static com.nerjal.json.JsonError.*;

/**
 * <p>An object that maps keys to JSON values,
 * allowing to build objects for a JSON structure.
 * </p>
 * <p>The object provides a mapping of keys and
 * {@link JsonElement} values, including the children
 * {@link JsonComment}, and a set of entries, only
 * including non-comment children.</p>
 *
 * <p>The objects stringification relies on options
 * set with a {@link ObjectParseOptions} attribute,
 * defaulted to match the JSON4 syntax, which can
 * be replaced or instantiated to a different value.
 * </p>
 */
public class JsonObject extends JsonElement {
    private final Map<String,JsonElement> map;
    private final Set<JsonNode> nodeSet;
    private final Set<JsonComment> commentSet;
    private ObjectParseOptions parseOptions;

    /**
     * An empty JsonObject with default stringification options
     */
    public JsonObject() {
        this.map = new HashMap<>();
        this.nodeSet = new HashSet<>();
        this.commentSet = new HashSet<>();
        this.parseOptions = new ObjectParseOptions();
    }

    public JsonObject(ObjectParseOptions options) {
        this.map = new HashMap<>();
        this.nodeSet = new HashSet<>();
        this.commentSet = new HashSet<>();
        this.parseOptions = options;
    }

    /**
     * Changes the object's stringification options
     * @param options the new options to apply
     */
    public void setParseOptions(ObjectParseOptions options) {
        this.parseOptions = options;
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

    /**
     * @return {@code true} if this object has no child element,
     *         {@link JsonComment} included
     */
    public boolean isEmpty() {
        return this.map.isEmpty();
    }

    /**
     * Returns the number of child element in this object<br>
     * Warning! This includes {@link JsonComment} instances as well,
     * although they are not registered as entries.
     * @return the number of children element of this object
     */
    public int size() {
        return this.map.size();
    }

    /**
     * Returns {@code true} if this object contains an entry for the
     * specified key.  More formally, returns {@code true} if and only
     * if this object contains an entry for a key {@code k} such that
     * {@code Objects.equals(key, k)}.  (There can be at most one such
     * mapping.)
     * @param key the key whose presence in this object is to be tested
     * @return {@code true} if this object contains an entry for the
     *         specified key
     */
    public boolean contains(String key) {
        return this.map.containsKey(key);
    }

    // edit

    /**
     * Adds a new entry to the object, associating the specified value with
     * the key given along
     * @param key the key to add a new entry for
     * @param element the value to associate to the new key
     * @return whether a new entry could be created or not. The object is not
     *         altered if {@code false}
     */
    public boolean add(String key, JsonElement element) {
        if (key == null && element.isComment()) {
            this.map.put(UUID.randomUUID().toString(),element);
            this.commentSet.add((JsonComment) element);
            return true;
        }
        if (this.map.containsKey(key)) {
            return false;
        }
        this.map.put(key, element);
        this.nodeSet.add(new JsonNode(key, element, this));
        for (JsonComment comment : element.getRootComments()) this.add(null, comment);
        return true;
    }

    /**
     * Associates a value to the specified key, and creates a new entry if
     * none already exists for the specified key
     * @param key the key to add or edit an entry for
     * @param element the value to associate to the key
     */
    public void put(String key, JsonElement element) {
        this.map.put(key, element);
        this.nodeSet.remove(new JsonNode(key, null, this));
        this.nodeSet.add(new JsonNode(key, element, this));
        for (JsonComment comment : element.getRootComments()) this.add(null, comment);
    }

    /**
     * Associates a value to the specified key from a JsonNode.
     * @param key the key to change the value for
     * @param value the value to put in the place of the old one
     * @throws NullPointerException if the JsonObject has been
     *         modified between iterations
     */
    private void nodeSetValue(String key, JsonElement value) throws NullPointerException {
        if (!this.contains(key)) throw new NullPointerException("No such entry in the object");
        this.map.put(key, value);
    }

    /**
     * Removes the entry for the specified key only if there is one currently
     * @param key the key with which is expected to be associated a value
     * @return the value removed from the object
     * @throws ChildNotFoundException if there is no entry with the specified
     *         key, and therefore none to be removed
     */
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
            else this.nodeSet.remove(new JsonNode(key,j, this));
            return j;
        } catch (NullPointerException e) {
            throw new ChildNotFoundException("");
        }
    }

    /**
     * Removes the entry for the specified key only if it is currently
     * mapped to the specified value
     * @param key the key with which is associated the value
     * @param j the value expected to be associated with the specified key
     * @return whether the element could be removed or not
     */
    public boolean remove(String key, JsonElement j) {
        boolean b = this.map.remove(key, j);
        if (b) {
            if (j.isComment()) this.commentSet.removeIf(e -> e.hashCode() == j.hashCode());
            else this.nodeSet.remove(new JsonNode(key, j, this));
        }
        return b;
    }

    /**
     * Removes all {@link JsonElement} child from the object meeting the given operator,
     * then returns them.<br>
     * The operator must return null for all not-fitting elements.
     * @param operator The operator to apply to all child element of the object.
     * @return a {@link Collection} of all the {@link JsonElement} removed from the object.
     */
    public Collection<JsonElement> remove(UnaryOperator<JsonElement> operator) {
        Set<String> remove = new HashSet<>();
        Set<JsonElement> removed = new HashSet<>();
        for (String key : map.keySet())
            if (map.get(key).isComment() && operator.apply(map.get(key)) != null) remove.add(key);
        for (String key : remove) {
            try {
                JsonElement e = remove(key);
                removed.add(e);
                nodeSet.remove(new JsonNode(key, e, this));
            } catch (ChildNotFoundException ignored) {}
        }
        return removed;
    }

    /**
     * Empties the object from all keys and children.
     * Comments aren't spared.
     * @see #remove(UnaryOperator) see
     *      JsonObject#remove(UnaryOperator) for a more
     *      specific removal
     */
    public void clear() {
        this.map.clear();
        this.nodeSet.clear();
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
    @Override
    public String stringify(String indentation, String indentIncrement, JsonStringifyStack stack)
            throws RecursiveJsonElementException {
        if (stack == null) stack = new JsonStringifyStack(this);
        if (this.map.size() == 0) return "{}";
        StringBuilder builder = new StringBuilder("{");
        AtomicInteger count = new AtomicInteger();
        AtomicInteger index = new AtomicInteger();
        AtomicInteger lastComma = new AtomicInteger();
        AtomicBoolean endOnComment = new AtomicBoolean(false);
        for (Map.Entry<String, JsonElement> entry : map.entrySet()) {
            String k = entry.getKey();
            JsonElement e = entry.getValue();
            if (stack.hasOrAdd(e)) throw new RecursiveJsonElementException("Recursive JSON structure in JsonObject");
            count.getAndIncrement();
            index.getAndIncrement();
            endOnComment.set(e.isComment());
            builder.append('\n').append(indentation).append(indentIncrement);
            if (!e.isComment()) {
                char c = parseOptions.keyQuoteChar();
                builder.append(String.format("%c%s%c: ", c, k, c));
            }
            builder.append(e.stringify(String.format("%s%s",indentation,indentIncrement),indentIncrement, stack));
            if (index.get() < size() &! e.isComment()) {
                lastComma.set(builder.length());
                builder.append(", ");
            }
            stack.rem(e);
        }
        if (endOnComment.get() && lastComma.get() != 0) builder.deleteCharAt(lastComma.get());
        builder.append('\n').append(indentation);
        builder.append('}');
        return builder.toString();
    }

    // iteration

    /**
     * Returns a {@link Set} of all the key-value associations
     * in the object, thus comments excluded.
     * @return a set of all non-comment children
     */
    public Set<JsonNode> entrySet() {
        return Set.copyOf(this.nodeSet);
    }

    /**
     * Returns a set of all children of the object.
     * Comments are here identified by {@link UUID} keys
     * @return a {@link Map.Entry} set of all children in
     *         the object.
     */
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
     *         added or removed during iteration
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
     *         added or removed during iteration
     */
    public void forAllComments(Consumer<JsonElement> action) {
        Objects.requireNonNull(action);
        this.commentSet.forEach(action);
    }

    /**
     * A {@link JsonObject} entry (key-value pair).<br>
     * The {@link JsonObject#entrySet()} returns a collection-view of the object,
     * whose values are non comments. The <i>only</i> way to get the full entry
     * set is by using the {@link JsonObject#allEntriesSet()} method.<br>
     * The only way to obtain a reference to a JsonObject entry is from the
     * iterator of this collection-view. These JsonNode objects are valid only
     * for the duration of the iteration; more formally, the behavior of a
     * JsonNode is undefined if the backing JsonObject has been modified after
     * the entry was returned by the iterator, except through the setValue
     * operation on the map entry.
     */
    public static class JsonNode implements Map.Entry<String, JsonElement> {
        private final String key;
        private JsonElement value;
        private final JsonObject backObject;

        protected JsonNode(String key, JsonElement value, JsonObject backObject) {
            this.key = key;
            this.value = value;
            this.backObject = backObject;
        }

        /**
         * @return the key corresponding to this entry
         */
        @Override
        public String getKey() {
            return this.key;
        }

        /**
         * @return the value corresponding to this entry
         */
        @Override
        public JsonElement getValue() {
            return this.value;
        }

        /**
         * Replaces the value corresponding to this entry with the
         * specified value (write through to the JsonObject)
         * @param value the value to put in the JsonObject for this
         *              entry
         * @return the old value of this entry
         * @throws NullPointerException if the backing JsonObject
         *         has been modified between iterations
         */
        @Override
        public JsonElement setValue(JsonElement value) {
            JsonElement old = this.value;
            this.value = value;
            this.backObject.nodeSetValue(this.key, value);
            return old;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            JsonNode jsonNode = (JsonNode) o;
            return Objects.equals(key, jsonNode.key) && Objects.equals(value, jsonNode.value);
        }

        /**
         * Returns the entry's identifying hash
         * @return the entry's identifying hash
         */
        @Override
        public int hashCode() {
            return Objects.hash(key,backObject);
        }
    }
}
