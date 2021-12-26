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
    public void read(char c) {
        if (c == '\n') this.parser.increaseLine();

        switch (c) {
            case ' ', '\n', '\t', '\r', '\f':
                return;
            case '{':
                this.openObject();
            case '[':
                this.openArray();
            case '/':
                this.openComment();
            default:
                this.error(String.format("unexpected character '%c'", c));
        }
    }

    @Override
    public JsonElement getElem() {
        if (this.element == null) return null;
        try {
            JsonObject object = this.element.getAsJsonObject();
            this.comments.forEach(c -> object.add(null, c));
            return object;
        } catch (JsonElementTypeException e) {
            JsonArray array = (JsonArray) this.element;
            this.comments.forEach(array::add);
            return array;
        }
    }

    @Override
    public void addSubElement(JsonElement element) {
        if (element.isComment()) this.comments.add((JsonComment) element);
        else if (element.isJsonObject() || element.isJsonArray()) this.element = element;
    }
}
