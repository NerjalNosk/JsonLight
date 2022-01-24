package com.nerjal.json.parser;

import com.nerjal.json.elements.JsonElement;

public interface ParserState {
    void openObject();
    void closeObject();
    void openArray();
    void closeArray();
    void openString();
    void closeString();
    void openNum();
    void closeNum();
    void openComment();
    void closeComment();
    void readBool(char c);
    void readNull(char c);
    void error(String s);

    void read(char c);
    JsonElement getElem();
    void addSubElement(JsonElement element);
}
