package com.nerjal.json.parser;

import com.nerjal.json.elements.JsonElement;
import com.nerjal.json.elements.JsonObject;

public class ObjectState extends AbstractState {
    private boolean seekNext = true;
    private boolean lookForKey = false;
    private boolean lookForAttributor = false;
    private boolean hasKey = false;
    private String key = null;
    private final JsonObject object = new JsonObject();

    public ObjectState(StringParser stringParser, ParserState olderState) {
        super(stringParser, olderState);
    }

    @Override
    public void openObject() {
        if (!this.hasKey) this.parser.switchState(new ObjectState(this.parser,this));
        else this.error(String.format(""));
    }

    @Override
    public void closeObject() {
        this.olderState.addSubElement(this.getElem());
        if (!this.seekNext) this.parser.switchState(this.olderState);
        else this.error(String.format(""));
    }

    @Override
    public void openArray() {
        if (!this.hasKey) this.parser.switchState(new ArrayState(this.parser, this));
        else this.error(String.format(""));
    }

    @Override
    public boolean isObject() {
        return true;
    }

    @Override
    public void read(char c) {
        switch (c) {
            case ' ','\t','\n':
                return;
            case '"':
                if (this.lookForKey) {
                    this.lookForKey = false;
                } // and so much more
        }
    }

    @Override
    public JsonElement getElem() {
        return this.object;
    }

    @Override
    public void addSubElement(JsonElement element) {
        this.object.add(this.key,element);
    }
}
