package io.github.nerjalnosk.jsonlight.mapper.errors;

import io.github.nerjalnosk.jsonlight.elements.JsonString;
import io.github.nerjalnosk.jsonlight.mapper.JsonMapper;

/**
 * {@link JsonMapper} error for Enum handling.
 * Thrown when no constant matches the specified name.
 */
public class JsonValueError extends JsonMapperError {
    public JsonValueError(JsonString string, Class<?> target) {
        super("Not a name of enum " + target.getName(), string);
    }
}
