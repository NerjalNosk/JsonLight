package com.nerjal.json.elements;

import com.nerjal.json.parser.options.BooleanParseOptions;

public class JsonBoolean extends JsonElement {
    private boolean value;
    private BooleanParseOptions parseOptions;

    public JsonBoolean() {
        this(false);
    }

    public JsonBoolean(boolean b) {
        this(b, new BooleanParseOptions());
    }

    public JsonBoolean(boolean b, BooleanParseOptions options) {
        this.value = b;
        this.parseOptions = options;
    }

    public void setValue(boolean b) {
        this.value = b;
    }

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
    public String stringify(String indentation, String indentIncrement, JsonStringifyStack stack) {
        if (this.parseOptions.usesAllLowercase()) return String.valueOf(this.value);
        else if (this.parseOptions.usesAllUppercase()) return this.value ? "TRUE" : "FALSE";
        else return this.value ? "True" : "False";
    }
}
