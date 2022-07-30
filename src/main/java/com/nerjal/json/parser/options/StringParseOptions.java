package com.nerjal.json.parser.options;

import com.nerjal.json.elements.JsonString;

/**
 * <p>Stringification options for
 * {@link JsonString} elements.
 * </p>
 * <p>Allows to set whether should
 * the string be stringified using
 * simple quoted or double quoted
 * keys.
 * </p>
 * <p>Hence only two formats
 * for strings' stringification
 * are available, each only
 * affecting the string's
 * quoting.
 * </p>
 * Default is double-quoted,
 * in order to better fit to
 * JSON4 standards, for a better
 * compatibility.
 * @author Nerjal Nosk
 */
public class StringParseOptions extends AbstractParseOptions<JsonString> {
    private QuoteFormat format;

    /**
     * Instantiates new string
     * stringification options
     * with the specified format.
     * @param format the string
     *               quoting
     *               format
     *               for the new
     *               options
     */
    public StringParseOptions(QuoteFormat format) {
        this.format = format;
        ping();
    }

    /**
     * Instantiates new
     * double-quoted string
     * stringification options.
     */
    public StringParseOptions() {
        this.format = QuoteFormat.DOUBLE_QUOTES;
    }

    /**
     * Returns whether the options
     * are set to stringify string
     * elements with single
     * quoting.
     * @return whether the options
     *         are set to stringify
     *         string elements with
     *         single quoting
     */
    public boolean usesSingleQuotes() {
        return this.format == QuoteFormat.SINGLE_QUOTES;
    }

    /**
     * Returns whether the options
     * are set to stringify string
     * elements with double
     * quoting.
     * @return whether the options
     *         are set to stringify
     *         string elements with
     *         double quoting
     */
    public boolean usesDoubleQuotes() {
        return this.format == QuoteFormat.DOUBLE_QUOTES;
    }

    /**
     * Sets the options to use
     * single quoting.
     */
    public void setUseSingleQuotes() {
        this.format = QuoteFormat.SINGLE_QUOTES;
        ping();
    }

    /**
     * Sets the options to use
     * double quoting.
     */
    public void setUseDoubleQuotes() {
        this.format = QuoteFormat.DOUBLE_QUOTES;
        ping();
    }

    /**
     * Different available
     * stringification formats
     * for {@link JsonString}
     */
    public enum QuoteFormat {
        SINGLE_QUOTES,
        DOUBLE_QUOTES
    }
}
