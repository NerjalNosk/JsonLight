package io.github.nerjalnosk.jsonlight.parser;

import io.github.nerjalnosk.jsonlight.elements.JsonElement;
import io.github.nerjalnosk.jsonlight.elements.JsonObject;
import io.github.nerjalnosk.jsonlight.elements.JsonString;
import io.github.nerjalnosk.jsonlight.parser.options.ObjectParseOptions;

/**
 * The {@link StringParser} JSON
 * object parsing state class.<br>
 * Can hold other elements, in
 * order to fill in the parsed
 * object. Thus, can open any
 * kind of other states.
 * @author nerjal
 */
public class ObjectState extends AbstractState {
    private boolean foundLineBreak = false;
    private boolean lookForKey = true;
    private boolean lookForAttributive = false;
    private boolean lookForValue = false;
    private boolean requiresIterator = false;
    private boolean trailingIterator = false;
    private int trailingIndex = 0;
    private String key = null;
    private final JsonObject object = new JsonObject(new ObjectParseOptions(true));

    public ObjectState(StringParser stringParser, ParserState olderState) {
        super(stringParser, olderState);
    }

    /**
     * Sets the affiliated
     * parser to throw an
     * exception due to an
     * empty iteration in
     * the object being
     * parsed.
     */
    private void trailingError() {
        this.parser.forward(this.parser.getIndex()-this.trailingIndex);
        this.error("empty object iteration");
    }

    /**
     * Reads an object key from
     * the parser's string, and
     * sets the state as looking
     * for an attributive char.
     * ( {@code ':'} )
     */
    private void readKey() {
        if (!this.lookForKey) this.unexpectedCharError(this.parser.getActual());
        StringBuilder s = new StringBuilder(String.valueOf(this.parser.getActual()));
        while (true) {
            char c = this.parser.getNext();
            if ((c > 47 && c < 58) || (c > 64 && c < 91) || (c > 96 && c < 123) || c == 95) s.append(c);
            else break;
            this.parser.forward();
        }
        this.key = s.toString();
        this.lookForKey = false;
        this.requiresIterator = false;
        this.lookForAttributive = true;
    }

    private boolean readLineBreak() {
        this.parser.increaseLine();
        if (this.parser.options.lineIter && this.requiresIterator) {
            this.foundLineBreak = true;
            return true;
        }
        return false;
    }

    /**
     * Reads an object iterator
     * ({@code ','})
     */
    private void readIterator() {
        if (this.requiresIterator) {
            this.requiresIterator = false;
            this.lookForKey = true;
        } else if (this.lookForKey) {
            this.trailingIterator = true;
            this.trailingIndex = this.parser.getIndex();
        } else this.error("unexpected iterator");
    }

    /**
     * Reads an object key-value
     * pair separator
     * ({@code ':'})
     */
    private void readAttributive() {
        if (this.lookForAttributive) {
            this.lookForAttributive = false;
            this.lookForValue = true;
        } else this.error("unexpected key-value attributive ':'");
    }

    private boolean canAcceptKey() {
        return this.lookForKey || this.foundLineBreak;
    }

    @Override
    public void close() {
        if (this.lookForAttributive || this.lookForValue) this.error("incomplete object node");
        else {
            this.olderState.addSubElement(this.getElem());
            this.parser.switchState(this.olderState);
        }
    }

    @Override
    public void openObject() {
        if (trailingIterator) this.trailingError();
        else if (this.lookForValue) this.parser.switchState(new ObjectState(this.parser,this));
        else if (this.canAcceptKey()) this.error("unexpected object key type");
        else this.unexpectedCharError('{');
    }

    @Override
    public void openArray() {
        if (trailingIterator) this.trailingError();
        else if (this.lookForValue) this.parser.switchState(new ArrayState(this.parser, this));
        else this.error("unexpected object key type");
    }

    @Override
    public void openString() {
        boolean singleQuote = this.parser.getActual() == '\'';
        if (trailingIterator) this.trailingError();
        else if (this.canAcceptKey() || this.lookForValue)
            this.parser.switchState(new StringState(this.parser, this, singleQuote));
        else this.error("unexpected string initializer '\"'");
    }

    @Override
    public void openNum() {
        if (trailingIterator) this.trailingError();
        else if (this.lookForValue) this.parser.switchState(new NumberState(this.parser, this));
        else this.unexpectedCharError(this.parser.getActual());
    }

    @Override
    public void openId() {
        if (this.trailingIterator) this.trailingError();
        else if (this.lookForValue) this.parser.switchState(new IdState(this.parser, this));
        else this.unexpectedCharError(this.parser.getActual());
    }

    @Override
    public void read(char c) {
        if ((c == '\n' || c == '\r') && this.readLineBreak()) {
            return;
        }
        if (Character.isWhitespace(c)) return;

        switch (c) {
            case ',':
                this.readIterator();
                break;
            case ':':
                this.readAttributive();
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
                if (this.canAcceptKey()) this.readKey();
                else this.readNull(c);
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
                this.openNum();
                break;
            case 'i':
            case 'I':
                if (this.canAcceptKey()) this.readKey();
                else this.openNum();
                break;
            case 't':
            case 'T':
            case 'f':
            case 'F':
                if (this.canAcceptKey()) this.readKey();
                else this.readBool(c);
                break;
            case '<':
                this.openId();
                break;
            case Character.MIN_VALUE:
                if (!this.parser.options.autoClose) {
                    this.disabledError("autoclosing");
                    break;
                }
            case '}':
                this.close();
                break;
            default:
                if (this.lookForKey) this.readKey();
                else this.unexpectedCharError(c);
        }
    }

    @Override
    public JsonObject getElem() {
        return this.object;
    }

    @Override
    public void addSubElement(JsonElement element) {
        if (element.isComment()) this.object.add(null, element);
        else if (this.lookForKey) {
            if (element.isString()) {
                this.key = ((JsonString) element).getAsString();
                this.lookForKey = false;
                this.foundLineBreak = false;
                this.lookForAttributive = true;
                this.requiresIterator = false;
            } else this.error("unexpected object key type found while parsing");
        } else {
            if (!this.object.add(this.key, element))
                this.error(String.format("duplicate value for key %s", this.key));
            this.lookForValue = false;
            this.requiresIterator = true;
        }
    }
}
