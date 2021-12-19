package com.nerjal.json.parser;

import static com.nerjal.json.JsonError.*;
import com.nerjal.json.elements.JsonElement;

import java.util.HashMap;
import java.util.Map;

public class StringParser {
    private ParserState state;
    private String readStr;
    private int index = 0;
    private int line = 1;
    private boolean isErrored = false;
    private String errMessage = null;
    private Map<Integer,String> comments = new HashMap<>();

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


    public void read() {}

    public void error(String s) {
        this.isErrored = true;
        this.errMessage = s;
    }

    public void addComment(String com) {
        this.comments.put(this.index, com);
    }

    public boolean switchState(ParserState parserState) {
        boolean out = !parserState.getClass().isAssignableFrom(parserState.getClass());
        if (out) this.state = parserState;
        return out;
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
}
