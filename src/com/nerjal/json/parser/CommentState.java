package com.nerjal.json.parser;

import com.nerjal.json.elements.JsonComment;
import com.nerjal.json.elements.JsonElement;

public class CommentState extends AbstractState {
    private boolean isBlock;
    private final StringBuilder val = new StringBuilder();
    private boolean onNewLine = false;

    public CommentState(StringParser stringParser, ParserState olderState, boolean block) {
        super(stringParser, olderState);
        this.isBlock = block;
    }

    @Override
    public void closeComment() {
        this.olderState.addSubElement(this.getElem());
        this.parser.switchState(this.olderState);
    }

    @Override
    public void read(char c) {
        if (c == '\n' || c == '\r') {
            this.parser.increaseLine();
            this.onNewLine = true;
        }

        if ((c == '\n' || c == '\r') && !this.isBlock) this.closeComment();
        else if (c == '*') {
            if (this.val.length() == 0) this.isBlock = true;
            else if (this.isBlock && this.parser.getNext() == '/') {
                this.parser.forward();
                this.closeComment();
            } else if (!this.onNewLine) this.val.append(c);
        } else if ((c == ' ' || c == '\t') && this.onNewLine) {
            if (this.parser.getPrecedent() == '*') this.onNewLine = false;
        } else this.val.append(c);
    }

    @Override
    public JsonElement getElem() {
        return new JsonComment(val.toString(),this.isBlock);
    }
}
