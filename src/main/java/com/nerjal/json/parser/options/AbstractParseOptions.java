package com.nerjal.json.parser.options;

import com.nerjal.json.elements.JsonElement;

/**
 * Abstract class used to set a {@link JsonElement}'s
 * default stringification options.
 * The {@link AbstractParseOptions#ping()} method
 * shall be used to notify the parseOptions instance
 * as non-default, in both set methods as non-default
 * constructors.
 * @param <T> The {@link JsonElement} the option
 *            class is for. Shall be set at
 *            superclass definition.
 */
public abstract class AbstractParseOptions <T extends JsonElement> {
    private boolean ping = false;

    /**
     * Notify the instance as non-default
     */
    public final void ping() {
        ping = true;
    }

    /**
     * Returns whether the instance is a default format
     * @return whether the instance is a default format
     */
    public final boolean isChanged() {
        return ping;
    }
}
