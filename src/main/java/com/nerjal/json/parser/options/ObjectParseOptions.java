package com.nerjal.json.parser.options;

import com.nerjal.json.elements.JsonObject;

/**
 * <p>Stringification options for
 * {@link JsonObject} elements.
 * </p>
 * <p>Allows to set whether should
 * the object be stringified using
 * unquoted, simple quoted or
 * double quoted keys.
 * </p>
 * <p>Hence only three formats
 * for objects' stringification
 * are available, each only
 * affecting the stringification
 * of the object's keys.</p>
 * Default is double-quoted keys,
 * in order to better fit to
 * JSON4 standards, for a better
 * compatibility.
 * @author Nerjal Nosk
 */
public class ObjectParseOptions {
    private ObjectFormat format;

    /**
     * Instantiates new
     * double-quoted keys object
     * stringification options.
     */
    public ObjectParseOptions() {
        this.format = ObjectFormat.DOUBLE_QUOTED_KEYS;
    }

    /**
     * Instantiates new object
     * stringification options
     * with the specified keys
     * stringification format
     * @param format the keys
     *               stringification
     *               format for
     *               the new
     *               options
     */
    public ObjectParseOptions(ObjectFormat format) {
        this.format = format;
    }

    /**
     * Sets the keys
     * stringification format
     * for the options
     * @param format the new keys
     *               stringification
     *               format of
     *               the options
     */
    public void setFormat(ObjectFormat format) {
        this.format = format;
    }

    /**
     * Returns the quoting char
     * corresponding to the
     * options' keys quoting
     * format.
     * @return the quoting char
     *         corresponding to
     *         the options' keys
     *         quoting format.
     */
    public char keyQuoteChar() {
        char c;
        switch (format) {
            case UNQUOTED_KEYS: {
                c = 0;
                break;
            }
            case SINGLE_QUOTED_KEYS: {
                c = '\'';
                break;
            }
            default: c = '"';
        }
        return c;
    }

    /**
     * Different available keys
     * stringification formats
     * for {@link JsonObject}
     */
    public enum ObjectFormat {
        UNQUOTED_KEYS,
        SINGLE_QUOTED_KEYS,
        DOUBLE_QUOTED_KEYS
    }
}
