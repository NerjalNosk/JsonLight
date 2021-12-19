package com.nerjal.json;

import com.nerjal.json.elements.JsonArray;
import com.nerjal.json.elements.JsonElement;
import com.nerjal.json.elements.JsonObject;
import com.nerjal.json.parser.StringParser;

import java.util.Map;

import static com.nerjal.json.JsonError.*;

public abstract class JsonParser {

    /**
     * Allows to get a String version of the giver JsonElement
     * (automatically calls for the value's toString method if primitive)
     * @param json the JsonElement to parse to String
     * @return String: the string version of the JsonElement
     * @throws JsonElementTypeException if the given JsonElement isn't valid
     */
    public static String parseJson(JsonElement json) throws JsonElementTypeException {
        return parseJson(json, 0, 2);
    }

    /**
     * Allows to get a String version of the giver JsonElement
     * (automatically calls for the value's toString method if primitive)
     * @param json the JsonElement to parse to String
     * @param space the indentation level (for recursive parsing, uses 0 by default)
     * @return String: the string version of the JsonElement
     * @throws JsonElementTypeException if the given JsonElement isn't valid
     */
    public static String parseJson(JsonElement json, int space) throws JsonElementTypeException {
        return parseJson(json, space, 2);
    }

    /**
     * Allows to get a String version of the giver JsonElement
     * (automatically calls for the given object's toString method if primitive)
     * @param json the JsonElement to parse to String
     * @param space the indentation level (for recursive parsing, uses 0 by default)
     * @param tabulation the number of spaces added in the beginning of each line by indentation level
     * @return String: the string version of the JsonElement
     * @throws JsonElementTypeException if the given JsonElement isn't valid
     */
    public static String parseJson(JsonElement json, int space, int tabulation) throws JsonElementTypeException {
        if (json.isPrimitive()) {
            return  json.isString() ? String.format("\"%s\"",json.getAsString()) : json.toString();
        }
        StringBuilder out = new StringBuilder();
        int spacing = space + 1;
        String tab = " ".repeat(tabulation);
        if (json.isJsonArray()) {
            JsonArray array = json.getAsJsonArray();
            out.append("[\n");
            for (int i = 0; i < array.size(); i++) {
                out.append("  ".repeat(spacing)).append(parseJson(array.get(i), spacing, tabulation));
                if (i + 1 < array.size()) out.append(",");
                out.append("\n");
            }
            out.append("  ".repeat(space)).append("]");
            return out.toString();
        }
        if (json.isJsonObject()) {
            JsonObject object = json.getAsJsonObject();
            out.append("{\n");
            for (Map.Entry<String,JsonElement> entry : object.entrySet()) {
                JsonElement elem = entry.getValue();
                out.append(tab.repeat(spacing)).append(String.format("\"%s\"",entry.getKey())).append(" : ");
                out.append(parseJson(elem, spacing, tabulation)).append(",\n");
            }
            out.deleteCharAt(out.lastIndexOf(","));
            out.append("  ".repeat(space)).append("]");
            return out.toString();
        }
        throw new JsonElementTypeException(String.format("Unknown Json type element %s",json.getClass().getName()));
    }

    /**
     * Parses a string to a JsonElement. The JsonElement type will depend on the String content
     * @param s String: the String to parse
     * @return the parsed JsonElement. Can be an instance of any class implementing JsonElement
     * @throws JsonParseException if the given string cannot be parsed (missing quotes, braces, commas, etc.)
     */
    public static JsonElement parseString(String s) throws JsonParseException {
        StringParser parser = new StringParser(s);
        return null;
    }
}
