package com.nerjal.json.parser;

import com.nerjal.json.elements.JsonElement;
import com.nerjal.json.elements.JsonString;

public class StringState extends AbstractState {
    private boolean precIsBackslash = false;
    private StringBuilder val = new StringBuilder();

    public StringState(StringParser stringParser, ParserState olderState) {
        super(stringParser, olderState);
    }

    @Override
    public void closeString() {
        this.parser.switchState(this.olderState);
    }

    @Override
    public void read(char c) {
        if (c == '\n') this.parser.error("unexpected newline");
        if (c == '\\') this.precIsBackslash = !this.precIsBackslash;
        if (c == '"' && !this.precIsBackslash) this.closeString();
        else if (this.precIsBackslash) {
            switch (c) {
                case 'b': this.val.append('\b');
                case 'f': this.val.append('\f');
                case 'n': this.val.append('\f');
                case 'r': this.val.append('\r');
                case 't': this.val.append('\t');
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
