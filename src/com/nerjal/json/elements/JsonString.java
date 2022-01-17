package com.nerjal.json.elements;

import com.nerjal.json.parser.options.StringParseOptions;

public class JsonString extends JsonElement {
    private String value;
    private StringParseOptions parseOptions;

    public JsonString(String s, StringParseOptions options) {
        this.value = s;
        this.parseOptions = options;
    }
    public JsonString(String value) {
        this(value, new StringParseOptions());
    }
    public JsonString(StringParseOptions options) {
        this(null, options);
    }
    public JsonString() {
        this((String) null);
    }

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
    public String typeToString() {
        return "String";
    }
    @Override
    public String getAsString() {
        return this.value;
    }

    @Override
    public String stringify(String indentation) {
        char c = this.parseOptions.usesDoubleQuotes() ? '"' : '\'';
        return String.format("%c%s%c",c,this.value,c);
    }
}
