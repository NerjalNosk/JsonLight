package io.github.nerjalnosk.jsonlight.mapper;

import io.github.nerjalnosk.jsonlight.elements.JsonElement;
import io.github.nerjalnosk.jsonlight.mapper.errors.CreationException;

public final class CreationAccessor {
    private CreationAccessor() {}

    public static <T, U> T resolveDefaultProvider(DefaultProvider provider, U instance, JsonElement element) throws CreationException {
        return CreationEngine.resolveDefaultProvider(provider, instance, element);
    }

    public static <T> T resolveEnumByName(String name, Class<T> type) throws CreationException {
        return CreationEngine.resolveEnumByName(name, type);
    }

    public static <T> T resolveEnumByNumber(int index, Class<T> type) throws CreationException {
        return CreationEngine.resolveEnumByNumber(index, type);
    }
}
