package com.github.nerjalnosk.jsonlight.elements;

import com.github.nerjalnosk.jsonlight.parser.options.NumberParseOptions;
import com.github.nerjalnosk.jsonlight.parser.options.ParseSet;

/**
 * <p>A JsonElement allowing instantiation of
 * numbers inside a JSON structure.<br>
 * </p>
 * <p>Does not make distinction between any
 * number types, as it allows to obtain
 * any type from its sole value.<br>
 * </p>
 * <p>Stringification options allow setting
 * whether it should be parsed as an
 * integer or floating number, and using
 * decimal, hexadecimal or scientific
 * notation.
 * </p>
 * By default, stringification is set to
 * return an integer decimal string of the
 * number.
 * @author nerjal
 */
public class JsonNumber extends JsonElement {
    private Number value;
    private NumberParseOptions parseOptions;

    /**
     * A new JsonNumber with a null value (0)
     */
    public JsonNumber() {
        this(0);
    }

    /**
     * A new JsonNumber with the specified value.
     * @param n the value of the new JsonNumber
     */
    public JsonNumber(Number n) {
        this(n, new NumberParseOptions());
    }

    /**
     * A new JsonNumber with the specified value
     * and stringification options.
     * @param n the new instance's value
     * @param options the new instance's
     *                stringification option
     */
    public JsonNumber (Number n, NumberParseOptions options) {
        this.value = n;
        this.parseOptions = options;
    }

    /**
     * Returns a new JsonNumber with the specified
     * string parsed to an integer as value, and
     * default stringification options.
     * @param s the string to be parsed as the
     *          number's value
     * @return the number with the value parsed
     *         from the specified string
     * @throws NumberFormatException if the
     *         specified string cannot be
     *         parsed to an integer
     */
    public static JsonNumber fromIntegerString(String s) {
        return fromIntegerString(s, new NumberParseOptions());
    }

    /**
     * Returns a new JsonNumber with the specified
     * string parsed to an integer as value, and
     * the specified stringification options.
     * @param s the string to be parsed as the
     *          number's value
     * @param options the stringification options
     *                of the new number
     * @return the number with the value parsed
     *         from the specified string, and
     *         stringification options
     * @throws NumberFormatException if the
     *         specified string cannot be
     *         parsed to an integer
     */
    public static JsonNumber fromIntegerString(String s, NumberParseOptions options) {
        return new JsonNumber(Integer.parseInt(s), options);
    }

    /**
     * Returns a new JsonNumber with the specified
     * string parsed to a float as value, and
     * default stringification options.
     * @param s the string to be parsed as the
     *          number's value
     * @return the number with the value parsed
     *         from the specified string
     * @throws NumberFormatException if the
     *         specified string cannot be
     *         parsed to a float
     * @throws NullPointerException if the
     *         specified string is
     *         {@code null}
     */
    public static JsonNumber fromFloatString(String s) {
        return fromFloatString(s, new NumberParseOptions(true));
    }

    /**
     * Returns a new JsonNumber with the specified
     * string parsed to a float as value, and
     * the specified stringification options.
     * @param s the string to be parsed as the
     *          number's value
     * @param options the stringification options
     *                of the new number
     * @return the number with the value parsed
     *         from the specified string
     * @throws NumberFormatException if the
     *         specified string cannot be
     *         parsed to a float
     * @throws NullPointerException if the
     *         specified string is
     *         {@code null}
     */
    public static JsonNumber fromFloatString(String s, NumberParseOptions options) {
        return new JsonNumber(Float.parseFloat(s), options);
    }

    /**
     * Sets the number's value
     * @param n the number's new value
     */
    public void setValue(Number n) {
        this.value = n;
    }

    /**
     * Changes the number's stringification
     * options
     * @param parseOptions the number's new
     *                     stringification
     *                     options
     */
    public void setParseOptions(NumberParseOptions parseOptions) {
        this.parseOptions = parseOptions;
    }

    @Override
    public boolean isNumber() {
        return true;
    }
    @Override
    public boolean isPrimitive() {
        return true;
    }
    @Override
    public String typeToString() {
        return "Number";
    }
    @Override
    public JsonNumber getAsJsonNumber() {
        return this;
    }
    @Override
    public Number getAsNumber() {
        return this.value;
    }
    @Override
    public int getAsInt() {
        return this.value.intValue();
    }
    @Override
    public long getAsLong() {
        return this.value.longValue();
    }
    @Override
    public float getAsFloat() {
        return this.value.floatValue();
    }
    @Override
    public double getAsDouble() {
        return this.value.doubleValue();
    }

    @Override
    public String stringify(ParseSet parseSet, String indentation, String indentIncrement, JsonStringifyStack stack) {
        NumberParseOptions setOptions = (NumberParseOptions) parseSet.getOptions(this.getClass());
        NumberParseOptions options = parseOptions.isChanged() ? parseOptions :
                setOptions == null ? parseOptions : setOptions;
        int i = (int) Math.pow(10,options.getDecimals());
        String s;
        if (options.usesHexadecimal()) {
            s = options.isFloating() ?
                    Double.toHexString(((double)((int)(this.value.doubleValue()*i)))/i) :
                    Integer.toHexString(this.value.intValue());
        }
        else if (options.usesScientific())
            s = NumberParseOptions.sciFormat.format(this.value.doubleValue());
        else if (this.getAsDouble() == this.getAsInt()) {
            s = options.isInteger() ?
                    Integer.toString(this.value.intValue()) :
                    Double.toString(((double)((int)(this.value.doubleValue()*i))/i));
        } else s = Double.toString(((double)((int)(this.value.doubleValue()*i))/i));
        return s;
    }
}
