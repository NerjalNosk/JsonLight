package io.github.nerjalnosk.jsonlight.elements;

import io.github.nerjalnosk.jsonlight.parser.options.BooleanParseOptions;
import io.github.nerjalnosk.jsonlight.parser.options.ParseSet;

/**
 * <p>An object that allows instantiating a
 * JSON boolean element in a JSON
 * structure.
 * </p>
 * <p>Stringification options rely on a
 * {@link BooleanParseOptions} object,
 * which allows to set whether the boolean
 * should be stringified fully in lowercase,
 * fully in uppercase, or in a title style
 * (fully lowercase but upper first char).
 * </p>
 * <p>Default stringification options
 * imply full lowercase.
 * </p>
 * @author nerjal
 */
public class JsonBoolean extends JsonElement {
    private boolean value;
    private BooleanParseOptions parseOptions;

    /**
     * Instantiates a new {@code false}
     * boolean with default stringification
     * options.
     */
    public JsonBoolean() {
        this(false);
    }

    /**
     * Instantiates a new boolean with the
     * specified value and default
     * stringification options.
     * @param b the value of the new boolean
     */
    public JsonBoolean(boolean b) {
        this(b, new BooleanParseOptions());
    }

    /**
     * Instantiates a new boolean with the
     * specified value and stringification
     * options.
     * @param b the value of the new boolean
     * @param options the stringification
     *                options of the new
     *                boolean
     */
    public JsonBoolean(boolean b, BooleanParseOptions options) {
        this.value = b;
        this.parseOptions = options;
    }

    /**
     * Sets the boolean's value to the
     * specified one.
     * @param b the object's new value
     */
    public void setValue(boolean b) {
        this.value = b;
    }

    /**
     * Changes the object's stringification
     * options for the specified ones.
     * @param parseOptions the new
     *                     stringification
     *                     options
     */
    public void setParseOptions(BooleanParseOptions parseOptions) {
        this.parseOptions = parseOptions;
    }

    @Override
    public boolean isBoolean() {
        return true;
    }
    @Override
    public boolean isPrimitive() {
        return true;
    }
    @Override
    public String typeToString() {
        return "Boolean";
    }
    @Override
    public boolean getAsBoolean() {
        return this.value;
    }

    @Override
    public String stringify(ParseSet parseSet, String indentation, String indentIncrement, JsonStringifyStack stack) {
        if (parseSet == null) parseSet = new ParseSet();
        BooleanParseOptions setOptions = (BooleanParseOptions) parseSet.getOptions(this.getClass());
        BooleanParseOptions options = parseOptions.isChanged() ? parseOptions :
                setOptions == null ? parseOptions : setOptions;
        if (options.usesAllLowercase()) return String.valueOf(this.value);
        else if (options.usesAllUppercase()) return this.value ? "TRUE" : "FALSE";
        else return this.value ? "True" : "False";
    }
}
