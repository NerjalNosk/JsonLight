package com.nerjal.json.parser;

import static com.nerjal.json.JsonError.*;
import com.nerjal.json.elements.JsonElement;

public class StringParser {
    private ParserState state;
    private String readStr;
    private int index = 0;
    private int line = 1;
    private int lineIndex = 0;
    private boolean isErrored = false;
    private String errMessage = null;

    public StringParser() {}

    public StringParser(String s) {
        this.readStr = s;
        this.state = new EmptyState(this,null);
    }

    public void setParseString(String s) {
        this.readStr = s;
        this.state = new EmptyState(this, null);
        this.index = 0;
        this.line = 1;
    }

    public static JsonElement parse(String s) throws JsonParseException {
        StringParser parser = new StringParser();
        parser.setParseString(s);
        parser.read();
        return parser.getState().getElem();
    }

    private ParserState getState() {
        return this.state;
    }

    public int getIndex() {
        return index;
    }

    public void read() throws JsonParseException {
        while (this.index < this.readStr.length()) {
            if (this.isErrored) throw this.buildError();
            this.state.read(this.getNext());
            this.index++;
            this.lineIndex++;
        }
    }

    public void error(String s) {
        this.isErrored = true;
        this.errMessage = s;
    }

    public void switchState(ParserState parserState) {
        boolean out = !parserState.getClass().isAssignableFrom(parserState.getClass());
        if (out) this.state = parserState;
    }
    public void forward(int i) {
        this.index += i;
    }
    public char getNext() {
        return this.readStr.charAt(this.index+1);
    }
    public char[] getNext(int length) {
        return this.readStr.substring(this.index+1, this.index+1+length).toCharArray();
    }
    public char getActual() {
        return this.readStr.charAt(this.index);
    }
    public char getPrecedent() {
        return this.index == 0 ? null : this.readStr.charAt(this.index-1);
    }
    public char getOlder(int i) {
        return this.index - i < 0 ? null : this.readStr.charAt(this.index-i);
    }
    public void increaseLine() {
        this.line++;
        this.lineIndex = 0;
    }

    protected JsonParseException buildError() {
        return new JsonParseException(String.format(
                "Error parsing string to json element: %s at index %d of line %d",
                this.errMessage, this.lineIndex, this.line));
    }
}
