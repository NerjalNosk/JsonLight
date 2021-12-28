package com.nerjal.json;

public abstract class Tests {
    public static void main(String[] args) throws JsonError.JsonParseException, JsonError.JsonElementTypeException {
        System.out.println(JsonParser.parseJson(JsonParser.parseString("{a : 1}")));
    }
}
