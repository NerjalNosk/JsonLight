package com.nerjal.json;

import com.nerjal.json.elements.JsonArray;
import com.nerjal.json.elements.JsonElement;

public abstract class Tests {
    public static void main(String[] args) throws JsonError.JsonParseException, JsonError.JsonElementTypeException {
        /*
        * System.out.println(JsonParser.parseJson(JsonParser.parseString("{a : 1}")));
        * System.out.println(JsonParser.parseJson(JsonParser.parseString("[1,2,,,]")));
        * System.out.println(JsonParser.parseJson(JsonParser.parseString("{'a':1,'b':2}")));
        * System.out.println(JsonParser.parseJson(JsonParser.parseString("{a:'1'}")));
        */
        System.out.println(JsonParser.parseJson(JsonParser.parseString("[[], []]")));
        System.out.println(JsonParser.parseJson(JsonParser.parseString("[[],[],]")));
        System.out.println(JsonParser.parseJson(JsonParser.parseString("{a:\"1\\\n\"}")));
        System.out.println(JsonParser.parseJson(JsonParser.parseString("{a : 1,//a\nb:2}")));
        //System.out.println(JsonParser.parseJson(JsonParser.parseString("{a : 1//a\nb:2}")));
        JsonArray jArr = JsonParser.parseString("[1, 2, -5]").getAsJsonArray();
        for (JsonElement element : jArr) System.out.println(element.toString());
    }
}
