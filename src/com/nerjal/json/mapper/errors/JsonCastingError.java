package com.nerjal.json.mapper.errors;

import com.nerjal.json.elements.JsonElement;

public class JsonCastingError extends Exception {
    public JsonCastingError(JsonElement element, Class<?> target) {
        super("JSON " + element.typeToString() + " cannot be cast to " + target.getName());
    }
}
