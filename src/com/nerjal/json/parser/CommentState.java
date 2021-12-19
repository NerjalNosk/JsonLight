package com.nerjal.json.parser;

public class CommentState extends AbstractState {
    private boolean isBlock;
    private final StringBuilder val = new StringBuilder();

    public CommentState(StringParser stringParser, ParserState olderState) {
        super(stringParser, olderState);
    }

    @Override
    public void closeComment() {
        this.parser.addComment(this.val.toString());
        this.parser.switchState(this.olderState);
    }

    @Override
    public void read(char c) {
        if (c == '\n' && !this.isBlock) this.closeComment();
        if (c == '*' && this.isBlock && this.parser.getNext() == '/') {
            this.parser.forward(1);
            this.closeComment();
        }
        else this.val.append(c);
    }
}
