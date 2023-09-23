package io.github.nerjalnosk.jsonlight.elements;

import io.github.nerjalnosk.jsonlight.JsonError;
import io.github.nerjalnosk.jsonlight.JsonError.RecursiveJsonElementException;
import io.github.nerjalnosk.jsonlight.parser.options.ParseSet;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

/**
 * This is the base class for all possible JSON element
 * in this API. It defines some strictly required methods
 * in order to properly manipulate JSON elements. All
 * further functionalities are specified in the different
 * classes themselves.
 * @author nerjal
 */
public abstract class JsonElement implements Serializable {
    private JsonComment[] comments = new JsonComment[]{};
    private Long id;

    protected JsonElement() {}

    protected JsonElement(long id) {
        this.id = id;
    }

    /**
     * Returns the element's reference ID.
     * @return the element's reference ID.
     */
    public final Optional<Long> getId() {
        return Optional.of(this.id);
    }

    /**
     * Sets the element's ID if it doesn't already have one.
     * @param l The theoretical element's new reference ID.
     *          Must be strictly positive.
     */
    public final void withId(long l) {
        if (this.id == null && l > 0) {
            this.id = l;
        }
    }

    /**
     * For a cleaner exception code
     * @param targetType the expected type for a proper
     *                   method execution
     * @return The exception to be thrown
     */
    protected final JsonError.JsonElementTypeException buildTypeError(String targetType) {
        return new JsonError.JsonElementTypeException(
                String.format("%s is not a %s element", this.getClass().getName(), targetType)
        );
    }

    /**
     * Adds a comment to the element's root comments
     * @param comment the comment to add
     */
    public final void addRootComment(JsonComment comment) {
        int size = this.comments.length;
        this.comments = Arrays.copyOf(this.comments,this.comments.length+1);
        this.comments[size] = comment;
    }

    /**
     * Adds comments to the element's root comments
     * @param comments the comments to add
     */
    public final void addRootComments(JsonComment[] comments) {
        int size = this.comments.length;
        this.comments = Arrays.copyOf(this.comments, size+comments.length);
        for (JsonComment comment : comments) {
            this.comments[size] = comment;
            size++;
        }
    }

    /**
     * Getter for the element's root comments
     * @return the array of the element's root comments
     */
    public final JsonComment[] getRootComments() {
        return this.comments;
    }

    /**
     * Clears the element's root comments
     */
    public void clearRootComment() {
        this.comments = new JsonComment[]{};
    }

    /**
     * @return whether the element is a {@link JsonObject}
     */
    public boolean isJsonObject() {
        return false;
    }

    /**
     * @return whether the element is a {@link JsonArray}
     */
    public boolean isJsonArray() {
        return false;
    }

    /**
     * @return whether the element is a {@link JsonString}
     */
    public boolean isString() {
        return false;
    }

    /**
     * @return whether the element is a {@link JsonNumber}
     */
    public boolean isNumber() {
        return false;
    }

    /**
     * @return whether the element is a {@link JsonBoolean}
     */
    public boolean isBoolean() {
        return false;
    }


    /**
     * @return whether the element is a {@link JsonString},
     *         a {@link JsonNumber} or a {@link JsonBoolean}
     */
    public boolean isPrimitive() {
        return false;
    }

    /**
     * @return whether the element is a {@link JsonComment}
     */
    public boolean isComment() {
        return false;
    }


    /**
     * @return the element' type's name
     */
    public abstract String typeToString();

    /**
     * Gives off the JsonObject of the element.<br>
     * Has the same effect as casting the element.
     * @return The JsonObject cast of the element
     * @throws JsonError.JsonElementTypeException if the element isn't
     *         a JsonObject
     */
    public JsonObject getAsJsonObject() throws JsonError.JsonElementTypeException {
        throw buildTypeError("Object");
    }

    /**
     * Gives off the JsonArray of the element.<br>
     * Has the same effect as casting the element.
     * @return The JsonArray cast of the element
     * @throws JsonError.JsonElementTypeException if the element isn't
     *         a JsonArray
     */
    public JsonArray getAsJsonArray() throws JsonError.JsonElementTypeException {
        throw buildTypeError("Array");
    }

    /**
     * Gives off the JsonComment of the element.<br>
     * Has the same effect as casting the element.
     * @return The JsonComment cast of the element
     * @throws JsonError.JsonElementTypeException if the element isn't
     *         a JsonComment
     */
    public JsonComment getAsJsonComment() throws JsonError.JsonElementTypeException {
        throw buildTypeError("Comment");
    }

    /**
     * Gives off the JsonNumber of the element.<br>
     * Has the same effect as casting the element.
     * @return The JsonNumber cast of the element
     * @throws JsonError.JsonElementTypeException if the element isn't
     *         a JsonNumber
     */
    public JsonNumber getAsJsonNumber() throws JsonError.JsonElementTypeException {
        throw buildTypeError("Number");
    }

    /**
     * Gives off the JsonString of the element.<br>
     * Has the same effect as casting the element.
     * @return The JsonString cast of the element
     * @throws JsonError.JsonElementTypeException if the element isn't
     *         a JsonString
     */
    public JsonString getAsJsonString() throws JsonError.JsonElementTypeException {
        throw buildTypeError("String");
    }

    /**
     * Gives off the String value of the element.<br>
     * It is only valid for string valued elements such as
     * {@link JsonString} and {@link JsonComment}
     * @return The String value of the element
     * @throws JsonError.JsonElementTypeException if the element doesn't
     *         have a string value
     */
    public String getAsString() throws JsonError.JsonElementTypeException {
        throw buildTypeError("String");
    }

    /**
     * Gives off the Number value of the element.<br>
     * It is only valid for {@link JsonNumber} elements
     * @return The Number value of the element
     * @throws JsonError.JsonElementTypeException if the element doesn't
     *         have a numeral value
     */
    public Number getAsNumber() throws JsonError.JsonElementTypeException {
        throw buildTypeError("Number");
    }

    /**
     * Gives off the integer value of the element.<br>
     * It is only valid for {@link JsonNumber} elements
     * @return The integer value of the element
     * @throws JsonError.JsonElementTypeException if the element doesn't
     *         have a numeral value
     */
    public int getAsInt() throws JsonError.JsonElementTypeException {
        throw buildTypeError("Int");
    }

    /**
     * Gives off the long value of the element.<br>
     * It is only valid for {@link JsonNumber} elements
     * @return The long value of the element
     * @throws JsonError.JsonElementTypeException if the element doesn't
     *         have a numeral value
     */
    public long getAsLong() throws JsonError.JsonElementTypeException {
        throw buildTypeError("Long");
    }

    /**
     * Gives off the float value of the element.<br>
     * It is only valid for {@link JsonNumber} elements
     * @return The float value of the element
     * @throws JsonError.JsonElementTypeException if the element doesn't
     *         have a numeral value
     */
    public float getAsFloat() throws JsonError.JsonElementTypeException {
        throw buildTypeError("Float");
    }

    /**
     * Gives off the double value of the element.<br>
     * It is only valid for {@link JsonNumber} elements
     * @return The double value of the element
     * @throws JsonError.JsonElementTypeException if the element doesn't
     *         have a numeral value
     */
    public double getAsDouble() throws JsonError.JsonElementTypeException {
        throw buildTypeError("Double");
    }

    /**
     * Gives off the boolean value of the element.<br>
     * It is only valid for {@link JsonBoolean} elements
     * @return The boolean value of the element
     * @throws JsonError.JsonElementTypeException if the element doesn't
     *         have a numeral value
     */
    public boolean getAsBoolean() throws JsonError.JsonElementTypeException {
        throw buildTypeError("Boolean");
    }

    /**
     * Returns the JSON String corresponding to this JsonElement, recursively
     * parsing the internal elements if there are, with the according base
     * indentation and level indent incrementation.
     * @param indentation the base indentation at which should be the element,
     *                    hence the one-lower level of its children
     * @param indentIncrement the string to increment to the indentation at
     *                        each indentation level, recursively to the
     *                        element's children as well as their own, etc.
     * @param stack The foolproof security check to avoid recursive
     *              stringification. Can be null.
     * @return The JSON String corresponding to this element.
     * @throws RecursiveJsonElementException If the element or one of its
     *         children contains an element already in the stack or
     *         one of themselves, which would end up in loop parsing.
     */
    protected abstract String stringify(ParseSet parseSet, String indentation, String indentIncrement, ExplorationStack stack)
            throws RecursiveJsonElementException;

    /**
     * Returns the JSON String corresponding to this JsonElement, recursively
     * parsing the internal elements if there are, with the according base
     * indentation and level indent incrementation.
     * @param parseSet the default stringification options for this object
     *                 and its children.
     * @param indentation the base indent at which should be the element,
     *                    hence the one-lower level of its children
     * @param indentIncrement the string to increment to the indentation at
     *                        each indentation level, recursively to the
     *                        element's children as well as their own, etc.
     * @return The JSON String corresponding to this element.
     * @throws RecursiveJsonElementException If the element or one of its
     *         children contains an element already in the stack or
     *         one of themselves, which would end up in loop parsing.
     */
    public final String stringify(ParseSet parseSet, String indentation, String indentIncrement)
            throws RecursiveJsonElementException {
        return this.stringify(parseSet, indentation, indentIncrement, this.explore());
    }

    /**
     * Returns the JSON String corresponding to this JsonElement, recursively
     * parsing the internal elements if there are, with the according base
     * indentation and level indent incrementation
     * @param indentation the base indent at which should be the element,
     *                    hence the one-lower level of its children
     * @param indentIncrement the string to increment to the indentation at
     *                        each indentation level, recursively to the
     *                        element's children as well as their own, etc.
     * @return The JSON String corresponding to this element.
     * @throws RecursiveJsonElementException If the element or one of its
     *         children contains an element already in the stack or
     *         one of themselves, which would end up in loop parsing.
     */
    public final String stringify(String indentation, String indentIncrement) throws RecursiveJsonElementException {
        return this.stringify(new ParseSet(), indentation, indentIncrement, this.explore());
    }

    /**
     * Returns the JSON String corresponding to this JsonElement, recursively
     * parsing the internal elements if there are, with the according base
     * indentation and level indent incrementation
     * @param indentation the base indent at which should be the element,
     *                    hence the one-lower level of its children
     * @param parseSet the default stringification options for this object
     *                 and its children.
     * @return The JSON String corresponding to this element.
     * @throws RecursiveJsonElementException If the element or one of its
     *         children contains an element already in the stack or
     *         one of themselves, which would end up in loop parsing.
     */
    public final String stringify(ParseSet parseSet, String indentation) throws RecursiveJsonElementException {
        return this.stringify(parseSet, indentation, "  ", this.explore());
    }

    /**
     * Returns the JSON String corresponding to this JsonElement, recursively
     * parsing the internal elements if there are, with a null base indentation
     * and a double space per-level indent incrementation.
     * @param parseSet the default stringification options for this object
     *                 and its children.
     * @return The JSON String corresponding to this element.
     * @throws RecursiveJsonElementException If the element or one of its
     *         children contains an element already in the stack or
     *         one of themselves, which would end up in loop parsing.
     */
    public final String stringify(ParseSet parseSet) throws RecursiveJsonElementException {
        return this.stringify(parseSet, "", "  ", this.explore());
    }

    /**
     * Returns the JSON String corresponding to this JsonElement, recursively
     * parsing the internal elements if there are, with the according base
     * indentation and a double space default per-level indent incrementation
     * @param indentation the base indent at which should be the element,
     *                    hence the one-lower level of its children
     * @return The JSON String corresponding to this element.
     * @throws RecursiveJsonElementException If the element or one of its
     *         children contains an element already in the stack or
     *         one of themselves, which would end up in loop parsing.
     */
    public final String stringify(String indentation) throws RecursiveJsonElementException {
        return this.stringify(new ParseSet(), indentation, "  ", this.explore());
    }

    /**
     * Returns the JSON String corresponding to this JsonElement, recursively
     * parsing the internal elements if there are, with the according base
     * per-level indent incrementation, at a null base indentation
     * @param parseSet the default stringification options for this object
     *                 and its children.
     * @param indentIncrement the string to increment to the indentation at
     *                        each indentation level, recursively to the
     *                        element's children as well as their own, etc.
     * @return The JSON String corresponding to this element.
     * @throws RecursiveJsonElementException If the element or one of its
     *         children contains an element already in the stack or
     *         one of themselves, which would end up in loop parsing.
     */
    public final String stringifyRoot(ParseSet parseSet, String indentIncrement) throws RecursiveJsonElementException {
        return this.stringify(parseSet, "", indentIncrement, this.explore());
    }

    /**
     * Returns the JSON String corresponding to this JsonElement, recursively
     * parsing the internal elements if there are, with the according base
     * per-level indent incrementation, at a null base indentation
     * @param indentIncrement the string to increment to the indentation at
     *                        each indentation level, recursively to the
     *                        element's children as well as their own, etc.
     * @return The JSON String corresponding to this element.
     * @throws RecursiveJsonElementException If the element or one of its
     *         children contains an element already in the stack or
     *         one of themselves, which would end up in loop parsing.
     */
    public final String stringifyRoot(String indentIncrement) throws RecursiveJsonElementException {
        return this.stringify(new ParseSet(), "", indentIncrement, this.explore());
    }

    /**
     * Returns the JSON String corresponding to this JsonElement, recursively
     * parsing the internal elements if there are, with a null base indentation
     * and a double space per-level indent incrementation.
     * @return The JSON String corresponding to this element.
     * @throws RecursiveJsonElementException If the element or one of its
     *         children contains an element already in the stack or
     *         one of themselves, which would end up in loop parsing.
     */
    public final String stringify() throws RecursiveJsonElementException {
        return this.stringify(new ParseSet(),"", "  ", this.explore());
    }

    /**
     * Element exploration stack, allows to explore for circular
     * structure detection.
     */
    protected static final class ExplorationStack {
        private ExplorationStack() {
            this.elements = new JsonElement[0];
            this.stringificationIds = new int[0];
        }

        /**
         * Not using an object, for the sake of performance.
         * Sorted by hashcode.
         */
        private JsonElement[] elements;
        private int[] stringificationIds;

        /**
         * Adds the provided element to the stack, if absent.
         * @param e The element to add to the stack.
         * @return Whether the element could be added to the stack. Only
         *         returns {@code false} if it was already present,
         *         returns {@code true} otherwise.
         */
        public boolean add(JsonElement e) {
            Objects.requireNonNull(e);
            int hash = e.hashCode();
            int i = 0;
            boolean add = false;
            // TODO: optimize finding element/placement
            while (i < this.elements.length) {
                int j = this.elements[i].hashCode();
                if (j < hash) i++;
                else {
                    if (j > hash) add = true;
                    break;
                }
            }
            if (add) {
                int l = this.elements.length;
                JsonElement[] arr = Arrays.copyOf(Arrays.copyOfRange(this.elements, 0, i), l+1);
                arr [i] = e;
                while (i < l) {
                    arr[i+1] = this.elements[i];
                    i++;
                }
                this.elements = arr;
            }
            return add;
        }

        /**
         * Removes the provided element from the stack, if present.
         * @param e The element to remove from the stack.
         */
        public void remove(JsonElement e) {
            Objects.requireNonNull(e);
            int hash = e.hashCode();
            int i = 0;
            boolean slice = false;
            while (i < this.elements.length) {
                int j = this.elements[i].hashCode();
                if (j < hash) i++;
                else {
                    if (j == hash) slice = true;
                    break;
                }
            }
            if (slice) {
                int l = this.elements.length-1;
                JsonElement[] arr = Arrays.copyOf(Arrays.copyOfRange(this.elements, 0, i-1), l);
                while (i < l) {
                    arr[i] = this.elements[i+1];
                    i++;
                }
                this.elements = arr;
            }
        }

        /**
         * Returns whether the provided hash is already in the stack.
         * @param hash The hash to look for in the stack.
         * @return Whether the provided hash is already in the stack.
         */
        boolean has(int hash) {
            int i = 0;
            do {
                int h = this.elements[i].hashCode();
                if (h == hash) return true;
                if (h > hash) return false;
                i++;
            } while (i < this.elements.length);
            return false;
        }

        /**
         * Returns whether the provided element is already in the stack.
         * @param e The element to look for in the stack.
         * @return Whether the provided element is already in the stack.
         */
        public boolean has(JsonElement e) {
            Objects.requireNonNull(e);
            return has(e.hashCode());
        }

        @SuppressWarnings("DuplicatedCode")
        public boolean stack(int i) {
            final int h = this.stringificationIds.length;
            // if extremes = target
            if (this.stringificationIds[0] == i || this.stringificationIds[h-1] == i) return true;
            // if lower end over target
            if (this.stringificationIds[0] > i) {
                int[] arr = new int[h+1];
                arr[0] = i;
                System.arraycopy(this.stringificationIds, 0, arr, 1, h);
                this.stringificationIds = arr;
                return false;
            }
            // if higher end below target
            if (this.stringificationIds[h-1] < i) {
                int[] arr = Arrays.copyOf(this.stringificationIds, h+1);
                arr[h] = i;
                this.stringificationIds = arr;
                return false;
            }
            int k = h/2;
            int d = k;
            int p = 0;
            // bubble search for target/pos
            while (d >= 1) {
                int n = this.stringificationIds[k];
                if (n == i) return true;
                int t = k;
                if (n < i) k += Math.abs(k-p)/2;
                else k -= Math.abs(k-p)/2;
                d = Math.abs(t-k);
                p = t;
            }
            int[] arr = Arrays.copyOf(Arrays.copyOfRange(this.stringificationIds, 0, k), h+1);
            arr[k] = i;
            System.arraycopy(this.stringificationIds, k, arr, k+1, h-k);
            this.stringificationIds = arr;
            return false;
        }

        @SuppressWarnings("DuplicatedCode")
        public void unstack(int i) {
            final int h = this.stringificationIds.length;
            if (this.stringificationIds[0] > i || this.stringificationIds[h-1] < i) return;
            // bubble search
            int k = h/2;
            int d = k;
            int p = 0;
            while (d >= 1) {
                int n = this.stringificationIds[k];
                if (n == i) {
                    int[] arr = Arrays.copyOf(Arrays.copyOfRange(this.stringificationIds, 0, k), h-1);
                    System.arraycopy(this.stringificationIds, k+1, arr, k, h-k-1);
                    this.stringificationIds = arr;
                    return;
                }
                int t = k;
                if (n < i) k += Math.abs(k-p)/2;
                else k -= Math.abs(k-p)/2;
                d = Math.abs(t-k);
                p = t;
            }
        }
    }

    protected final String stringifiedId() {
        return String.format("<@%d>",this.id);
    }

    protected final String asRef() {
        return String.format("<#%d>",this.id);
    }

    protected final ExplorationStack explore() {
        return this.explore(new ExplorationStack());
    }

    protected ExplorationStack explore(ExplorationStack stack) {
        return null;
    }
}
