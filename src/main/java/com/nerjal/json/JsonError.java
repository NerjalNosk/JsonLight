package com.nerjal.json;

import com.nerjal.json.elements.*;

/**
 * All exception classes used by the generic JsonLight API<br>
 * (doesn't include Mapper exceptions)
 * @author Nerjal Nosk
 * @since JDK 16
 */
public abstract class JsonError {
    /**
     * Thrown when trying to get a typed element from a
     * {@link JsonElement} which isn't of the corresponding type.
     * @see JsonElement#getAsBoolean()
     * @see JsonElement#getAsInt()
     * @see JsonElement#getAsFloat()
     * @see JsonElement#getAsLong()
     * @see JsonElement#getAsDouble()
     * @see JsonElement#getAsString()
     * @see JsonElement#getAsJsonArray()
     * @see JsonElement#getAsJsonObject()
     * @see JsonElement#getAsJsonComment()
     */
    public static class JsonElementTypeException extends Exception {
        public JsonElementTypeException(String s) {
            super(s);
        }
    }

    /**
     * Thrown when trying to get a child {@link JsonElement} from
     * a {@link JsonObject} which doesn't have any for the given key.
     * @see JsonObject#get(String)
     */
    public static class ChildNotFoundException extends Exception {
        public ChildNotFoundException(String s) {
            super(s);
        }
    }

    /**
     * Thrown when trying to parse an invalid String to a
     * {@link JsonElement}
     * @see com.nerjal.json.parser.StringParser
     */
    public static class JsonParseException extends Exception {
        public JsonParseException(String s) {
            super(s);
        }
        public JsonParseException(Exception e) {
            super(e);
        }
    }

    /**
     * Thrown when setting a {@link JsonArray} to stringify with
     * an invalid number of elements per line (negative or null)
     * @see com.nerjal.json.parser.options.ArrayParseOptions
     */
    public static class IllegalLineElementsNumberException extends Exception{
        public IllegalLineElementsNumberException(String s) {
            super(s);
        }
    }

    /**
     * Thrown when trying to stringify a JsonElement containing
     * itself.
     * @see JsonElement#stringify()
     */
    public static class RecursiveJsonElementException extends Exception {
        public RecursiveJsonElementException(String s) {
            super(s);
        }
    }

    /**
     * Thrown when trying to parse an invalid file
     * to
     * a {@link JsonElement}
     * @see com.nerjal.json.parser.FileParser
     */
    public static class FileNotFoundException extends JsonParseException{
        public FileNotFoundException(String s) {
            super(s);
        }
        public FileNotFoundException(Exception e) {
            super(e);
        }
    }
}
