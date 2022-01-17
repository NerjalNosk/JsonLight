package com.nerjal.json.elements;

import com.nerjal.json.parser.options.NumberParseOptions;

public class JsonNumber extends JsonElement {
    private Number value;
    private NumberParseOptions parseOptions;

    public JsonNumber() {
        this(0);
    }
    public JsonNumber(Number n) {
        this(n, new NumberParseOptions());
    }
    public JsonNumber (Number n, NumberParseOptions options) {
        this.value = n;
        this.parseOptions = options;
    }
    public static JsonNumber fromIntegerString(String s) {
        return fromIntegerString(s, new NumberParseOptions());
    }
    public static JsonNumber fromIntegerString(String s, NumberParseOptions options) {
        return new JsonNumber(Integer.parseInt(s), options);
    }
    public static JsonNumber fromFloatString(String s) {
        return fromFloatString(s, new NumberParseOptions(true));
    }
    public static JsonNumber fromFloatString(String s, NumberParseOptions options) {
        return new JsonNumber(Float.parseFloat(s), options);
    }

    public void setValue(Number n) {
        this.value = n;
    }

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
    public String stringify(String indentation, String indentIncrement) {
        String s;
        if (this.parseOptions.usesHexadecimal()) s = Double.toHexString(this.value.doubleValue());
        else if (this.parseOptions.usesScientific())
            s = NumberParseOptions.sciFormat.format(this.value.doubleValue());
        else if (this.getAsDouble() == this.getAsInt()) {
            s = this.parseOptions.isInteger() ?
                        Integer.toString(this.value.intValue()) : Double.toString(this.value.doubleValue());
        } else s = Double.toString(this.value.doubleValue());
        return s;
    }
}
