package io.github.nerjalnosk.jsonlight.parser;

import io.github.nerjalnosk.jsonlight.JsonParser;
import io.github.nerjalnosk.jsonlight.elements.JsonElement;
import io.github.nerjalnosk.jsonlight.elements.JsonString;
import io.github.nerjalnosk.jsonlight.parser.options.StringParseOptions;

import java.util.HashMap;
import java.util.Map;

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
    private final Map<String, Integer> unicoded = new HashMap<>();

    public StringState(StringParser stringParser, ParserState olderState, boolean isSingleQuote) {
        super(stringParser, olderState);
        if (isSingleQuote) {
            this.disabledError("single-quoted strings (Json5)");
        }
        this.isSingleQuoteString = isSingleQuote;
    }

    private void unicodeError(char c) {
        this.error("Invalid hexadecimal character '"+c+"' in unicode code");
    }

    private void readUnicode() {
        if (!this.parser.options.parseUnicode) {
            this.disabledError("unicode parsing");
        }
        char c1 = this.parser.getNext();
        if (!JsonParser.isHex(c1)) {
            this.unicodeError(c1);
            return;
        }
        int i1 = JsonParser.hexValue(c1);
        int code = i1;
        int size = 4;
        int i = 1;
        if (i1 == 14) {
            // case 1110xxxx
            size = 6;
        } else if (i1 == 15) {
            // case 11110xxx
            size = 8;
            char c2 = this.parser.getNext();
            int i2 = JsonParser.hexValue(c2);
            if (i2 == -1 || (i2 >> 3) != 0) {
                this.unicodeError(c2);
                return;
            }
            code <<= 4;
            code += i2;
            i = 2;
        } else if ((i1 >> 1) != 6) {
            // case 110xxxxx
            this.unicodeError(c1);
            return;
        }
        while (i < size) {
            char c = this.parser.getNext();
            int v = JsonParser.hexValue(c);
            boolean b = (i % 2) == 0; // even char (e.g. capitals: XXXXxxxx XXXXxxxx)
            if (v == -1 || (b && (v >> 2) != 2)) {
                // check for 10xxxxxx on post-init code characters
                this.unicodeError(c);
                return;
            }
            code <<= 4;
            code += v;
            i++;
        }
        char[] c = Character.toChars(code);
        this.unicoded.putIfAbsent(String.valueOf(c), code);
        this.val.append(c);
    }

    @Override
    public void close() {
        this.parser.switchState(this.olderState);
        this.olderState.addSubElement(this.getElem());
    }

    @Override
    public void read(char c) {
        if (c == '\n') {
            if (!this.parser.options.json5) this.disabledError("multiline strings (Json5)");
            else if (!this.preIsBackslash) this.parser.error("unexpected newLine");
        }
        if (c == Character.MIN_VALUE) {
            if (!this.parser.options.autoClose) {
                this.disabledError("autoclosing");
                return;
            }
            this.close();
        }
        else if (c == '\\') {
            if (this.preIsBackslash) this.val.append('\\');
            this.preIsBackslash = !this.preIsBackslash;
        }
        else if (c == '"' && !this.preIsBackslash && !this.isSingleQuoteString) this.close();
        else if (c == '\'' && !this.preIsBackslash && this.isSingleQuoteString) this.close();
        else if (this.preIsBackslash) {
            switch (c) {
                case 'b':
                    this.val.append('\b'); // backspace
                    break;
                case 'n':
                    this.val.append('\n'); // newline
                    break;
                case 'r':
                    this.val.append('\r'); // return
                    this.parser.increaseLine(); // escaped return
                    break;
                case 's':
                    this.val.append(' '); // whitespace
                    break;
                case 't':
                    this.val.append('\t'); // tab
                    break;
                case 'u':
                    this.readUnicode(); // unicode code
                    break;
                case '\n':
                    this.val.append('\n'); // escaped newline
                    this.parser.increaseLine();
                    break;
                default: this.val.append('\\').append(c);
            }
            this.preIsBackslash = false;
        }
        else this.val.append(c);
    }

    @Override
    public JsonElement getElem() {
        return new JsonString(val.toString(),
                new StringParseOptions(isSingleQuoteString ? StringParseOptions.QuoteFormat.SINGLE_QUOTES : StringParseOptions.QuoteFormat.DOUBLE_QUOTES)
                        .withUnicoded(this.unicoded));
    }
}
