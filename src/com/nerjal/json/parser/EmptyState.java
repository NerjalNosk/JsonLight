package com.nerjal.json.parser;

public class EmptyState extends AbstractState {
    public EmptyState(StringParser stringParser, ParserState olderState) {
        super(stringParser, olderState);
    }
}
