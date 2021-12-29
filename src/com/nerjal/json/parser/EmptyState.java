package com.nerjal.json.parser;

import com.nerjal.json.elements.JsonArray;
import com.nerjal.json.elements.JsonComment;
import com.nerjal.json.elements.JsonElement;
import com.nerjal.json.elements.JsonObject;

import java.util.ArrayList;
import java.util.List;

import static com.nerjal.json.JsonError.*;

public class EmptyState extends AbstractState {
    private JsonElement element = null;
    private final List<JsonComment> comments = new ArrayList<>();

    public EmptyState(StringParser stringParser, ParserState olderState) {
        super(stringParser, olderState);
    }

    @Override
    public void openObject() {
        if (this.element == null) this.parser.switchState(new ObjectState(this.parser, this));
        else this.error("unexpected character '{'");
    }

    @Override
    public void openArray() {
        if (this.element == null) this.parser.switchState(new ArrayState(this.parser, this));
        else this.error("unexpected character '['");
    }

    @Override
    public void openString() {
        boolean singleQuote = this.parser.getActual() == '\'';
        this.parser.switchState(new StringState(this.parser, this, singleQuote));
    }

    @Override
    public void openNum() {
        this.parser.switchState(new NumberState(this.parser, this));
    }

    @Override
    public void read(char c) {
        if (c == '\n') this.parser.increaseLine();

        switch (c) {
            case ' ', '\n', '\t', '\r', '\f':
                return;
            case '{':
                this.openObject();
                break;
            case '[':
                this.openArray();
                break;
            case '"', '\'':
                this.openString();
                break;
            case '.','0','1','2','3','4','5','6','7','8','9','+','-', 'n', 'N', 'i', 'I':
                this.openNum();
                break;
            case 't', 'T', 'f', 'F':
                this.readBool(c);
                break;
            case '/':
                this.openComment();
                break;
            default:
                this.error(String.format("unexpected character '%c'", c));
        }
    }

    @Override
    public JsonElement getElem() {
        if (this.element == null) return null;
        this.comments.forEach(this.element::addRootComment);
        return this.element;
    }

    @Override
    public void addSubElement(JsonElement element) {
        if (element.isComment()) this.comments.add((JsonComment) element);
        else if (element.isJsonObject() || element.isJsonArray()) this.element = element;
    }
}
