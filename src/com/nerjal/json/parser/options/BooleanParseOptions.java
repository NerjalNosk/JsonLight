package com.nerjal.json.parser.options;

public class BooleanParseOptions {
    private BooleanFormat format;

    public BooleanParseOptions(BooleanFormat format) {
        this.format = format;
    }

    public BooleanParseOptions() {
        this(BooleanFormat.ALL_LOWERCASE);
    }

    public void setFormat(BooleanFormat format) {
        this.format = format;
    }

    public boolean usesAllUppercase() {
        return this.format == BooleanFormat.ALL_UPPERCASE;
    }

    public boolean usesAllLowercase() {
        return this.format == BooleanFormat.ALL_LOWERCASE;
    }

    public boolean usesTitleFormat() {
        return this.format == BooleanFormat.TITLE;
    }

    public enum BooleanFormat {
        ALL_UPPERCASE,
        ALL_LOWERCASE,
        TITLE
    }
}
