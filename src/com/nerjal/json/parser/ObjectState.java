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
    private String key = null;
    private final JsonObject object = new JsonObject();

    public ObjectState(StringParser stringParser, ParserState olderState) {
        super(stringParser, olderState);
    }

    @Override
    public void openObject() {
        if (this.lookForValue) this.parser.switchState(new ObjectState(this.parser,this));
        else this.error("unexpected object key type");
    }

    @Override
    public void closeObject() {
        this.olderState.addSubElement(this.getElem());
        if (this.requiresIterator || !this.started) this.parser.switchState(this.olderState);
        else this.error("expected object node");
    }

    @Override
    public void openArray() {
        if (this.lookForValue) this.parser.switchState(new ArrayState(this.parser, this));
        else this.error("unexpected object key type");
    }

    @Override
    public void openString() {
        this.started = true;
        if (this.lookForKey || this.lookForValue) this.parser.switchState(new StringState(this.parser, this));
        else this.error("unexpected string initializer '\"'");
    }

    @Override
    public boolean isObject() {
        return true;
    }

    @Override
    public void read(char c) {
        if (c == '\n') this.parser.increaseLine();

        switch (c) {
            case ' ','\t','\n':
                return;
            case ',':
                if (this.requiresIterator) this.requiresIterator = false;
                else this.error("unexpected iterator ','");
            case ':':
                if (this.lookForAttributive) this.lookForAttributive = false;
                else this.error("unexpected key-value attributive ':'");
            case '"':
                this.openString();
            case '{':
                this.openObject();
            case '[':
                this.openArray();
            case '/':
                this.openComment();
            case '0','1','2','3','4','5','6','7','8','9':
                this.openInt();
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
