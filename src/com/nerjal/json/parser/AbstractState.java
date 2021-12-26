package com.nerjal.json.parser;

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
    public void openComment() {

    }

    @Override
    public void closeComment() {

    }

    @Override
    public void readBool() {

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
