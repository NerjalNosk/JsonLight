package com.nerjal.json.parser;

import com.nerjal.json.elements.JsonElement;
import com.nerjal.json.elements.JsonObject;
import com.nerjal.json.elements.JsonString;

public class ObjectState extends AbstractState {
    private boolean started = false;
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
        if (this.requiresIterator || !this.started) this.parser.switchState(this.olderState);
        else this.error("expected object node");
    }

    @Override
    public void openArray() {
        if (trailingIterator) this.trailingError();
        else if (this.lookForValue) this.parser.switchState(new ArrayState(this.parser, this));
        else this.error("unexpected object key type");
    }

    @Override
    public void openString() {
        this.started = true;
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
        if (c == '\n') this.parser.increaseLine();

        switch (c) {
            case ' ', '\t', '\n', '\r', '\f':
                return;
            case ',':
                if (this.requiresIterator) {
                    this.requiresIterator = false;
                    this.lookForKey = true;
                } else if (this.lookForKey) {
                    this.trailingIterator = true;
                    this.trailingIndex = this.parser.getIndex();
                } else this.error("unexpected iterator ','");
            case ':':
                if (this.lookForAttributive) {
                    this.lookForAttributive = false;
                    this.lookForValue = true;
                } else this.error("unexpected key-value attributive ':'");
            case '"', '\'':
                this.openString();
            case '{':
                this.openObject();
            case '[':
                this.openArray();
            case '/':
                this.openComment();
            case '.','0','1','2','3','4','5','6','7','8','9','+','-':
                this.openNum();
            case 't', 'T', 'f', 'F':
                this.readBool(c);
            case '}':
                this.closeObject();
            default:
                this.error(String.format("unexpected character %c",c));
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
            this.object.add(this.key, element);
            this.lookForValue = false;
            this.requiresIterator = true;
        }
    }
}
