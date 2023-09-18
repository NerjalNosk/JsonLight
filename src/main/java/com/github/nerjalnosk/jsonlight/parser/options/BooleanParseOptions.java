package com.github.nerjalnosk.jsonlight.parser.options;

import com.github.nerjalnosk.jsonlight.elements.JsonBoolean;

/**
 * <p>Stringification options for
 * {@link JsonBoolean} elements.
 * </p>
 * <p>Allows to set how should
 * be stringified a boolean.
 * </p>
 * <p>Available options are:
 * </p>
 * <ul>
 *     <li>All Uppercase</li>
 *     <li>All Lowercase</li>
 *     <li>Titled</li>
 * </ul>
 * <p>Only capitalization is
 * affected. Booleans will
 * always be stringified in
 * full letters, by their
 * English name.</p>
 * @author Nerjal Nosk
 */
public class BooleanParseOptions extends AbstractParseOptions<JsonBoolean> {
    private BooleanFormat format;

    /**
     * Instantiates new boolean
     * stringification options with
     * the specified format.
     * @param format the new options'
     *               capitalization
     *               format
     */
    public BooleanParseOptions(BooleanFormat format) {
        this.format = format;
        ping();
    }

    /**
     * Instantiates new boolean
     * stringification options with
     * default all lowercase format.
     */
    public BooleanParseOptions() {
        this.format = BooleanFormat.ALL_LOWERCASE;
    }

    /**
     * Sets a new format for the
     * stringification options
     * @param format the stringification
     *               options' new format
     */
    public void setFormat(BooleanFormat format) {
        this.format = format;
        ping();
    }

    /**
     * Returns whether the options
     * are set to use all upper case.
     * @return whether the options
     *         are set to use all
     *         upper case
     */
    public boolean usesAllUppercase() {
        return this.format == BooleanFormat.ALL_UPPERCASE;
    }

    /**
     * Returns whether the options
     * are set to use all lower case.
     * @return whether the options
     *         are set to use all
     *         lower case
     */
    public boolean usesAllLowercase() {
        return this.format == BooleanFormat.ALL_LOWERCASE;
    }

    /**
     * Returns whether the options
     * are set to use title format.
     * @return whether the options
     *         are set to use
     *         title format
     */
    public boolean usesTitleFormat() {
        return this.format == BooleanFormat.TITLE;
    }

    /**
     * Different available formats
     * for {@link JsonBoolean}
     * stringification options.
     */
    public enum BooleanFormat {
        ALL_UPPERCASE,
        ALL_LOWERCASE,
        TITLE
    }
}
