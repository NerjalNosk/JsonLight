package io.github.nerjalnosk.jsonlight.parser.options;

import io.github.nerjalnosk.jsonlight.JsonParser;
import io.github.nerjalnosk.jsonlight.elements.JsonElement;

import java.util.HashMap;

/**
 * Generic class to manage the
 * {@link JsonParser}'s
 * default stringification options when
 * parsing an element to string
 */
public final class ParseSet {
    private final HashMap<Class<? extends JsonElement>, AbstractParseOptions<?>> map = new HashMap<>();

    /**
     * Adds the default parseOptions for the specified
     * JsonElement class for this parsing set
     * @param elementClass the class to set the options
     *                     for
     * @param options the options to set as default for
     *                this set for the specified class
     * @return whether the options could be added for
     *         the specified class
     * @param <T> the {@link JsonElement} class to link
     *            to the specified options
     */
    public <T extends JsonElement> boolean addOptions(Class<T> elementClass, AbstractParseOptions<T> options) {
        if (map.containsKey(elementClass)) return false;
        map.put(elementClass, options);
        return true;
    }

    @SuppressWarnings("unchecked")
    public <T extends JsonElement> AbstractParseOptions<T> getOptions(Class<T> elementClass) {
        if (!map.containsKey(elementClass)) return null;
        return (AbstractParseOptions<T>) map.get(elementClass);
    }
}
