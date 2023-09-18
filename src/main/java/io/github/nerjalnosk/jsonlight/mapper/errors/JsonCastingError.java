package io.github.nerjalnosk.jsonlight.mapper.errors;

import io.github.nerjalnosk.jsonlight.elements.JsonElement;

public class JsonCastingError extends Exception {
    public JsonCastingError(JsonElement element, Class<?> target) {
        super("JSON " + element.typeToString() + " cannot be cast to " + target.getName());
    }
}
