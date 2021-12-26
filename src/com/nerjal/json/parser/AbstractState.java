package com.nerjal.json.parser;

import com.nerjal.json.elements.JsonBoolean;
import com.nerjal.json.elements.JsonElement;

public abstract class AbstractState implements ParserState {
    protected StringParser parser;
    protected ParserState olderState;
    public AbstractState(StringParser stringParser, ParserState olderState) {
        this.parser = stringParser;
    }

    @Override
    public void openObject() {

    }

    @Override
    public void closeObject() {

    }

    @Override
    public void openArray() {

    }

    @Override
    public void closeArray() {

    }

    @Override
    public void openString() {

    }

    @Override
    public void closeString() {

    }

    @Override
    public void openInt() {

    }

    @Override
    public void closeInt() {

    }

    @Override
    public final void openComment() {
        this.parser.forward(1);
        switch (this.parser.getActual()) {
            case '/': this.parser.switchState(new CommentState(this.parser, this, false));
            case '*': this.parser.switchState(new CommentState(this.parser, this, true));
            default: this.error("unexpected character '/'");
        }
    }

    @Override
    public void closeComment() {

    }

    @Override
    public final void readBool(char c) {
        switch (c) {
            case 't', 'T':
                if (String.valueOf(this.parser.getNext(3)).equalsIgnoreCase("rue")) {
                    this.parser.forward(3);
                    this.addSubElement(new JsonBoolean(true));
                } else this.error(String.format("unexpected character '%c'",c));
            case 'f', 'F':
                if (String.valueOf(this.parser.getNext(4)).equalsIgnoreCase("alse")) {
                    this.parser.forward(4);
                    this.addSubElement(new JsonBoolean(false));
                } else this.error(String.format("unexpected character '%c'", c));
        }
    }

    @Override
    public void readEmpty() {

    }

    @Override
    public void readKeyAttribution() {

    }

    @Override
    public final void error(String s) {
        this.parser.error(s);
    }


    @Override
    public boolean isObject() {
        return false;
    }
    @Override
    public boolean isArray() {
        return false;
    }
    @Override
    public boolean isString() {
        return false;
    }
    @Override
    public boolean isEnd() {
        return this.olderState == null;
    }


    @Override
    public void read(char c) {

    }
    @Override
    public JsonElement getElem() {
        return null;
    }

    @Override
    public void addSubElement(JsonElement element) {}
}
