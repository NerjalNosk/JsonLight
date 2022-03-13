package com.nerjal.json.parser;

import com.nerjal.json.elements.JsonElement;
import com.nerjal.json.elements.JsonString;
import com.nerjal.json.parser.options.StringParseOptions;

import static com.nerjal.json.parser.options.StringParseOptions.QuoteFormat.*;

/**
 * The {@link StringParser} JSON
 * string parsing state class.<br>
 * Cannot hold other elements<br>
 * Depending on the state opening
 * char, and therefore the string
 * quoting, closes on either
 * <i>{@code '}</i> or
 * <i>{@code "}</i>
 * @author Nerjal Nosk
 */
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
        this.olderState.addSubElement(this.getElem());
    }

    @Override
    public void read(char c) {
        if (c == '\n' &! this.precIsBackslash) this.parser.error("unexpected newline");
        if (c == '\\') {
            if (this.precIsBackslash) this.val.append('\\');
            this.precIsBackslash = !this.precIsBackslash;
        } else if (c == '"' && !this.precIsBackslash && !this.isSingleQuoteString) this.closeString();
        else if (c == '\'' && !this.precIsBackslash && this.isSingleQuoteString) this.closeString();
        else if (this.precIsBackslash) {
            switch (c) {
                case 'b': {
                    this.val.append('\b'); // backspace
                    break;}
                case 'f': {
                    this.val.append('\f'); // from-feed
                    break;}
                case 'n': {
                    this.val.append('\n'); // newline
                    break;}
                case 'r': {
                    this.val.append('\r'); // return
                    this.parser.increaseLine(); // escaped return
                    break;}
                case 't': {
                    this.val.append('\t'); // tab
                    break;}
                case '\n': {
                    this.val.append('\n'); // escaped newline
                    this.parser.increaseLine();
                    break;}
                default: this.val.append('\\').append(c);
            }
            this.precIsBackslash = false;
        } else this.val.append(c);
    }

    @Override
    public JsonElement getElem() {
        return new JsonString(val.toString(),
                new StringParseOptions(isSingleQuoteString ? SINGLE_QUOTES : DOUBLE_QUOTES));
    }
}
