package com.nerjal.json.parser.options;

import com.nerjal.json.JsonError;

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

    public void  setNumPerLine(long i) throws JsonError.IllegalLineElementsNumberException {
        if (i <= 0) throw new JsonError.IllegalLineElementsNumberException(
                "Cannot parse a JsonArray with less than one element per line");
        if (i == 1) this.format = ArrayFormat.ONE_PER_LINE;
        else if (i == Long.MAX_VALUE) this.format = ArrayFormat.INLINE;
        else {
            this.numPerLine = (int)i;
            this.format = ArrayFormat.MULTIPLE_PER_LINE;
        }
    }

    public int getNumPerLine() {
        if (this.format == ArrayFormat.INLINE) return Integer.MAX_VALUE;
        if (this.format == ArrayFormat.ONE_PER_LINE) return 1;
        return this.numPerLine;
    }

    public boolean isAllInOneLine() {
        return this.format == ArrayFormat.INLINE;
    }

    public enum ArrayFormat {
        INLINE,
        ONE_PER_LINE,
        MULTIPLE_PER_LINE
    }
}
