package io.github.nerjalnosk.jsonlight.mapper.errors;

import io.github.nerjalnosk.jsonlight.elements.JsonElement;

public class JsonMapperTypeError extends JsonMapperError {
    public JsonMapperTypeError(JsonElement element) {
        super(element);
    }

    public JsonMapperTypeError(String s, JsonElement element) {
        super(s, element);
    }

    public JsonMapperTypeError(Throwable t, JsonElement element) {
        super(t, element);
    }

    public JsonMapperTypeError(String s, Throwable t, JsonElement element) {
        super(s, t, element);
    }
}
