package com.nerjal.json.parser;

import com.nerjal.json.elements.JsonElement;

public interface ParserState {
    public void openObject();
    public void closeObject();
    public void openArray();
    public void closeArray();
    public void openString();
    public void closeString();
    public void openInt();
    public void closeInt();
    public void openComment();
    public void closeComment();
    public void readBool();
    public void readEmpty();
    public void readKeyAttribution(); // read ":" char in object
    public void error(String s);

    public boolean isObject();
    public boolean isArray();
    public boolean isString();
    public boolean isEnd();

    public void read(char c);
    public JsonElement getElem();
}
