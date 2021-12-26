package com.nerjal.json.parser;

import com.nerjal.json.elements.JsonArray;
import com.nerjal.json.elements.JsonElement;

public class ArrayState extends AbstractState {
    private boolean started = false;
    private boolean lookForValue = true;
    private boolean requiresIterator = false;
    private final JsonArray array = new JsonArray();

    public ArrayState(StringParser stringParser, ParserState olderState) {
        super(stringParser, olderState);
    }

    @Override
    public void openObject() {
        if (this.lookForValue) this.parser.switchState(new ObjectState(this.parser, this));
        else this.error("unexpected '{' character");
    }

    @Override
    public void openArray() {
        if (this.lookForValue) this.parser.switchState(new ArrayState(this.parser, this));
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
        if (this.lookForValue) this.parser.switchState(new StringState(this.parser, this));
        else this.error("unexpected '\"' character");
    }

    @Override
    public void openNum() {
        if (this.lookForValue) this.parser.switchState(new NumberState(this.parser, this));
        else this.error(String.format("unexpected character '%c'", this.parser.getActual()));
    }

    @Override
    public boolean isArray() {
        return true;
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
                } else this.error("unexpected iterator ','");
            case '"':
                this.openString();
            case '{':
                this.openObject();
            case '[':
                this.openArray();
            case '/':
                this.openComment();
            case '0','1','2','3','4','5','6','7','8','9':
                this.openNum();
            case 't', 'T', 'f', 'F':
                this.readBool(c);
            case ']':
                this.closeArray();
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
