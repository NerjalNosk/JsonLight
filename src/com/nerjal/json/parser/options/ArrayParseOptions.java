package com.nerjal.json.parser.options;

public class ArrayParseOptions {
    private int numPerLine;
    private ArrayFormat format;

    public ArrayParseOptions(ArrayFormat format, int numPerLine) {
        this.format = format;
        this.numPerLine = numPerLine;
    }

    public ArrayParseOptions(ArrayFormat format) {
        this(format, format == ArrayFormat.MULTIPLE_PER_LINE ? 2 : 0);
    }

    public ArrayParseOptions() {
        this(ArrayFormat.ONE_PER_LINE);
    }

    public void setFormat(ArrayFormat format) {
        this.format = format;
    }

    public void  setNumPerLine(int i) {
        this.numPerLine = i;
    }

    public int getNumPerLine() {
        if (this.format == ArrayFormat.INLINE) return Integer.MAX_VALUE;
        if (this.format == ArrayFormat.ONE_PER_LINE) return 1;
        return this.numPerLine;
    }

    public enum ArrayFormat {
        INLINE,
        ONE_PER_LINE,
        MULTIPLE_PER_LINE
    }
}
