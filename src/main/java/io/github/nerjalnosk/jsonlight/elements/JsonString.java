package io.github.nerjalnosk.jsonlight.elements;

import io.github.nerjalnosk.jsonlight.parser.options.ParseSet;
import io.github.nerjalnosk.jsonlight.parser.options.StringParseOptions;

/**
 * <p>An object made to symbolise a String
 * in a JSON Structure.
 * </p>
 * <p>Stringification options rely on a
 * {@link StringParseOptions} object,
 * which allows to set whether the object
 * should be stringified with simple or
 * double quotes.
 * </p>
 * <p>Default Stringification options means
 * double quiting.
 * </p>
 * Stringifies as {@code null} if the
 * value is null, just as {@code null}
 * is parsed as a null JsonString with
 * the parser.
 * @author nerjal
 */
public class JsonString extends JsonElement {
    public static final JsonString NULL = new JsonString((String) null) {
        @Override
        public String getAsString() {
            return "null";
        }

        @Override
        protected String stringify(ParseSet parseSet, String indentation, String indentIncrement, ExplorationStack stack) {
            return "null";
        }

        @Override
        public void setValue(String s) {}

        @Override
        public void setParseOptions(StringParseOptions options) {}

        @Override
        public JsonString clone() {
            return this;
        }
    };

    private String value;
    private transient StringParseOptions parseOptions;

    /**
     * Instantiates a new JsonString with the
     * specified value and stringification
     * options.
     * @param s the value of the string
     * @param options the stringification
     *                options for the new
     *                instance
     */
    public JsonString(String s, StringParseOptions options) {
        this.value = s;
        this.parseOptions = options;
    }

    /**
     * Instantiates a new JsonString with the
     * specified value and default
     * stringification options.
     * @param value the value of the string
     */
    public JsonString(String value) {
        this(value, new StringParseOptions());
    }

    /**
     * Instantiates a new JsonString with a
     * {@code null} value and the specified
     * stringification options.
     * @param options the stringification
     *                options for the new
     *                instance
     */
    public JsonString(StringParseOptions options) {
        this(null, options);
    }

    /**
     * Instantiates a new JsonString with a
     * {@code null} value and default
     * stringification options.
     */
    public JsonString() {
        this((String) null);
    }

    /**
     * Changes the object's stringification
     * options to the specified ones
     * @param options the new stringification
     *                options
     */
    public void setParseOptions(StringParseOptions options) {
        this.parseOptions = options;
    }

    /**
     * Changes the string's value to the
     * specified one.
     * @param s the new value for the object
     */
    public void setValue(String s) {
        this.value = s;
    }
    
    @Override
    public boolean isString() {
        return true;
    }
    @Override
    public boolean isPrimitive() {
        return true;
    }
    @Override
    public boolean isNull() {
        return this.value == null;
    }
    @Override
    public String typeToString() {
        return value == null ? "null" : "String";
    }
    @Override
    public JsonString getAsJsonString() {
        return this;
    }
    @Override
    public String getAsString() {
        return this.value;
    }
    public String getValue() {
        return this.value;
    }

    @Override
    protected String stringify(ParseSet parseSet, String indentation, String indentIncrement, ExplorationStack stack) {
        if (this.value == null) return "null";
        StringParseOptions setOptions = (StringParseOptions) parseSet.getOptions(this.getClass());
        StringParseOptions options = parseOptions.isChanged() ? parseOptions :
                setOptions == null ? parseOptions : setOptions;
        char c = options.usesDoubleQuotes() ? '"' : '\'';
        if (this.parseOptions.hasUnicodedEncoded()) {
            StringBuilder builder = new StringBuilder();
            builder.append(c);
            int i = 0;
            while (i < this.value.length()) {
                String sub = this.value.substring(i, i+2);
                if (this.parseOptions.isCharUnicoded(sub)) {
                    builder.append("\\u").append(this.parseOptions.unicodedCode(sub));
                    i++;
                } else {
                    builder.append(sub.charAt(0));
                }
                i++;
            }
            builder.append(c);
            return builder.toString();
        }
        return  String.format("%c%s%c",c,this.value,c);
    }

    @Override
    public JsonString clone() {
        JsonString copy = new JsonString(this.value);
        if (this.parseOptions.isChanged()) {
            copy.setParseOptions(this.parseOptions.clone());
        }
        return copy;
    }
}
