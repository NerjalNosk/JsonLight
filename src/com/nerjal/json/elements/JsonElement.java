package com.nerjal.json.elements;

import com.nerjal.json.JsonError;
import com.nerjal.json.JsonError.RecursiveJsonElementException;

import java.util.Arrays;

/**
 * This is the base class for all possible JSON element
 * in this API. It defines some strictly required methods
 * in order to properly manipulate JSON elements. All
 * further functionalities are specified in the different
 * classes themselves.
 */
public abstract class JsonElement {
    private JsonComment[] comments = new JsonComment[]{};

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
     * @return whether or not the element is a {@link JsonObject}
     */
    public boolean isJsonObject() {
        return false;
    }

    /**
     * @return whether or not the element is a {@link JsonArray}
     */
    public boolean isJsonArray() {
        return false;
    }

    /**
     * @return whether or not the element is a {@link JsonString}
     */
    public boolean isString() {
        return false;
    }

    /**
     * @return whether or not the element is a {@link JsonNumber}
     */
    public boolean isNumber() {
        return false;
    }

    /**
     * @return whether or not the element is a {@link JsonBoolean}
     */
    public boolean isBoolean() {
        return false;
    }


    /**
     * @return whether or not the element is a {@link JsonString},
     *         a {@link JsonNumber} or a {@link JsonBoolean}
     */
    public boolean isPrimitive() {
        return false;
    }

    /**
     * @return whether or not the element is a {@link JsonComment}
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
        throw new JsonError.JsonElementTypeException(String.format("%s is not an Object element",this.getClass().getName()));
    }

    /**
     * Gives off the JsonArray of the element.<br>
     * Has the same effect as casting the element.
     * @return The JsonArray cast of the element
     * @throws JsonError.JsonElementTypeException if the element isn't
     *         a JsonArray
     */
    public JsonArray getAsJsonArray() throws JsonError.JsonElementTypeException {
        throw new JsonError.JsonElementTypeException(String.format("%s is not an Array element",this.getClass().getName()));
    }

    /**
     * Gives off the JsonComment of the element.<br>
     * Has the same effect as casting the element.
     * @return The JsonComment cast of the element
     * @throws JsonError.JsonElementTypeException if the element isn't
     *         a JsonComment
     */
    public JsonComment getAsJsonComment() throws JsonError.JsonElementTypeException {
        throw new JsonError.JsonElementTypeException(String.format("%s is not a Comment element", this.getClass().getName()));
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
        throw new JsonError.JsonElementTypeException(String.format("%s is not a String element",this.getClass().getName()));
    }

    /**
     * Gives off the Number value of the element.<br>
     * It is only valid for {@link JsonNumber} elements
     * @return The Number value of the element
     * @throws JsonError.JsonElementTypeException if the element doesn't
     *         have a numeral value
     */
    public Number getAsNumber() throws JsonError.JsonElementTypeException {
        throw new JsonError.JsonElementTypeException(String.format("%s is not a Number element",this.getClass().getName()));
    }

    /**
     * Gives off the integer value of the element.<br>
     * It is only valid for {@link JsonNumber} elements
     * @return The integer value of the element
     * @throws JsonError.JsonElementTypeException if the element doesn't
     *         have a numeral value
     */
    public int getAsInt() throws JsonError.JsonElementTypeException {
        throw new JsonError.JsonElementTypeException(String.format("%s is not an int element",this.getClass().getName()));
    }

    /**
     * Gives off the long value of the element.<br>
     * It is only valid for {@link JsonNumber} elements
     * @return The long value of the element
     * @throws JsonError.JsonElementTypeException if the element doesn't
     *         have a numeral value
     */
    public long getAsLong() throws JsonError.JsonElementTypeException {
        throw new JsonError.JsonElementTypeException(String.format("%s is not an int element",this.getClass().getName()));
    }

    /**
     * Gives off the float value of the element.<br>
     * It is only valid for {@link JsonNumber} elements
     * @return The float value of the element
     * @throws JsonError.JsonElementTypeException if the element doesn't
     *         have a numeral value
     */
    public float getAsFloat() throws JsonError.JsonElementTypeException {
        throw new JsonError.JsonElementTypeException(String.format("%s is not a Float element",this.getClass().getName()));
    }

    /**
     * Gives off the double value of the element.<br>
     * It is only valid for {@link JsonNumber} elements
     * @return The double value of the element
     * @throws JsonError.JsonElementTypeException if the element doesn't
     *         have a numeral value
     */
    public double getAsDouble() throws JsonError.JsonElementTypeException {
        throw new JsonError.JsonElementTypeException(String.format("%s is not an int element",this.getClass().getName()));
    }

    /**
     * Gives off the boolean value of the element.<br>
     * It is only valid for {@link JsonBoolean} elements
     * @return The boolean value of the element
     * @throws JsonError.JsonElementTypeException if the element doesn't
     *         have a numeral value
     */
    public boolean getAsBoolean() throws JsonError.JsonElementTypeException {
        throw new JsonError.JsonElementTypeException(String.format("%s is not a Boolean element",this.getClass().getName()));
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
    abstract String stringify(String indentation, String indentIncrement, JsonStringifyStack stack)
            throws RecursiveJsonElementException;

    /**
     * Returns the JSON String corresponding to this JsonElement, recursively
     * parsing the internal elements if there are, with the according base
     * indentation and level indent incrementation
     * @param indentation the base indent at which should be the element,
     *                    hence the one-lower level of its children
     * @param indentIncrement the string to increment to the indentation at
     *                        each indentation level, recursively to the
     *                        element's children as well as their own, etc.
     * @return The foolproof security check to avoid recursive
     *         stringification. Can be null.
     * @throws RecursiveJsonElementException If the element or one of its
     *         children contains an element already in the stack or
     *         one of themselves, which would end up in loop parsing.
     */
    public final String stringify(String indentation, String indentIncrement) throws RecursiveJsonElementException {
        return this.stringify(indentation, indentIncrement, new JsonStringifyStack(this));
    }

    /**
     * Returns the JSON String corresponding to this JsonElement, recursively
     * parsing the internal elements if there are, with the according base
     * indentation and a double space default per-level indent incrementation
     * @param indentation the base indent at which should be the element,
     *                    hence the one-lower level of its children
     * @return The foolproof security check to avoid recursive
     *         stringification. Can be null.
     * @throws RecursiveJsonElementException If the element or one of its
     *         children contains an element already in the stack or
     *         one of themselves, which would end up in loop parsing.
     */
    public final String stringify(String indentation) throws RecursiveJsonElementException {
        return this.stringify(indentation, "  ", new JsonStringifyStack(this));
    }

    /**
     * Returns the JSON String corresponding to this JsonElement, recursively
     * parsing the internal elements if there are, with the according base
     * per-level indent incrementation, at a null base indentation
     * @param indentIncrement the string to increment to the indentation at
     *                        each indentation level, recursively to the
     *                        element's children as well as their own, etc.
     * @return The foolproof security check to avoid recursive
     *         stringification. Can be null.
     * @throws RecursiveJsonElementException If the element or one of its
     *         children contains an element already in the stack or
     *         one of themselves, which would end up in loop parsing.
     */
    public final String stringifyRoot(String indentIncrement) throws RecursiveJsonElementException {
        return this.stringify("", indentIncrement, new JsonStringifyStack(this));
    }

    /**
     * Returns the JSON String corresponding to this JsonElement, recursively
     * parsing the internal elements if there are, with a null base indentation
     * and a double space per-level indent incrementation.
     * @return The foolproof security check to avoid recursive
     *         stringification. Can be null.
     * @throws RecursiveJsonElementException If the element or one of its
     *         children contains an element already in the stack or
     *         one of themselves, which would end up in loop parsing.
     */
    public final String stringify() throws RecursiveJsonElementException {
        return this.stringify("", "  ", new JsonStringifyStack(this));
    }
}
