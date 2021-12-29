package com.nerjal.json.mapper;

import com.nerjal.json.elements.JsonElement;

public class JsonCastingError extends Exception {
    JsonCastingError(JsonElement element, Class<?> target) {
        super();
    }
}
