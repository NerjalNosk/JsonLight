package com.github.nerjalnosk.jsonlight.mapper.errors;

import com.github.nerjalnosk.jsonlight.elements.JsonElement;

public class JsonCastingError extends Exception {
    public JsonCastingError(JsonElement element, Class<?> target) {
        super("JSON " + element.typeToString() + " cannot be cast to " + target.getName());
    }
}
