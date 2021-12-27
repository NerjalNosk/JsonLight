package com.nerjal.json.parser;

import com.nerjal.json.elements.JsonElement;
import com.nerjal.json.elements.JsonString;

public class StringState extends AbstractState {
    private boolean precIsBackslash = false;
    private final boolean isSingleQuoteString;
    private final StringBuilder val = new StringBuilder();

    public StringState(StringParser stringParser, ParserState olderState, boolean isSingleQuote) {
        super(stringParser, olderState);
        this.isSingleQuoteString = isSingleQuote;
    }

    @Override
    public void closeString() {
        this.parser.switchState(this.olderState);
    }

    @Override
    public void read(char c) {
        if (c == '\n') this.parser.error("unexpected newline");
        if (c == '\\') {
            if (this.precIsBackslash) this.val.append('\\');
            this.precIsBackslash = !this.precIsBackslash;
        } else if (c == '"' && !this.precIsBackslash && !this.isSingleQuoteString) this.closeString();
        else if (c == '\'' && !this.precIsBackslash && this.isSingleQuoteString) this.closeString();
        else if (this.precIsBackslash) {
            switch (c) {
                case 'b': this.val.append('\b'); // backspace
                case 'f': this.val.append('\f'); // from-feed
                case 'n': this.val.append('\f'); // newline
                case 'r': this.val.append('\r'); // return
                case 't': this.val.append('\t'); // tab
                default: this.val.append('\\').append(c);
            }
            this.precIsBackslash = false;
        } else this.val.append(c);
    }

    @Override
    public JsonElement getElem() {
        return new JsonString(val.toString());
    }
}
