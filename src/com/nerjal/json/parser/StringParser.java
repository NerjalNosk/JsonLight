package com.nerjal.json.parser;

import static com.nerjal.json.JsonError.*;
import com.nerjal.json.elements.JsonElement;

/**
 *
 * @see com.nerjal.json.JsonParser
 * @author Nerjal Nosk
 */

public class StringParser {
    private ParserState state;
    private String readStr;
    private int index = 0;
    private int line = 1;
    private int lineIndex = 0;
    private boolean stop = false;
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
        this.stop = false;
    }

    public static JsonElement parse(String s) throws JsonParseException {
        StringParser parser = new StringParser();
        parser.setParseString(s);
        parser.read();
        return parser.getElem();
    }

    public JsonElement parse() throws JsonParseException {
        if (this.readStr == null) throw new JsonParseException("Cannot parse null");
        this.read();
        return this.getElem();
    }

    public void read() throws JsonParseException {
        while (!this.stop) {
            if (this.isErrored) throw this.buildError();
            System.out.printf("reading char %c at index %d%n", this.getActual(), this.index);
            System.out.printf("reading with state %s%n",this.state.getClass().getName());
            this.state.read(this.getActual());
            this.index++;
            this.lineIndex++;
            if (this.index >= this.readStr.length()) this.stop = true;
        }
    }

    public JsonElement getElem() throws JsonParseException {
        if (!this.stop) throw new JsonParseException("Parser haven't parsed the given string");
        return this.state.getElem();
    }

    // state manipulation

    private ParserState getState() {
        return this.state;
    }

    public void switchState(ParserState parserState) {
        this.state = parserState;
    }

    // readStr chars and indexes manipulation

    public void forward() {
        this.index++;
    }
    public void forward(int i) {
        this.index += i;
    }
    public void increaseLine() {
        this.line++;
        this.lineIndex = 0;
    }
    public int getIndex() {
        return this.index;
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
        return this.index < 1 ? null : this.readStr.charAt(this.index-1);
    }
    public char[] getPrecedents(int i) {
        return this.readStr.substring(this.index-i,this.index).toCharArray();
    }

    // errors

    public void error(String s) {
        this.isErrored = true;
        this.errMessage = s;
    }

    protected JsonParseException buildError() {
        return new JsonParseException(String.format(
                "Error parsing string to json element: %s at index %d of line %d",
                this.errMessage, this.lineIndex, this.line));
    }
}
