package io.github.nerjalnosk.jsonlight.elements;

import io.github.nerjalnosk.jsonlight.JsonError;
import io.github.nerjalnosk.jsonlight.JsonError.RecursiveJsonElementException;
import io.github.nerjalnosk.jsonlight.parser.options.ArrayParseOptions;
import io.github.nerjalnosk.jsonlight.parser.options.ParseSet;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

/**
 * <p>An object that lists JSON values, allowing to
 * build arrays for a JSON structure.
 * </p>
 * <p>The object provides list of values, accessible
 * with their indexes in the array, and iterable
 * using most common methods. Eg.</p>
 * <pre>{@code
 *  JsonArray.forEach(UnaryConsumer<JsonElement>)
 * }
 * {@code
 *  for (JsonElement : JsonArray) {}}
 * {@code
 *  while (JsonArray.iterator.hasnext() {
 *      JsonArray.next();
 *  }}
 * </pre>
 * <p>Warning! Iteration with indexes includes
 * all {@link JsonComment} in the list. Thus
 * retaining indexes while iterating with the
 * included iterator might cause errors.<br>
 * <br></p>
 *
 * <p>The objects stringification relies on options
 * set with a {@link ArrayParseOptions} attribute,
 * defaulted to match the JSON4 syntax, which can
 * be replaced or instantiated to a different value.
 * <br>These options allow to set how many elements
 * are put on each line, from one to putting all
 * in one line. (exception being for single-line
 * comments, which are still followed by a line
 * break)
 * </p>
 * @author nerjal
 */
public class JsonArray extends JsonElement implements Iterable<JsonElement> {
    private final List<JsonElement> list;
    private final Set<JsonComment> commentSet;
    private ArrayParseOptions parseOptions;
    protected transient int modCount = 0;

    /**
     * An empty JsonArray with default stringification options
     */
    public JsonArray() {
        this.list = new ArrayList<>();
        this.commentSet = new HashSet<>();
        this.parseOptions = new ArrayParseOptions();
    }

    /**
     * A JsonArray with the given {@link Collection} of
     * JsonElement as content, and default stringification
     * options.
     * @param elements the {@link JsonElement} collection
     *                 to fill the array with as default
     *                 values.
     */
    public JsonArray(Collection<JsonElement> elements) {
        this.list = new ArrayList<>(elements);
        this.commentSet = new HashSet<>();
        elements.forEach(e -> {
            if (e.isComment())
                commentSet.add((JsonComment) e);
        });
        this.parseOptions = new ArrayParseOptions();
    }
    /**
     * An empty JsonArray with the specified
     * stringification options.
     * @param options the options to use for the array's
     *      *                stringification methods
     */
    public JsonArray(ArrayParseOptions options) {
        this.list = new ArrayList<>();
        this.commentSet = new HashSet<>();
        this.parseOptions = options;
    }
    /**
     * A JsonArray with the given {@link Collection} of
     * JsonElement as content, and the specified
     * stringification options.
     * @param elements the {@link JsonElement} collection
     *                 to fill the array with as default
     *                 values.
     * @param options the options to use for the array's
     *                stringification methods
     */
    public JsonArray(Collection<JsonElement> elements, ArrayParseOptions options) {
        this.list = new ArrayList<>(elements);
        this.commentSet = new HashSet<>();
        elements.forEach(e -> {
            if (e.isComment())
                commentSet.add((JsonComment) e);
        });
        this.parseOptions = options;
    }

    /**
     * Changes the object's stringification options
     * @param options the new options to apply
     */
    public void setParseOptions(ArrayParseOptions options) {
        this.parseOptions = options;
    }

    /**
     * Returns the element of the array stored at the
     * specified index.
     * @param index the index of the element to return
     * @return the element stored in the array at the
     *         specified index
     * @throws IndexOutOfBoundsException if the index
     *         targets a position out of the array's
     *         boundaries.
     */
    public JsonElement get(int index) throws IndexOutOfBoundsException {
        return this.list.get(index);
    }

    /**
     * Returns the number stored at the specified index
     * in the array
     * @param index the index at which is stored the
     *              expected number
     * @return the number stored at the specified index
     * @throws IndexOutOfBoundsException if the index
     *         targets a position out of the array's
     *         boundaries
     * @throws JsonError.JsonElementTypeException if the
     *         element doesn't have a numeral value
     */
    public Number getNumber(int index) throws IndexOutOfBoundsException, JsonError.JsonElementTypeException {
        return this.get(index).getAsNumber();
    }

    /**
     * Returns the string stored at the specified index
     * in the array
     * @param index the index at which is stored the
     *              expected string
     * @return the number stored at the specified index
     * @throws IndexOutOfBoundsException if the index
     *         targets a position out of the array's
     *         boundaries
     * @throws JsonError.JsonElementTypeException if the
     *         element doesn't have a string value
     */
    public String getString(int index) throws IndexOutOfBoundsException, JsonError.JsonElementTypeException {
        return this.get(index).getAsString();
    }

    /**
     * Returns the boolean stored at the specified index
     * in the array
     * @param index the index at which is stored the
     *              expected boolean
     * @return the boolean stored at the specified index
     * @throws IndexOutOfBoundsException if the index
     *         targets a position out of the array's
     *         boundaries
     * @throws JsonError.JsonElementTypeException if the
     *         element doesn't have a boolean value
     */
    public boolean getBoolean(int index) throws IndexOutOfBoundsException, JsonError.JsonElementTypeException {
        return this.get(index).getAsBoolean();
    }

    /**
     * Returns the array stored at the specified index
     * in the array
     * @param index the index at which is stored the
     *              expected array
     * @return the array stored at the specified index
     * @throws IndexOutOfBoundsException if the index
     *         targets a position out of the array's
     *         boundaries
     * @throws JsonError.JsonElementTypeException if the
     *         element doesn't have an array value
     */
    public JsonArray getArray(int index) throws IndexOutOfBoundsException, JsonError.JsonElementTypeException {
        return this.get(index).getAsJsonArray();
    }

    /**
     * Returns the object stored at the specified index
     * in the array
     * @param index the index at which is stored the
     *              expected object
     * @return the object stored at the specified index
     * @throws IndexOutOfBoundsException if the index
     *         targets a position out of the array's
     *         boundaries
     * @throws JsonError.JsonElementTypeException if the
     *         element doesn't have an object value
     */
    public JsonObject getObject(int index) throws IndexOutOfBoundsException, JsonError.JsonElementTypeException {
        return this.get(index).getAsJsonObject();
    }

    /**
     * Returns an array of JsonElement stored in the
     * array between the specified indexes.
     * @param from the index of the first element of
     *             the array to return - inclusive
     * @param to the index + 1 of the last element
     *           of the array to return.
     * @return the array of element stored between
     *         the specified indexes
     * @throws IndexOutOfBoundsException if the
     *         specified indexes reach out of the
     *         array's boundaries
     * @throws IllegalArgumentException from > to
     */
    public JsonElement[] getAll(int from, int to) {
        return (JsonElement[]) Arrays.copyOfRange(this.list.toArray(), from, to);
    }

    /**
     * Returns an array of all the comments in this
     * array.
     * @return an array of all the comments in this
     *         array
     */
    public JsonComment[] getAllComments() {
        return (JsonComment[]) Arrays.copyOf(this.commentSet.toArray(), this.commentSet.size());
    }

    /**
     * Returns the number of elements in this list.<br>
     * If this list contains more than
     * {@code Integer.MAX_VALUE} elements, returns
     * {@code Integer.MAX_VALUE}
     * @return the number of elements in ths list
     */
    public int size() {
        return this.list.size();
    }

    /**
     * Removes the first occurrence (with the lowest
     * index) of the specified object in this list,
     * if it is present. The list is unchanged if no
     * occurrence is found.<br>
     * Returns {@code true} if the list contained the
     * specified element, {@code false} otherwise.
     * @param element the element to be removed from
     *                the list
     * @return whether the element could be removed
     *         or not
     * @throws NullPointerException if the specified
     *         element is {@code null}
     */
    public boolean remove(JsonElement element) {
        boolean b = this.list.remove(element);
        if (b) {
            modCount++;
            if (element.isComment())
                commentSet.remove((JsonComment) element);
        }
        return b;
    }

    /**
     * Removes the element at the specified position
     * in this list. All subsequent elements are
     * shifted to the left. Returns the element that
     * was removed from the list.
     * @param index the index of the element to be
     *              removed
     * @return the element previously at the specified
     * position
     * @throws IndexOutOfBoundsException if the index
     *         is out of range
     *         {@code (index < 0 || index >= size())}
     */
    public JsonElement remove(int index) {
        JsonElement e = this.list.remove(index);
        modCount++;
        if (e.isComment())
            commentSet.remove((JsonComment) e);
        return e;
    }

    /**
     * Removes the first occurrence (with the lowest
     * index) of each element in the specified collection
     * from this list if it is present. The list remains
     * unchanged for any absent element.<br>
     * Returns a collection of all elements that could be
     * removed from this list (empty if none were found)
     * @param elements the collection of elements to
     *                 remove the first occurrence of each
     * @return the collection of all elements removed
     *         from this list
     * @throws NullPointerException if the specified
     *         collection is {@code null} or contains
     *         a {@code null} element
     */
    public Collection<JsonElement> removeAll(Collection<JsonElement> elements) {
        List<JsonElement> returnList = new ArrayList<>();
        elements.forEach(e -> {
            if (list.remove(e)) {
                returnList.add(e);
                if (e.isComment())
                    commentSet.remove((JsonComment) e);
            }
        });
        modCount++;
        return returnList;
    }

    /**
     * Appends the specified element at the end of this
     * list
     * @param element element to be added to this list
     */
    public void add(JsonElement element) {
        this.list.add(element);
        if (element.isComment())
            commentSet.add((JsonComment) element);
        else {
            this.list.addAll(Arrays.asList(element.getRootComments()));
            element.clearRootComment();
        }
        modCount++;
    }

    /**
     * Inserts the specified element at the specified
     * position in this list. Shifts the element
     * currently at that position (if any) and any
     * subsequent elements to the right.
     * @param index the index at which the specified
     *              element is to be inserted
     * @param element the element to be inserted
     * @throws NullPointerException if the specified
     *         element is {@code null}
     * @throws IndexOutOfBoundsException if the
     *         specified index is out of range
     *         {@code (index < 0 || index > size())}
     */
    public void add(int index, JsonElement element) {
        this.list.add(index, element);
        int k = index+1;
        if (element.isComment()) {
            commentSet.add((JsonComment) element);
        } else for (JsonComment comment : element.getRootComments()) {
                this.list.add(k, comment);
                k++;
        }
        element.clearRootComment();
        modCount++;
    }

    /**
     * Appends all the elements in the specified
     * collection to this list, in the order they
     * are returned by the specified collection's
     * iterator.
     * @param elements the collection containing
     *                 elements to be added to
     *                 this list
     * @throws NullPointerException if the specified
     *         collection contains one or more
     *         {@code null} elements, or if the
     *         specified collection is {@code null}
     */
    public void addAll(Collection<JsonElement> elements) {
        this.list.addAll(elements);
        elements.forEach(e -> {
            if (e.isComment()) commentSet.add((JsonComment) e);
            else {
                list.addAll(Arrays.asList(e.getRootComments()));
                e.clearRootComment();
            }
        });
        modCount++;
    }

    /**
     * Appends all the specified elements to this
     * list, in the order they are given.
     * @param elements the elements to be added
     *                 to this list.
     * @throws NullPointerException if the specified
     *         array contains one or more {@code null}
     *         elements, or if the specified array is
     *         {@code null}
     */
    public void addAll(JsonElement[] elements) {
        this.addAll(Arrays.asList(elements));
        for (JsonElement e : elements) {
            if (e.isComment()) commentSet.add((JsonComment) e);
            else {
                list.addAll(Arrays.asList(e.getRootComments()));
                e.clearRootComment();
            }
        }
        modCount++;
    }

    /**
     * Replaces each element of this list with the
     * result of applying the operator to that element.
     * Errors or runtime exception thrown by the
     * operator are relayed to the caller.
     * @param operator the operator to apply to each
     *                 element
     */
    public void replaceAll(UnaryOperator<JsonElement> operator) {
        this.list.replaceAll(operator);
        modCount++;
    }

    /**
     * Pushes all the items of the specified array into this one.
     * @param array the array to push into this one. Will not be affected.
     */
    public void push(JsonArray array) {
        array.forEach(this::add);
    }

    /**
     * Pushes akk the items and comments of the specified
     * array into this one.
     * @param array the array to push into this one. Will not be affected.
     */
    public void pushAll(JsonArray array) {
        array.forAll(this::add);
    }

    /**
     * Creates a new JsonArray with the element and
     * unique comments of all the specified arrays.
     * Comments are compared via their hash.
     * @param a1 The first array to merge.
     *           Will not be affected.
     * @param a2 The second array to merge.
     *           Will not be affected.
     * @param arrays All the arrays to merge as well.
     *               None will be affected.
     * @return A new array containing the children and
     *         comments of all the specified arrays,
     *         in the order they are given.
     */
    public static JsonArray merge(JsonArray a1, JsonArray a2, JsonArray... arrays) {
        JsonArray arr = new JsonArray();
        arr.pushAll(a1);
        arr.push(a2);
        for (JsonComment comment : a2.commentSet) {
            if (arr.commentSet.contains(comment)) continue;
            arr.add(comment);
        }
        for (JsonArray array : arrays) {
            arr.push(array);
            for (JsonComment comment : array.commentSet) {
                if (arr.commentSet.contains(comment)) continue;
                arr.add(comment);
            }
        }
        return arr;
    }

    @Override
    public boolean isJsonArray() {
        return true;
    }
    @Override
    public String typeToString() {
        return "Array";
    }
    @Override
    public JsonArray getAsJsonArray() {
        return this;
    }
    @Override
    public String stringify(ParseSet parseSet, String indentation, String indentIncrement, JsonStringifyStack stack)
            throws RecursiveJsonElementException {
        if (parseSet == null) parseSet = new ParseSet();
        ArrayParseOptions setOptions = (ArrayParseOptions) parseSet.getOptions(this.getClass());
        ArrayParseOptions options = parseOptions.isChanged() ? parseOptions :
                setOptions == null ? parseOptions : setOptions;
        if (this.list.isEmpty()) return "[]";
        if (stack == null) stack = new JsonStringifyStack(this);
        StringBuilder builder = new StringBuilder("[");
        int count = 0;
        int index = 0;
        int lastComma = 0;
        boolean endOnComment = false;
        long maxLine = options.getNumPerLine();
        for (JsonElement e : this.list) {
            if (stack.hasOrAdd(e)) throw new RecursiveJsonElementException("Recursive JSON structure in JsonArray");
            if ((!options.isAllInOneLine() && (count >= maxLine || index == 0)) || e.isComment() ||
                    (index > 0 && list.get(index-1).isComment())) {
                builder.append('\n');
                builder.append(indentation).append(indentIncrement);
            }
            if (count == maxLine || e.isComment()) count = 0;
            index++;
            endOnComment = e.isComment();
            builder.append(e.stringify(parseSet, String.format("%s%s",indentation,indentIncrement),indentIncrement, stack));
            if (!e.isComment()) {
                count++;
                if (index < size()){
                    lastComma = builder.length();
                    builder.append(", ");
                }
            }
            stack.rem(e);
        }
        if (endOnComment && lastComma != 0) builder.deleteCharAt(lastComma);
        if (!parseOptions.isAllInOneLine()) builder.append('\n').append(indentation);
        builder.append(']');
        return builder.toString();
    }


    /* ITERATION */

    /**
     * Performs the given action for each element
     * of this list until all element have been
     * processed or the action throws an exception.
     * <br>This includes {@link JsonComment}
     * elements.<br>
     * Actions are performed in the order of
     * iteration. Exceptions thrown are relayed
     * to the caller.<br>
     * This method won't allow editing this list
     * but only its elements during iteration.
     * @param action the action to be performed
     *               for each element
     * @throws NullPointerException if the specified
     *         action is null
     * @throws UnsupportedOperationException if the
     *         action tries to append, remove, or
     *         replace elements in this list
     */
    public void forAll(Consumer<? super JsonElement> action) {
        this.list.forEach(action);
    }

    @Override
    public Iterator<JsonElement> iterator() {
        return new SimpleJArrayIterator();
    }

    /**
     * Performs the given action for each element
     * processed by the iterator until all of these
     * elements have been processed or the action
     * throws an exception.<br>
     * {@link JsonComment} elements are not
     * processed by this method.<br>
     * Actions are performed in the order of
     * iteration. Exceptions thrown are relayed
     * to the caller.<br>
     * This method won't allow editing this list
     * but only its elements during iteration.
     * @param action the action to be performed
     *               for each element
     * @throws NullPointerException if the specified
     *         action is null
     * @throws UnsupportedOperationException if the
     *         action tries to append, remove, or
     *         replace elements in this list
     */
    @Override
    public void forEach(Consumer<? super JsonElement> action) {
        Iterable.super.forEach(action);
    }

    /**
     * The {@link JsonArray} iterator class.<br>
     * Allows iterating through the array without
     * taking {@link JsonComment} elements in
     * consideration.
     */
    public class SimpleJArrayIterator implements Iterator<JsonElement> {
        /**
         * Index of element to be returned by subsequent
         * call to next.
         */
        int cursor = 0;

        /**
         * Index of element returned by most recent call
         * to next or previous.  Reset to -1 if this
         * element is deleted by a call to remove.
         */
        int lastRet = -1;

        /**
         * The modCount value that the iterator believes
         * that the backing List should have. If this
         * expectation is violated, the iterator has
         * detected concurrent modification.
         */
        int expectedModCount = modCount;

        /**
         * Private constructor to avoid outer instantiation
         */
        private SimpleJArrayIterator(){}


        @Override
        public boolean hasNext() {
            if (cursor == size()) return false;
            for (int i = cursor; i < size(); i++) {
                if (!list.get(i).isComment()) return true;
            }
            return false;
        }

        @Override
        public JsonElement next() {
            checkForComodification();
            try {
                JsonElement element = get(cursor);
                lastRet = cursor;
                cursor++;
                if (element.isComment()) return next();
                return element;
            } catch (IndexOutOfBoundsException e) {
                checkForComodification();
                throw new NoSuchElementException(e.toString());
            }
        }

        @Override
        public void forEachRemaining(Consumer<? super JsonElement> action) {
            Iterator.super.forEachRemaining(action);
        }


        public void remove() {
            if (lastRet < 0)
                throw new IllegalStateException();
            checkForComodification();

            try {
                JsonArray.this.remove(lastRet);
                if (lastRet < cursor)
                    cursor--;
                lastRet = -1;
                expectedModCount = modCount;
            } catch (IndexOutOfBoundsException e) {
                throw new ConcurrentModificationException();
            }
        }

        final void checkForComodification() {
            if (modCount != expectedModCount)
                throw new ConcurrentModificationException();
        }
    }
}
