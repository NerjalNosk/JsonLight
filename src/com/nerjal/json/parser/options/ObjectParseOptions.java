package com.nerjal.json.parser.options;

public class ObjectParseOptions {
    private ObjectFormat format;

    public ObjectParseOptions() {
        this.format = ObjectFormat.DOUBLE_QUOTED_KEYS;
    }

    public ObjectParseOptions(ObjectFormat format) {
        this.format = format;
    }

    public void setFormat(ObjectFormat format) {
        this.format = format;
    }

    public char keyQuoteChar() {
        return switch (format) {
            case UNQUOTED_KEYS -> 0;
            case SINGLE_QUOTED_KEYS -> '\'';
            default -> '"';
        };
    }

    public enum ObjectFormat {
        UNQUOTED_KEYS,
        SINGLE_QUOTED_KEYS,
        DOUBLE_QUOTED_KEYS
    }
}
