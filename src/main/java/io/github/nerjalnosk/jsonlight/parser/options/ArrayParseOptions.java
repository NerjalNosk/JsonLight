package io.github.nerjalnosk.jsonlight.parser.options;

import io.github.nerjalnosk.jsonlight.elements.JsonArray;

import static io.github.nerjalnosk.jsonlight.JsonError.*;

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
    private boolean circular;
    private boolean lineBreak;

    public static ArrayParseOptions extended() {
        return new ArrayParseOptions(ArrayFormat.ONE_PER_LINE, 1, true, true);
    }

    /**
     * Instantiates new array stringification
     * options with the specified format,
     * number of elements per line (only used
     * with
     * {@link ArrayFormat#MULTIPLE_PER_LINE})
     * and circularisation.
     * @param format the stringification
     *               format to use
     * @param numPerLine the number of
     *                   element per line if
     *                   multiple
     * @param doCircular whether arrays using
     *                   these options should
     *                   resolve circularity
     * @param lineBreak whether arrays using
     *                  these options should
     *                  use line breaks as
     *                  iteration separator
     *                  when possible.
     */
    public ArrayParseOptions(ArrayFormat format, long numPerLine, boolean doCircular, boolean lineBreak) {
        this.format = format;
        if (numPerLine < 0)
            throw new IllegalArgumentException(
                    "Unsupported negative number for array stringification options");
        this.numPerLine = numPerLine;
        this.circular = doCircular;
        this.lineBreak = lineBreak;
        ping();
    }

    /**
     * Instantiates new array stringification
     * options with the specified circularisation
     * and line break iteration.
     * @param doCircular whether arrays using
     *                   these options should
     *                   resolve circularity
     * @param lineBreak whether arrays using
     *                  these options should
     *                  use line breaks as
     *                  iteration separator
     *                  when possible.
     */
    public ArrayParseOptions(boolean doCircular, boolean lineBreak) {
        this(ArrayFormat.ONE_PER_LINE, 0, doCircular, lineBreak);
    }

    /**
     * Instantiates new array stringification
     * options with the specified format,
     * number of elements per line (only used
     * with
     * {@link ArrayFormat#MULTIPLE_PER_LINE})
     * and circularisation.
     * @param format the stringification
     *               format to use
     * @param numPerLine the number of
     *                   element per line if
     *                   multiple
     * @param doCircular whether arrays using
     *                   these options should
     *                   resolve circularity
     */
    public ArrayParseOptions(ArrayFormat format, long numPerLine, boolean doCircular) {
        this(format, numPerLine, doCircular, false);
    }

    /**
     * Instantiates new array stringification
     * options with the specified format, and
     * number of elements per line (only used
     * with
     * {@link ArrayFormat#MULTIPLE_PER_LINE})
     * <p>
     * Circularisation is {@code false} by
     * default
     * @param format the stringification
     *               format to use
     * @param numPerLine the number of
     *                   element per line if
     *                   multiple
     * @throws IllegalArgumentException if
     *         numPerLine is negative
     */
    public ArrayParseOptions(ArrayFormat format, long numPerLine) {
        this(format, numPerLine, false);
    }

    /**
     * Instantiates new array stringification
     * options with the specified format, and
     * a default number of elements per line
     * of 2 if multiple format, 0 otherwise.
     * <p>
     * Circularisation is {@code false} by
     * default
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
     * <p>
     * Circularisation is {@code false} by
     * default
     */
    public ArrayParseOptions() {
        this.format = ArrayFormat.ONE_PER_LINE;
        this.numPerLine = 0;
    }

    @Override
    public ArrayParseOptions clone() {
        return new ArrayParseOptions(this.format, this.numPerLine, this.circular, this.lineBreak);
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
        if (this.format == ArrayFormat.INLINE) return Long.MAX_VALUE;
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
     * Sets whether arrays using
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
     * Returns whether arrays should resolve
     * circularity.
     * @return Whether arrays should resolve
     *         circularity.
     */
    public boolean resolveCircular() {
        return this.circular;
    }

    /**
     * Sets whether arrays using
     * these options should use line
     * breaks as iteration separator
     * when possible according
     * to their format option.
     * @param b the new line break
     *          setting value.
     */
    public void useLineBreak(boolean b) {
        this.lineBreak = b;
        ping();
    }

    /**
     * Returns whether arrays should
     * use line breaks as iteration
     * separator when possible.
     * @return Whether arrays should
     *         use line breaks as
     *         iteration separator
     *         when possible.
     */
    public boolean useLineBreakAsIterator() {
        return this.lineBreak;
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
