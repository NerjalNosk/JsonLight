package com.nerjal.json.mapper.errors;

import com.nerjal.json.elements.JsonString;

/**
 * {@link com.nerjal.json.mapper.JsonMapper} error for Enum handling.
 * Thrown when no constant matches the specified name.
 */
public class JsonValueError extends Exception {
    public JsonValueError(JsonString string, Class<?> target) {
        super("JSON " + string.getAsString() + "is not a " + target.getName() + " value;");
    }
}
