package com.nerjal.json.parser;

public class NumberState extends AbstractState {
    private int powerOfTen = 0;
    private boolean foundE = false;
    private boolean foundDecimal = false;
    public NumberState(StringParser stringParser, ParserState olderState) {
        super(stringParser, olderState);
    }
}
