package io.github.nerjalnosk.jsonlight.parser;

import io.github.nerjalnosk.jsonlight.elements.JsonComment;
import io.github.nerjalnosk.jsonlight.elements.JsonElement;

import java.util.ArrayList;
import java.util.List;

/**
 * The {@link StringParser} JSON
 * root parsing state class.<br>
 * Can hold one other element, to
 * be the one returned by the
 * parser.<br>
 * Closes at the end of the
 * parsed string.
 * @author nerjal
 */
public class EmptyState extends AbstractState {
    private JsonElement element = null;
    private final List<JsonComment> comments = new ArrayList<>();

    public EmptyState(StringParser stringParser, ParserState olderState) {
        super(stringParser, olderState);
    }

    @Override
    public void openObject() {
        if (this.element == null) this.parser.switchState(new ObjectState(this.parser, this));
        else this.unexpectedCharError('{');
    }

    @Override
    public void openArray() {
        if (this.element == null) this.parser.switchState(new ArrayState(this.parser, this));
        else this.unexpectedCharError('[');
    }

    @Override
    public void openString() {
        boolean singleQuote = this.parser.getActual() == '\'';
        this.parser.switchState(new StringState(this.parser, this, singleQuote));
    }

    @Override
    public void openNum() {
        this.parser.switchState(new NumberState(this.parser, this));
    }

    @Override
    public void close() {
        // nothing to do here
    }

    @Override
    public void read(char c) {
        if (c == '\n' || c == '\r') this.parser.increaseLine();

        switch (c) {
            case ' ':
            case '\n':
            case '\t':
            case '\r':
            case '\f':
                return;
            case '{':
                this.openObject();
                break;
            case '[':
                this.openArray();
                break;
            case '"':
            case '\'':
                this.openString();
                break;
            case 'n':
            case 'N':
                this.readNull(c);
                break;
            case '.':
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
            case '+':
            case '-':
            case 'i':
            case 'I':
                this.openNum();
                break;
            case 't':
            case 'T':
            case 'f':
            case 'F':
                this.readBool(c);
                break;
            case '/':
                this.openComment();
                break;
            case '<':
                this.openId();
                break;
            default:
                this.error(String.format("unexpected character '%c'", c));
        }
    }

    @Override
    public JsonElement getElem() {
        if (this.element == null) return null;
        this.comments.forEach(this.element::addRootComment);
        return this.element;
    }

    @Override
    public void addSubElement(JsonElement element) {
        if (element.isComment()) this.comments.add((JsonComment) element);
        else if (this.element == null) {
            this.element = element;
            if (this.storedId != null) {
                if (this.parser.feedId(this.storedId, element)) {
                    this.error("already mapped ID "+this.storedId);
                }
                this.storedId = null;
            }
        }
        else this.error("multiple root elements found in Json");
    }
}
