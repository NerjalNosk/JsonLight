package com.github.nerjalnosk.jsonlight.parser.options;

import com.github.nerjalnosk.jsonlight.elements.JsonArray;

import static com.github.nerjalnosk.jsonlight.JsonError.*;

/**
 * <p>Stringification options for
 * {@link JsonArray} elements.
 * </p>
 * <p>Allows to set how many elements
 * should be stringified per line.
 * </p>
 * Therefore, it supports multiple
 * values, which are:
 * <ul>
 * <li>one element per line</li>
 * <li>a specified number of elements
 * per line</li>
 * <li>all in one line</li>
 * </ul>
 * Warning! comments bypass the
 * multiple-per-line rule, always
 * being on a new line, and
 * followed by one.
 * @author Nerjal Nosk
 */
public class ArrayParseOptions extends AbstractParseOptions<JsonArray> {
    private long numPerLine;
    private ArrayFormat format;

    /**
     * Instantiates new array stringification
     * options with the specified format, and
     * number of elements per line (only used
     * with
     * {@link ArrayFormat#MULTIPLE_PER_LINE})
     * @param format the stringification
     *               format to use
     * @param numPerLine the number of
     *                   element per line if
     *                   multiple
     * @throws IllegalArgumentException if
     *         numPerLine is negative
     */
    public ArrayParseOptions(ArrayFormat format, int numPerLine) {
        this.format = format;
        if (numPerLine < 0)
            throw new IllegalArgumentException(
                    "Unhandled negative number for array stringification options");
        this.numPerLine = numPerLine;
        ping();
    }

    /**
     * Instantiates new array stringification
     * options with the specified format, and
     * a default number of elements per line
     * of 2 if multiple format, 0 otherwise.
     * @param format the stringification
     *               format to use
     */
    public ArrayParseOptions(ArrayFormat format) {
        this(format, format == ArrayFormat.MULTIPLE_PER_LINE ? 2 : 0);
        ping();
    }

    /**
     * Instantiates new array stringification
     * options with a default one-per-line
     * format.
     */
    public ArrayParseOptions() {
        this.format = ArrayFormat.ONE_PER_LINE;
        this.numPerLine = 0;
    }

    /**
     * Sets a new format for the
     * stringification options
     * @param format the stringification
     *              options' new format
     */
    public void setFormat(ArrayFormat format) {
        ping();
        this.format = format;
    }

    /**
     * Sets a new number of
     * elements per line for the
     * stringification options.<br>
     * Format is automatically
     * adapted to the specified
     * number:<br>
     * {@code 1} -> one per line<br>
     * {@code 0} or
     * {@code Long.MAX_VALUE} ->
     * all inline<br>
     * other -> multiple per line
     * @param i the new number of
     *          elements per line
     * @throws IllegalLineElementsNumberException
     *         if the given number
     *         is negative
     */
    public void  setNumPerLine(long i) throws IllegalLineElementsNumberException {
        ping();
        if (i < 0) throw new IllegalLineElementsNumberException(
                "Cannot parse a JsonArray with less than one element per line");
        if (i == 1) this.format = ArrayFormat.ONE_PER_LINE;
        else if (i == Long.MAX_VALUE || i == 0) this.format = ArrayFormat.INLINE;
        else {
            this.numPerLine = i;
            this.format = ArrayFormat.MULTIPLE_PER_LINE;
        }
    }

    /**
     * Returns the number of
     * elements per line as
     * executed during
     * stringification.
     * @return the number of
     *         elements stringified
     *         per line
     */
    public long getNumPerLine() {
        if (this.format == ArrayFormat.INLINE) return Integer.MAX_VALUE;
        if (this.format == ArrayFormat.ONE_PER_LINE) return 1;
        return this.numPerLine;
    }

    /**
     * Returns whether the options
     * shall make any array
     * stringified with all
     * children elements in one line
     * @return whether all array
     *         children elements
     *         shall be in one line
     */
    public boolean isAllInOneLine() {
        return this.format == ArrayFormat.INLINE;
    }

    /**
     * Different available formats
     * for {@link JsonArray}
     * stringification options
     */
    public enum ArrayFormat {
        INLINE,
        ONE_PER_LINE,
        MULTIPLE_PER_LINE
    }
}
