package io.github.nerjalnosk.jsonlight.parser.options;

import io.github.nerjalnosk.jsonlight.elements.JsonString;

import java.util.HashMap;
import java.util.Map;

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
    private final Map<String, Integer> unicodedCodes = new HashMap<>();

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

    @Override
    public StringParseOptions clone() {
        return new StringParseOptions(this.format).withUnicoded(this.unicodedCodes);
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
     * Sets the stringification options to also
     * included the provided map of unicode
     * codepoints per char combination.
     * @param unicodedCodes the map of char
     *                      combination - codepoint
     *                      to add to the options
     * @return this
     */
    public StringParseOptions withUnicoded(Map<String, Integer> unicodedCodes) {
        unicodedCodes.forEach(this.unicodedCodes::putIfAbsent);
        if (!unicodedCodes.isEmpty()) ping();
        return this;
    }

    public boolean isCharUnicoded(String s) {
        return this.unicodedCodes.containsKey(s);
    }

    public int unicodedCode(String s) {
        return this.unicodedCodes.getOrDefault(s, -1);
    }

    public void addUnicodedCode(String s, int i) {
        if (i < 0) return;
        if (this.unicodedCodes.putIfAbsent(s, i) != null) ping();
    }

    public boolean hasUnicodedEncoded() {
        return !this.unicodedCodes.isEmpty();
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
