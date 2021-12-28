package com.nerjal.json.parser;

import com.nerjal.json.elements.JsonArray;
import com.nerjal.json.elements.JsonElement;

public class ArrayState extends AbstractState {
    private boolean started = false;
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
        else this.error("unexpected '{' character");
    }

    @Override
    public void openArray() {
        if (trailingIterator) this.trailingError();
        else if (this.lookForValue) this.parser.switchState(new ArrayState(this.parser, this));
        else this.error("unexpected '[' character");
    }

    @Override
    public void closeArray() {
        if (!(this.lookForValue || this.started)) {
            this.olderState.addSubElement(this.getElem());
            this.parser.switchState(this.olderState);
        }
    }

    @Override
    public void openString() {
        boolean singleQuote = this.parser.getActual() == '\'';
        if (trailingIterator) this.trailingError();
        else if (this.lookForValue) this.parser.switchState(new StringState(this.parser, this, singleQuote));
        else this.error("unexpected '\"' character");
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
            case ' ', '\n', '\t', '\r', '\f':
                return;
            default:
                this.started = true;
        }

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
            case '.','0','1','2','3','4','5','6','7','8','9','+','-', 'n', 'N', 'i', 'I':
                this.openNum();
                break;
            case 't', 'T', 'f', 'F':
                this.readBool(c);
                break;
            case ']':
                this.closeArray();
                break;
            default:
                this.error(String.format("unexpected character '%c'", c));
        }
    }

    @Override
    public JsonArray getElem() {
        return this.array;
    }

    @Override
    public void addSubElement(JsonElement element) {
        this.array.add(element);
    }
}
