package com.nerjal.json.parser.options;

public class StringParseOptions {
    private QuoteFormat format;

    public StringParseOptions(QuoteFormat format) {
        this.format = format;
    }

    public StringParseOptions() {
        this(QuoteFormat.DOUBLE_QUOTES);
    }

    public boolean usesSingleQuotes() {
        return this.format == QuoteFormat.SINGLE_QUOTES;
    }

    public boolean usesDoubleQuotes() {
        return this.format == QuoteFormat.DOUBLE_QUOTES;
    }

    public void setUseSingleQuotes() {
        this.format = QuoteFormat.SINGLE_QUOTES;
    }

    public void setUseDoubleQuotes() {
        this.format = QuoteFormat.DOUBLE_QUOTES;
    }

    public enum QuoteFormat {
        SINGLE_QUOTES,
        DOUBLE_QUOTES
    }
}
