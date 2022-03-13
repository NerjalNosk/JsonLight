package com.nerjal.json.parser;

import com.nerjal.json.elements.JsonElement;
import com.nerjal.json.elements.JsonObject;
import com.nerjal.json.elements.JsonString;

/**
 * The {@link StringParser} JSON
 * object parsing state class.<br>
 * Can hold other elements, in
 * order to fill in the parsed
 * object. Thus, can open any
 * kind of other states.
 * @author Nerjal Nosk
 */
public class ObjectState extends AbstractState {
    private boolean lookForKey = true;
    private boolean lookForAttributive = false;
    private boolean lookForValue = false;
    private boolean requiresIterator = false;
    private boolean trailingIterator = false;
    private int trailingIndex = 0;
    private String key = null;
    private final JsonObject object = new JsonObject();

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
        if (!this.lookForKey) this.error(String.format("unexpected character %c",this.parser.getActual()));
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

    @Override
    public void openObject() {
        if (trailingIterator) this.trailingError();
        else if (this.lookForValue) this.parser.switchState(new ObjectState(this.parser,this));
        else if (this.lookForKey) this.error("unexpected object key type");
        else this.error("unexpected '{' character");
    }

    @Override
    public void closeObject() {
        this.olderState.addSubElement(this.getElem());
        if (this.lookForAttributive || this.lookForValue) this.error("incomplete object node");
        else this.parser.switchState(this.olderState);
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
        else if (this.lookForKey || this.lookForValue)
            this.parser.switchState(new StringState(this.parser, this, singleQuote));
        else this.error("unexpected string initializer '\"'");
    }

    @Override
    public void openNum() {
        if (trailingIterator) this.trailingError();
        else if (this.lookForValue) this.parser.switchState(new NumberState(this.parser, this));
        else this.error(String.format("unexpected character '%c'", this.parser.getActual()));
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
            case ',':
                if (this.requiresIterator) {
                    this.requiresIterator = false;
                    this.lookForKey = true;
                } else if (this.lookForKey) {
                    this.trailingIterator = true;
                    this.trailingIndex = this.parser.getIndex();
                } else this.error("unexpected iterator ','");
                break;
            case ':':
                if (this.lookForAttributive) {
                    this.lookForAttributive = false;
                    this.lookForValue = true;
                } else this.error("unexpected key-value attributive ':'");
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
                if (this.lookForKey) this.readKey();
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
                if (this.lookForKey) this.readKey();
                else this.openNum();
                break;
            case 't':
            case 'T':
            case 'f':
            case 'F':
                if (this.lookForKey) this.readKey();
                else this.readBool(c);
                break;
            case '}':
                this.closeObject();
                break;
            default:
                if (this.lookForKey) this.readKey();
                else this.error(String.format("unexpected character %c",c));
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
