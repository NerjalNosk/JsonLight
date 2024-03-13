package io.github.nerjalnosk.jsonlight.parser;

import io.github.nerjalnosk.jsonlight.elements.JsonElement;
import io.github.nerjalnosk.jsonlight.elements.JsonString;
import io.github.nerjalnosk.jsonlight.parser.options.StringParseOptions;

/**
 * The {@link StringParser} JSON
 * string parsing state class.<br>
 * Cannot hold other elements<br>
 * Depending on the state opening
 * char, and therefore the string
 * quoting, closes on either
 * <i>{@code '}</i> or
 * <i>{@code "}</i>
 * @author nerjal
 */
public class StringState extends AbstractState {
    private boolean preIsBackslash = false;
    private final boolean isSingleQuoteString;
    private final StringBuilder val = new StringBuilder();

    public StringState(StringParser stringParser, ParserState olderState, boolean isSingleQuote) {
        super(stringParser, olderState);
        this.isSingleQuoteString = isSingleQuote;
    }

    @Override
    public void close() {
        this.parser.switchState(this.olderState);
        this.olderState.addSubElement(this.getElem());
    }

    @Override
    public void read(char c) {
        if (c == '\n' &! this.preIsBackslash) this.parser.error("unexpected newline");
        if (c == Character.MIN_VALUE) this.close();
        else if (c == '\\') {
            if (this.preIsBackslash) this.val.append('\\');
            this.preIsBackslash = !this.preIsBackslash;
        }
        else if (c == '"' && !this.preIsBackslash && !this.isSingleQuoteString) this.close();
        else if (c == '\'' && !this.preIsBackslash && this.isSingleQuoteString) this.close();
        else if (this.preIsBackslash) {
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
            this.preIsBackslash = false;
        } else this.val.append(c);
    }

    @Override
    public JsonElement getElem() {
        return new JsonString(val.toString(),
                new StringParseOptions(isSingleQuoteString ? StringParseOptions.QuoteFormat.SINGLE_QUOTES : StringParseOptions.QuoteFormat.DOUBLE_QUOTES));
    }
}
