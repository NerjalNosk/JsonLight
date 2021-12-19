package com.nerjal.json.parser;

public class ArrayState extends AbstractState {
    private boolean foundComma = false;

    public ArrayState(StringParser stringParser, ParserState olderState) {
        super(stringParser, olderState);
    }
}
