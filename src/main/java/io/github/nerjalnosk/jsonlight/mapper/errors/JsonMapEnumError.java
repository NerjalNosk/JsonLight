package io.github.nerjalnosk.jsonlight.mapper.errors;

import io.github.nerjalnosk.jsonlight.elements.JsonElement;

public class JsonMapEnumError extends JsonMapperError {
    public final Class<?> enumClass;

    public JsonMapEnumError(JsonElement element, Class<?> enumClass) {
        super(element);
        this.enumClass = enumClass;
    }

    public JsonMapEnumError(String s, JsonElement element, Class<?> enumClass) {
        super(s, element);
        this.enumClass = enumClass;
    }

    public JsonMapEnumError(Throwable t, JsonElement element, Class<?> enumClass) {
        super(t, element);
        this.enumClass = enumClass;
    }

    public JsonMapEnumError(String s, Throwable t, JsonElement element, Class<?> enumClass) {
        super(s, t, element);
        this.enumClass = enumClass;
    }
}
