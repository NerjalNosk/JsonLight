package com.nerjal.json;

import com.nerjal.json.elements.JsonArray;
import com.nerjal.json.elements.JsonComment;
import com.nerjal.json.elements.JsonElement;
import com.nerjal.json.elements.JsonObject;
import com.nerjal.json.parser.StringParser;

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static com.nerjal.json.JsonError.*;

public abstract class JsonParser {

    /**
     * Allows to get a String version of the given JsonElement
     * (automatically calls for the value's toString method if primitive)
     * @param json the JsonElement to parse to String
     * @return String: the string version of the JsonElement
     * @throws JsonElementTypeException if the given JsonElement isn't valid
     */
    public static String parseJson(JsonElement json) throws JsonElementTypeException {
        return parseJson(json, 0, 2);
    }

    /**
     * Allows to get a String version of the given JsonElement
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
     * Allows to get a String version of the given JsonElement
     * (automatically calls for the given object's toString method if primitive)
     * @param json the JsonElement to parse to String
     * @param space the indentation level (for recursive parsing, uses 0 by default)
     * @param tabulation the number of spaces added in the beginning of each line by indentation level
     * @return String: the string version of the JsonElement
     * @throws JsonElementTypeException if the given JsonElement isn't valid
     */
    public static String parseJson(JsonElement json, int space, int tabulation) throws JsonElementTypeException {
        if (json.isPrimitive()) {
            return json.isString() ? String.format("\"%s\"",json.getAsString()) : json.toString();
        }
        StringBuilder out = new StringBuilder();
        String tab = " ".repeat(tabulation);
        if (json.isComment()) {
            JsonComment comment = json.getAsJsonComment();
            if (comment.isBlock()) {
                String[] lines = comment.getSplitValue();
                out.append("/*\n");
                for (String s : lines) out.append(tab.repeat(space)).append("* ").append(s).append("\n");
                out.append(tab.repeat(space)).append("*/\n");
            } else out.append("//").append(comment);
            return out.toString();
        }
        int spacing = space + 1;
        if (json.isJsonArray()) {
            JsonArray array = json.getAsJsonArray();
            out.append("[\n");
            AtomicInteger i = new AtomicInteger();
            AtomicBoolean eraseLastComma = new AtomicBoolean(false);
            AtomicInteger index = new AtomicInteger();
            array.forAll(elem -> {
                try {
                    out.append("  ".repeat(spacing)).append(parseJson(elem, spacing, tabulation));
                    if (elem.isComment()) eraseLastComma.set(false);
                    else {
                        if (index.get() + 1 < array.size()) {
                            out.append(",");
                            i.set(out.length() - 1);
                        }
                        eraseLastComma.set(true);
                        out.append("\n");
                    }
                } catch (JsonElementTypeException e) {
                    e.printStackTrace();
                }
                index.getAndIncrement();
            });
            if (eraseLastComma.get() && out.charAt(i.get())==',') out.deleteCharAt(i.get());
            out.append("  ".repeat(space)).append("]");
            return out.toString();
        }
        if (json.isJsonObject()) {
            JsonObject object = json.getAsJsonObject();
            out.append("{\n");
            int i = 0;
            // i is used to remove last iteration comma without caring about comments
            for (Map.Entry<String,JsonElement> entry : object.allEntriesSet()) {
                JsonElement elem = entry.getValue();
                out.append(tab.repeat(spacing));
                if (!elem.isComment()) {
                    out.append(String.format("\"%s\"", entry.getKey())).append(" : ");
                    out.append(parseJson(elem, spacing, tabulation)).append(",\n");
                    i = out.length()-2;
                } else {
                    out.append(parseJson(elem, spacing, tabulation));
                }
            }
            if (out.charAt(i) == ',') out.deleteCharAt(i);
            out.append("  ".repeat(space)).append("}\n");
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
