package io.github.nerjalnosk.jsonlight;

import io.github.nerjalnosk.jsonlight.elements.JsonArray;
import io.github.nerjalnosk.jsonlight.elements.JsonElement;
import io.github.nerjalnosk.jsonlight.elements.JsonObject;
import io.github.nerjalnosk.jsonlight.parser.FileParser;
import io.github.nerjalnosk.jsonlight.parser.StringParser;
import io.github.nerjalnosk.jsonlight.parser.options.ArrayParseOptions;

/**
 * All exception classes used by the generic JsonLight API<br>
 * (doesn't include Mapper exceptions)
 * @author nerjal
 * @since JDK 16
 */
public abstract class JsonError {
    private JsonError() {}

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

    public static class JsonMappingException extends Exception {
        public JsonMappingException(Throwable t) {
            super(t);
        }

        public JsonMappingException(String s, Throwable t) {
            super(s, t);
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
     * @see StringParser
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
     * Thrown when trying to parse a feature that has been
     * disabled via the current
     * {@link io.github.nerjalnosk.jsonlight.parser.ParserOptions}
     * @see StringParser
     */
    public static class DisabledFeatureException extends JsonParseException {
        public DisabledFeatureException(String s) {
            super(s);
        }

        public DisabledFeatureException(Exception e) {
            super(e);
        }
    }

    /**
     * Thrown when setting a {@link JsonArray} to stringify with
     * an invalid number of elements per line (negative or null)
     * @see ArrayParseOptions
     */
    public static class IllegalLineElementsNumberException extends Exception {
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
     * @see FileParser
     */
    public static class FileNotFoundException extends JsonParseException {
        public FileNotFoundException(String s) {
            super(s);
        }
        public FileNotFoundException(Exception e) {
            super(e);
        }
    }

    public static class NoSuchIdCircularJsonException extends JsonParseException {
        public NoSuchIdCircularJsonException(String s) {
            super(s);
        }
    }
}
