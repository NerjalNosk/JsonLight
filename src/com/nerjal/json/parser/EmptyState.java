package com.nerjal.json.parser;

public class EmptyState extends AbstractState {
    public EmptyState(StringParser stringParser, ParserState olderState) {
        super(stringParser, olderState);
    }

    @Override
    public void openComment() {
        char c = this.parser.getNext();
        if (c == '/') {
            this.parser.forward(1);
            this.parser.switchState(new CommentState(this.parser, this, false));
        } else if (c == '*') {
            this.parser.forward(1);
            this.parser.switchState(new CommentState(this.parser, this, true));
        } else this.parser.error(String.format("Unexpected character '/' on index %d", this.parser.getIndex()));
    }
}
