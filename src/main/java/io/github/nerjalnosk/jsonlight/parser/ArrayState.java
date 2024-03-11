package io.github.nerjalnosk.jsonlight.parser;

import io.github.nerjalnosk.jsonlight.elements.JsonArray;
import io.github.nerjalnosk.jsonlight.elements.JsonElement;

/**
 * The {@link StringParser} JSON
 * array parsing state class.<br>
 * Can hold other elements, in
 * order to fill in the parsed
 * array. Thus, can open any
 * kind of other states.<br>
 * Closes on {@code ']'}
 * @author nerjal
 */
public class ArrayState extends AbstractState {
    private boolean lookForValue = true;
    private boolean requiresIterator = false;
    private boolean trailingIterator = false;
    private int trailingIndex = 0;
    private final JsonArray array = new JsonArray();

    public ArrayState(StringParser stringParser, ParserState olderState) {
        super(stringParser, olderState);
    }

    private void trailingError() {
        this.parser.forward(this.parser.getIndex()-this.trailingIndex);
        this.error("empty array iteration");
    }

    @Override
    public void openObject() {
        if (trailingIterator) this.trailingError();
        else if (this.lookForValue) this.parser.switchState(new ObjectState(this.parser, this));
        else this.unexpectedCharError(this.parser.getActual());
    }

    @Override
    public void openArray() {
        if (trailingIterator) this.trailingError();
        else if (this.lookForValue) this.parser.switchState(new ArrayState(this.parser, this));
        else this.unexpectedCharError(this.parser.getActual());
    }

    @Override
    public void closeArray() {
        this.olderState.addSubElement(this.getElem());
        this.parser.switchState(this.olderState);
    }

    @Override
    public void openString() {
        boolean singleQuote = this.parser.getActual() == '\'';
        if (trailingIterator) this.trailingError();
        else if (this.lookForValue) this.parser.switchState(new StringState(this.parser, this, singleQuote));
        else this.unexpectedCharError(this.parser.getActual());
    }

    @Override
    public void openNum() {
        if (trailingIterator) this.trailingError();
        else if (this.lookForValue) this.parser.switchState(new NumberState(this.parser, this));
        else this.unexpectedCharError(this.parser.getActual());
    }

    @Override
    public void openId() {
        if (trailingIterator) this.trailingError();
        else if (this.storedId != null) this.unexpectedIdError();
        else if (this.lookForValue) this.parser.switchState(new IdState(this.parser, this));
        else this.unexpectedCharError(this.parser.getActual());
    }

    @Override
    public void read(char c) {
        if (c == '\n' || c == '\r') this.parser.increaseLine();
        if (Character.isWhitespace(c)) return;

        switch (c) {
            case ',':
                if (this.requiresIterator) {
                    this.requiresIterator = false;
                    this.lookForValue = true;
                } else if (this.lookForValue) {
                    this.trailingIterator = true;
                    this.trailingIndex = this.parser.getIndex();
                } else this.error("unexpected iterator ','");
                break;
            case '"':
            case '\'':
                this.openString();
                break;
            case '{':
                this.openObject();
                break;
            case '[':
                this.openArray();
                break;
            case '/':
                this.openComment();
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
            case '<':
                this.openId();
                break;
            case ']':
                this.closeArray();
                break;
            default:
                this.unexpectedCharError(c);
        }
    }

    @Override
    public JsonArray getElem() {
        return this.array;
    }

    @Override
    public void addSubElement(JsonElement element) {
        this.array.add(element);
        this.lookForValue = false;
        this.requiresIterator = true;
        if (this.storedId != null && !element.isComment()) {
            if (this.parser.feedId(this.storedId, element)) {
                this.error("already mapped ID "+this.storedId);
            }
            this.storedId = null;
        }
    }
}
