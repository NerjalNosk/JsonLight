package com.nerjal.json.parser;

import com.nerjal.json.elements.JsonElement;

public interface ParserState {
    void openObject();
    void closeObject();
    void openArray();
    void closeArray();
    void openString();
    void closeString();
    void openInt();
    void closeInt();
    void openComment();
    void closeComment();
    void readBool();
    void readEmpty();
    void readKeyAttribution(); // read ":" char in object
    void error(String s);

    boolean isObject();
    boolean isArray();
    boolean isString();
    boolean isEnd();

    void read(char c);
    JsonElement getElem();
    void addSubElement(JsonElement element);
}
