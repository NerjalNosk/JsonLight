package io.github.nerjalnosk.jsonlight.elements;

import io.github.nerjalnosk.jsonlight.parser.options.NumberParseOptions;
import io.github.nerjalnosk.jsonlight.parser.options.ParseSet;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;

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
    private BigDecimal value;
    private transient NumberParseOptions parseOptions;

    /**
     * A new JsonNumber with a null value (0)
     */
    public JsonNumber() {
        this(BigDecimal.ZERO, new NumberParseOptions());
    }

    /**
     * A new JsonNumber with the specified value.
     * @param i the value of the new JsonNumber
     */
    public JsonNumber(int i) {
        this(BigDecimal.valueOf(i), new NumberParseOptions());
    }

    /**
     * A new JsonNumber with the specified value.
     * @param f the value of the new JsonNumber
     */
    public JsonNumber(float f) {
        this(BigDecimal.valueOf(f), new NumberParseOptions());
    }

    /**
     * A new JsonNumber with the specified value.
     * @param l the value of the new JsonNumber
     */
    public JsonNumber(long l) {
        this(BigDecimal.valueOf(l), new NumberParseOptions());
    }

    /**
     * A new JsonNumber with the specified value.
     * @param d the value of the new JsonNumber
     */
    public JsonNumber(double d) {
        this(BigDecimal.valueOf(d), new NumberParseOptions());
    }

    /**
     * A new JsonNumber with the specified value
     * and stringification options.
     * @param n the new instance's value
     * @param options the new instance's
     *                stringification option
     */
    public JsonNumber (Number n, NumberParseOptions options) {
        if (n instanceof BigInteger) this.value = new BigDecimal((BigInteger) n);
        else if (n instanceof BigDecimal) this.value = (BigDecimal) n;
        else this.value = BigDecimal.valueOf(n.doubleValue());
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
        return new JsonNumber(new BigInteger(s), options);
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
        return new JsonNumber(new BigDecimal(s), options);
    }

    /**
     * Sets the number's value
     * @param n the number's new value
     */
    public void setValue(Number n) {
        if (n instanceof BigInteger) this.value = new BigDecimal((BigInteger) n);
        else if (n instanceof BigDecimal) this.value = (BigDecimal) n;
        else this.value = BigDecimal.valueOf(n.doubleValue());
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
    public BigInteger getAsBigInt() {
        return this.value.toBigInteger();
    }
    @Override
    public BigDecimal getAsBigDecimal() {
        return this.value;
    }

    @Override
    protected String stringify(ParseSet parseSet, String indentation, String indentIncrement, ExplorationStack stack) {
        NumberParseOptions setOptions = (NumberParseOptions) parseSet.getOptions(this.getClass());
        NumberParseOptions options = parseOptions.isChanged() ? parseOptions :
                setOptions == null ? parseOptions : setOptions;
        int i = (int) Math.pow(10,options.getDecimals());
        String s;
        if (options.usesHexadecimal()) {
            StringBuilder floating = new StringBuilder();
            BigInteger intCopy = this.value.toBigInteger();
            if (options.isFloating()) {
                floating.append(".");
                BigDecimal frac = this.value.subtract(new BigDecimal(intCopy));
                int l = Math.max(frac.scale(), options.getDecimals());
                while (frac.intValue() == 0 && frac.scale() > 0 && l > 0) {
                    int t = frac.multiply(new BigDecimal(16)).intValue();
                    floating.append(Integer.toHexString(t));
                    frac = frac.multiply(new BigDecimal(16)).subtract(new BigDecimal(t));
                    l--;
                }
            }
            String intPart = intCopy.toString(16);
            s = intPart + floating;
        }
        else if (options.usesScientific()) {
            DecimalFormat f = options.getFormat();
            f.setMaximumFractionDigits(Math.max(this.value.scale(), options.getDecimals()));
            s = f.format(this.value.doubleValue());
        }
        else if (this.getAsDouble() == this.getAsLong()) {
            this.value.setScale(1);
            s = options.isInteger() ?
                    this.value.toPlainString() :
                    new BigDecimal(this.value.multiply(BigDecimal.valueOf(i)).toBigInteger()).toString();
        } else s = new BigDecimal(this.value.multiply(BigDecimal.valueOf(i)).toBigInteger()).toString();
        return s;
    }

    @Override
    public JsonNumber clone() {
        NumberParseOptions options = this.parseOptions.isChanged() ? this.parseOptions.clone() : new NumberParseOptions();
        return new JsonNumber(this.value, options);
    }
}
