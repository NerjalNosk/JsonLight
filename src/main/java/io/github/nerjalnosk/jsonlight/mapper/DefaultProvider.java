package io.github.nerjalnosk.jsonlight.mapper;

import io.github.nerjalnosk.jsonlight.elements.JsonElement;
import io.github.nerjalnosk.jsonlight.mapper.annotations.JsonDefaultProvider;

import java.lang.reflect.Method;

/**
 * Specification of a default entity provider entry, to be used
 * with {@link CreationAccessor#resolveDefaultProvider(DefaultProvider, Object, JsonElement)}
 */
public final class DefaultProvider {
    public final int priority;
    public final String value;
    public final Class<?> clazz;

    private DefaultProvider(int i, String s, Class<?> c) {
        this.priority = i;
        this.value = s;
        this.clazz = c;
    }

    public static DefaultProvider ofMethod(Method m) {
        return ofMethod(m, 1);
    }

    public static DefaultProvider ofMethod(Method m, int i) {
        return new DefaultProvider(i, m.getName(), m.getDeclaringClass());
    }

    public static DefaultProvider ofValue(String s) {
        return ofValue(s, 1);
    }

    public static DefaultProvider ofValue(String s, int i) {
        return new DefaultProvider(i, s, DefaultProvider.class);
    }

    public static DefaultProvider ofAnnotation(JsonDefaultProvider annotation) {
        return new DefaultProvider(annotation.priority(), annotation.value(), annotation.clazz());
    }
}
