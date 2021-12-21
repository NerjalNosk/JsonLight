package com.nerjal.json.parser;

import com.nerjal.json.elements.JsonComment;
import com.nerjal.json.elements.JsonElement;

public class CommentState extends AbstractState {
    private boolean isBlock;
    private final StringBuilder val = new StringBuilder();

    public CommentState(StringParser stringParser, ParserState olderState, boolean block) {
        super(stringParser, olderState);
    }

    @Override
    public void closeComment() {
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

    @Override
    public JsonElement getElem() {
        return new JsonComment(val.toString());
    }
}
