package com.github.nerjalnosk.jsonlight.mapper.errors;

import com.github.nerjalnosk.jsonlight.elements.JsonString;
import com.github.nerjalnosk.jsonlight.mapper.JsonMapper;

/**
 * {@link JsonMapper} error for Enum handling.
 * Thrown when no constant matches the specified name.
 */
public class JsonValueError extends Exception {
    public JsonValueError(JsonString string, Class<?> target) {
        super("JSON " + string.getAsString() + "is not a " + target.getName() + " value;");
    }
}
