package com.nerjal.json.parser;

import com.nerjal.json.elements.JsonElement;
import com.nerjal.json.elements.JsonObject;
import com.nerjal.json.elements.JsonString;

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

    private void trailingError() {
        this.parser.forward(this.parser.getIndex()-this.trailingIndex);
        this.error("empty array iteration");
    }

    private void readKey() {
        if (!this.lookForKey) this.error(String.format("unexpected character %c",this.parser.getActual()));
        StringBuilder s = new StringBuilder(String.valueOf(this.parser.getActual()));
        while (true) {
            char c = this.parser.getNext();
            if ((c > 64 && c < 91) || (c > 96 && c < 123) || c == 95) s.append(c);
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
            case ' ', '\n', '\t', '\r', '\f':
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
            case '"', '\'':
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
            case '.','0','1','2','3','4','5','6','7','8','9','+','-':
                this.openNum();
                break;
            case 'n', 'N', 'i', 'I':
                if (this.lookForKey) this.readKey();
                else this.openNum();
                break;
            case 't', 'T', 'f', 'F':
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
