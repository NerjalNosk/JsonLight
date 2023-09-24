package io.github.nerjalnosk.jsonlight.elements;

import io.github.nerjalnosk.jsonlight.parser.options.ObjectParseOptions;
import io.github.nerjalnosk.jsonlight.parser.options.ParseSet;
import io.github.nerjalnosk.jsonlight.JsonError;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

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
 * <br>These options allow to set the keys quoting.
 * </p>
 * @author nerjal
 */
public class JsonObject extends JsonElement {
    private final Map<String,JsonElement> map;
    private final transient Set<JsonNode> nodeSet;
    private final Set<JsonComment> commentSet;
    private final transient List<JsonNode> orderList;
    private transient ObjectParseOptions parseOptions;

    /**
     * An empty JsonObject with default stringification options
     */
    public JsonObject() {
        this(new ObjectParseOptions());
    }

    public JsonObject(ObjectParseOptions options) {
        this.map = new HashMap<>();
        this.nodeSet = new HashSet<>();
        this.commentSet = new HashSet<>();
        this.orderList = new ArrayList<>();
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
     * @throws JsonError.ChildNotFoundException when there is
     * no such child in the object
     */
    public JsonElement get(String key) throws JsonError.ChildNotFoundException {
        try {
            return Objects.requireNonNull(this.map.get(key));
        } catch (NullPointerException e) {
            throw new JsonError.ChildNotFoundException(
                    String.format("JsonObject has no such child '%s'",key));
        }
    }

    /**
     * Returns the number associated with the specified key
     * @param key the key which is mapped for the expected number
     * @return the number mapped for the specified key
     * @throws JsonError.ChildNotFoundException when there is no child associated with
     *         the specified key in the object
     * @throws JsonError.JsonElementTypeException if the element doesn't have a numeral
     *         value
     */
    public Number getNumber(String key) throws JsonError.ChildNotFoundException, JsonError.JsonElementTypeException {
        return this.get(key).getAsNumber();
    }

    /**
     * Returns the String associated with the specified key
     * @param key the key which is mapped for the expected string
     * @return the string mapped for the specified key
     * @throws JsonError.ChildNotFoundException when there is no child associated with
     *         the specified key in the object
     * @throws JsonError.JsonElementTypeException if the element doesn't have a string
     *         value
     */
    public String getString(String key) throws JsonError.ChildNotFoundException, JsonError.JsonElementTypeException {
        return this.get(key).getAsString();
    }

    /**
     * Returns the boolean associated with the specified key
     * @param key the key which is mapped for the expected boolean
     * @return the boolean mapped for the specified key
     * @throws JsonError.ChildNotFoundException when there is no child associated with
     *         the specified key in the object
     * @throws JsonError.JsonElementTypeException if the element doesn't have a boolean
     *         value
     */
    public boolean getBoolean(String key) throws JsonError.ChildNotFoundException, JsonError.JsonElementTypeException {
        return this.get(key).getAsBoolean();
    }

    /**
     * Returns the array associated with the specified key
     * @param key the key which is mapped for the expected array
     * @return the array mapped for the specified key
     * @throws JsonError.ChildNotFoundException when there is no child associated with
     *         the specified key in the object
     * @throws JsonError.JsonElementTypeException if the element doesn't have an array
     *         value
     */
    public JsonArray getArray(String key) throws JsonError.ChildNotFoundException, JsonError.JsonElementTypeException {
        return this.get(key).getAsJsonArray();
    }

    /**
     * Returns the object associated with the specified key
     * @param key the key which is mapped for the expected object
     * @return the object mapped for the specified key
     * @throws JsonError.ChildNotFoundException when there is no child associated with
     *         the specified key in the object
     * @throws JsonError.JsonElementTypeException if the element doesn't have an object
     *         value
     */
    public JsonObject getObject(String key) throws JsonError.ChildNotFoundException, JsonError.JsonElementTypeException {
        return this.get(key).getAsJsonObject();
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
     * although they are not registered as nodes.
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
        JsonNode node = new JsonNode(key, element, this);
        if (key == null && element.isComment()) {
            this.map.put(UUID.randomUUID().toString(),element);
            this.commentSet.add((JsonComment) element);
            this.orderList.add(node);
            return true;
        }
        if (this.map.containsKey(key)) {
            return false;
        }
        this.map.put(key, element);
        this.nodeSet.add(node);
        this.orderList.add(node);
        for (JsonComment comment : element.getRootComments()) this.add(null, comment);
        return true;
    }

    /**
     * Renames the node with the specified key to the
     * specified new key.
     * @param key the old key of the node to rename
     * @param newKey the new key of the node
     * @return whether the node could be renamed. The
     *         node cannot be renamed to an already
     *         used key (see {@link JsonObject#forceRename})
     * @throws JsonError.ChildNotFoundException if there is no entry
     *         with the specified key.
     * @throws IllegalArgumentException if any of the
     *         specified keys is {@code null}.
     */
    public boolean rename(String key, String newKey) throws JsonError.ChildNotFoundException, IllegalArgumentException {
        return rename(key, newKey, false) == null;
    }

    /**
     * Renames by force the entry with the specified key
     * to the given new key. Any entry with the specified
     * new key will be overwritten, contrarily to
     * {@link JsonObject#rename}
     * @param key the old key of the node to rename
     * @param newKey the new key if the node
     * @return the value of any overwritten entry if there
     *         was, {@code null} otherwise.
     * @throws JsonError.ChildNotFoundException if there is no entry
     *         with the specified key.
     * @throws IllegalArgumentException if any of the
     *         specified keys is {@code null}.
     */
    public JsonElement forceRename(String key, String newKey) throws JsonError.ChildNotFoundException, IllegalArgumentException {
        return rename(key, newKey, true);
    }

    private JsonElement rename(String key, String newKey, boolean force)
            throws JsonError.ChildNotFoundException, IllegalArgumentException {
        if (key == null || newKey == null)
            throw new IllegalArgumentException("Unable to rename from or to a null key");
        if (key.equals(newKey))
            return null;
        if (!this.map.containsKey(key))
            throw new JsonError.ChildNotFoundException(String.format("Object has no such child '%s'",key));
        if (!force && this.map.containsKey(newKey))
            return new JsonString();
        JsonElement e = map.remove(newKey);
        this.orderList.removeIf(node -> (node.key.equals(newKey) && node.value == e));
        this.map.put(newKey, this.map.remove(key));
        return e;
    }

    /**
     * Associates a value to the specified key, and creates
     * a new entry if none already exists for the specified key.
     * Use {@link #add} to add comments
     * @param key the key to add or edit an entry for
     * @param element the value to associate to the key
     */
    public void put(String key, JsonElement element) {
        boolean b = this.map.containsKey(key);
        JsonNode node = new JsonNode(key, element, this);
        this.map.put(key, element);
        this.nodeSet.remove(new JsonNode(key, null, this));
        this.nodeSet.add(node);
        if (!b) this.orderList.add(node);
        for (JsonComment comment : element.getRootComments()) this.add(null, comment);
        element.clearRootComment();
    }

    /**
     * An alias to {@link JsonObject#put}
     * @param key the key to add or edit an entry for
     * @param element the value to associate to the key
     * @see JsonObject#put
     */
    public void set(String key, JsonElement element) {
        this.put(key, element);
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
     * @throws JsonError.ChildNotFoundException if there is no entry with the specified
     *         key, and therefore none to be removed
     */
    public JsonElement remove(String key) throws JsonError.ChildNotFoundException {
        try {
            JsonElement j = this.map.remove(key);
            if (j.isComment()) this.commentSet.removeIf(e -> {
                try {
                    return e.getAsString().equals(j.getAsJsonComment().getAsString());
                } catch (JsonError.JsonElementTypeException ex) {
                    ex.printStackTrace();
                    return false;
                }
            });
            else this.nodeSet.remove(new JsonNode(key,j, this));
            this.orderList.removeIf(node -> node.key.equals(key));
            return j;
        } catch (NullPointerException e) {
            throw new JsonError.ChildNotFoundException("");
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
            this.orderList.removeIf(node -> node.key.equals(key) && node.value == j);
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
            if ((!map.get(key).isComment()) && operator.apply(map.get(key)) != null) remove.add(key);
        for (String key : remove) {
            try {
                JsonElement e = remove(key);
                removed.add(e);
                nodeSet.remove(new JsonNode(key, e, this));
                if (e.isComment())
                    commentSet.remove(e);
                orderList.removeIf(node -> node.key.equals(key));
            } catch (JsonError.ChildNotFoundException ignored) {
                // ignored
            }
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
        this.commentSet.clear();
        this.orderList.clear();
    }

    /**
     * Pushes all the nodes of the specified object into
     * this one, and returns the number of modifications made.
     * See {@link #pushAll} to push comments at the same time.
     * @param object the object to push into this one. Will not be affected.
     * @return the number of modifications this object underwent.
     */
    public int push(JsonObject object) {
        AtomicInteger i = new AtomicInteger();
        object.forEach((key, value) -> {
            i.incrementAndGet();
            this.put(key, value);
        });
        return i.get();
    }

    /**
     * Recursively pushes the specified object into
     * this one, and returns the number of modifications
     * made. The recursion only occurs over object values
     * with the same key.
     * @param object the object to push into this one. Will not be affected.
     * @return the number of modifications this object and its
     *         nodes recursively underwent.
     */
    public int recursivePush(JsonObject object) {
        AtomicInteger i = new AtomicInteger();
        object.forEach((key, value) -> {
            if (this.map.containsKey(key) && this.map.get(key).isJsonObject() && value.isJsonObject())
                i.getAndAdd(((JsonObject)map.get(key)).recursivePush((JsonObject) value));
            else {
                i.incrementAndGet();
                this.put(key, value);
            }
        });
        return i.get();
    }

    /**
     * Pushes all the nodes and comments of the specified object
     * into this one, and returns the number of modifications made.
     * All pushed comment counts as a modification.
     * See {@link #push} to ignore comments
     * @param object The object to push into this one. Will not be affected.
     * @return the number of modifications this object underwent, counting comments
     */
    public int pushAll(JsonObject object) {
        AtomicInteger i = new AtomicInteger();
        object.forAll((key, value) -> {
            i.incrementAndGet();
            if (value.isComment())
                this.add(key, value);
            else this.put(key, value);
        });
        return i.get();
    }

    /**
     * Recursively pushes all the nodes and comments of the
     * specified object into this one returns the number of
     * modifications applied.
     * All pushed comment counts as a modification, recursion
     * only occurs over objects values with the same key.
     * @param object The object to push into this one. Will not be affected.
     * @return the number of modifications this object and
     *         nodes recursively underwent, counting comments.
     */
    public int recursivePushAll(JsonObject object) {
        AtomicInteger i = new AtomicInteger();
        object.forAll((key, value) -> {
            if (this.map.containsKey(key) && this.map.get(key).isJsonObject() && value.isJsonObject())
                i.getAndAdd(((JsonObject)map.get(key)).recursivePush((JsonObject) value));
            else {
                i.getAndIncrement();
                if (value.isComment())
                    this.add(key, value);
                else this.put(key, value);
            }
        });
        return i.get();
    }

    /**
     * <p>Creates a new JsonObject with the nodes and unique comments
     * of all the specified objects.
     * </p>
     * <p>Objects are each overwriting each other on the final output
     * in the order they are specified.
     * </p>
     * <p>Comments are compared via their hash.
     * </p>
     * @param o1 The first object to merge. Will not be affected.
     * @param o2 The second object to merge Will not be affected.
     * @param objects All the objects to merge. None will be affected.
     * @return a new object containing the nodes and comments of
     *         specified objects, in the order they are given.
     */
    public static JsonObject merge(JsonObject o1, JsonObject o2, JsonObject... objects) {
        JsonObject out = new JsonObject();
        out.pushAll(o1);
        out.push(o2);
        for (JsonComment c : o2.commentSet) {
            if (out.commentSet.contains(c)) continue;
            out.add(null,c);
        }
        for (JsonObject object : objects) {
            out.push(object);
            for (JsonComment c : object.commentSet) {
                if (out.commentSet.contains(c)) continue;
                out.add(null, c);
            }
        }
        return out;
    }

    /**
     * <p>Creates a new JsonObject recursively combining the nodes
     * and unique comments of all the specified objects.
     * </p>
     * <p>Objects are each overwriting each other on the final output
     * in the order they are specified.
     * </p>
     * <p>Comments are compared via their hash.
     * </p>
     * @param o1 The first object to merge. Will not be affected.
     * @param o2 The second object to recursively merge Will not be affected.
     * @param objects All the objects to recursively merge. None will be affected.
     * @return a new object containing the nodes and comments of
     *         specified objects, in the order they are given.
     */
    public static JsonObject recursiveMerge(JsonObject o1, JsonObject o2, JsonObject... objects) {
        JsonObject out = new JsonObject();
        out.recursivePushAll(o1);
        out.recursivePush(o2);
        for (JsonComment c : o2.commentSet) {
            if (out.commentSet.contains(c)) continue;
            out.add(null, c);
        }
        for (JsonObject object : objects) {
            out.recursivePush(object);
            for (JsonComment c : object.commentSet) {
                if (out.commentSet.contains(c)) continue;
                out.add(null, c);
            }
        }
        return out;
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
    protected String stringify(ParseSet parseSet, String indentation, String indentIncrement, ExplorationStack stack)
            throws JsonError.RecursiveJsonElementException {
        Objects.requireNonNull(stack);
        ObjectParseOptions setOptions = (ObjectParseOptions) parseSet.getOptions(this.getClass());
        if (this.parseOptions.isChanged() || setOptions == null) {
            setOptions = this.parseOptions;
        }
        ObjectParseOptions options = setOptions;
        if (this.map.isEmpty()) return "{}";
        if (stack.stack(this.hashCode())) {
            if (!options.resolveCircular()) {
                throw new JsonError.RecursiveJsonElementException("Recursive JSON structure in JsonArray");
            }
            return this.asRef();
        }
        StringBuilder builder = new StringBuilder();
        if (this.getId().isPresent() && options.resolveCircular()) {
            builder.append(this.stringifiedId()).append(" ");
        }
        builder.append("{");
        AtomicInteger count = new AtomicInteger();
        AtomicInteger index = new AtomicInteger();
        AtomicInteger lastComma = new AtomicInteger();
        AtomicBoolean endOnComment = new AtomicBoolean(false);
        List<JsonNode> l = options.isOrdered() ? orderList : new ArrayList<>();
        if (!options.isOrdered()) {
            this.map.forEach((k, v) -> l.add(new JsonNode(k, v, this)));
        }
        for (JsonNode node : l) {
            String k = node.key;
            JsonElement e = node.value;
            count.getAndIncrement();
            index.getAndIncrement();
            endOnComment.set(e.isComment());
            builder.append('\n').append(indentation).append(indentIncrement);
            if (!e.isComment()) {
                char c = options.keyQuoteChar();
                String s = c == 0 ? "%s: " : "%2$c%s%2$c: ";
                builder.append(String.format(s, k, c));
            }
            builder.append(e.stringify(parseSet, String.format("%s%s",indentation,indentIncrement),indentIncrement, stack));
            if (index.get() < size() && !e.isComment()) {
                lastComma.set(builder.length());
                builder.append(", ");
            }
        }
        if (endOnComment.get() && lastComma.get() != 0) builder.deleteCharAt(lastComma.get());
        builder.append('\n').append(indentation);
        builder.append('}');
        stack.unstack(this.hashCode());
        return builder.toString();
    }

    @Override
    protected ExplorationStack explore(ExplorationStack stack) {
        boolean b = stack.add(this);
        if (b) {
            this.withId(this.hashCode());
        }
        for (JsonNode node : this.nodeSet) {
            node.value.explore(stack);
        }
        stack.remove(this);
        return stack;
    }

    // iteration

    /**
     * Returns a {@link Set} of all the key-value associations
     * in the object, thus comments excluded.
     * @return a set of all non-comment children
     */
    public Set<JsonNode> entrySet() {
        return new HashSet<>(this.nodeSet);
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
    public void forEach(BiConsumer<String, ? super JsonElement> action) {
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
    public void forAll(BiConsumer<String, ? super JsonElement> action) {
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
    public void forAllComments(Consumer<JsonComment> action) {
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
