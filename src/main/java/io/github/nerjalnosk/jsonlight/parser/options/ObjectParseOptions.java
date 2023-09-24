package io.github.nerjalnosk.jsonlight.parser.options;

import io.github.nerjalnosk.jsonlight.elements.JsonObject;

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
public class ObjectParseOptions extends AbstractParseOptions<JsonObject> {
    private ObjectFormat format;
    private boolean ordered;
    private boolean circular;

    /**
     * Instantiates new
     * double-quoted keys and
     * unordered object
     * stringification options.
     * <p>
     * Circularisation is
     * {@code false} by default
     */
    public ObjectParseOptions() {
        this.format = ObjectFormat.DOUBLE_QUOTED_KEYS;
        this.ordered = false;
        this.circular = false;
    }

    /**
     * Instantiates new object
     * stringification options
     * with the specified keys
     * stringification and
     * unordered format
     * <p>
     * Circularisation is
     * {@code false} by default
     * @param format the keys
     *               stringification
     *               format for
     *               the new
     *               options
     */
    public ObjectParseOptions(ObjectFormat format) {
        this(format, false, false);
    }

    /**
     * Instantiates new
     * double-quoted keys
     * and with the specified
     * ordering object
     * stringification format
     * <p>
     * Circularisation is
     * {@code false} by default
     * @param ordered whether
     *                should the
     *                object be
     *                stringified
     *                with its
     *                elements
     *                ordered as
     *                added to it
     */
    public ObjectParseOptions(boolean ordered) {
        this(ObjectFormat.DOUBLE_QUOTED_KEYS, ordered, false);
    }

    /**
     * Instantiates new object
     * stringification options
     * with the specified keys
     * format and element
     * ordering.
     * <p>
     * Circularisation is
     * {@code false} by default
     * @param format the keys
     *               stringification
     *               format for
     *               the new
     *               options
     * @param ordered whether
     *                should the
     *                object be
     *                stringified
     *                with its
     *                elements
     *                ordered as
     *                added to it
     */
    public ObjectParseOptions(ObjectFormat format, boolean ordered) {
        this(format, ordered, false);
    }

    /**
     * Instantiates new object
     * stringification options
     * with the specified keys
     * format, element
     * ordering and
     * circularisation.
     * @param format the keys
     *               stringification
     *               format for
     *               the new
     *               options
     * @param ordered whether
     *                should the
     *                object be
     *                stringified
     *                with its
     *                elements
     *                ordered as
     *                added to it
     * @param doCircular whether
     *                   the object
     *                   should resolve
     *                   circularity
     *                   upon
     *                   stringification
     */
    public ObjectParseOptions(ObjectFormat format, boolean ordered, boolean doCircular) {
        this.format = format;
        this.ordered = ordered;
        this.circular = doCircular;
        ping();
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
        ping();
    }

    /**
     * Sets the ordering
     * stringification format
     * for the options
     * @param ordered the new
     *                ordering
     *                stringification
     *                format of the
     *                options
     */
    public void setOrdering(boolean ordered) {
        this.ordered = ordered;
        ping();
    }

    /**
     * Returns whether the
     * object's elements
     * should be ordered
     * as added upon
     * stringification
     * @return whether the
     *         object's elements
     *         should be ordered
     *         as added upon
     *         stringification
     */
    public boolean isOrdered() {
        return ordered;
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
     * Sets whether objects using
     * these options should resolve
     * circularity on themselves
     * upon stringification.
     * @param b the new circularity
     *          setting value.
     */
    public void doCircular(boolean b) {
        this.circular = b;
        ping();
    }

    /**
     * Returns whether objects should resolve
     * circularity.
     * @return Whether objects should resolve
     *         circularity.
     */
    public boolean resolveCircular() {
        return this.circular;
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
