package com.nerjal.json;

import com.nerjal.json.elements.JsonElement;
import com.nerjal.json.parser.StringParser;

import static com.nerjal.json.JsonError.*;

/**
 * Default class for parsing JSON both way.<br>
 * All comments contained in the root of a JSON string being
 * parsed to a {@link JsonElement} will be put in the main
 * element of the JSON string. Thus, the main element can only
 * be a container element (so an Array or an Object). All other
 * root element will result on a {@link JsonParseException}<br>
 * However, all JsonElement can be parsed to a string, that
 * being used for recursive parsing of container JsonElements
 * <p><br>
 * Use {@link JsonParser#stringify(JsonElement)} and derivatives
 * in order to parse a JSON structure to a String object.
 * <blockquote><pre>
 * using
 *     String s = JsonParser.parseJson(jsonObject);
 * gives
 *     {
 *       "key": [value],
 *       ...
 *     }
 * </pre></blockquote>
 * <p>
 * Using {@link JsonParser#jsonify(String)} allows to get
 * the {@link JsonElement} of the given string.
 * <blockquote><pre>
 * using
 *     JsonElement jElem = JsonParser.parseString("{"a":["a",1,true]}");
 * gives
 *     JsonObject[ "a": JsonArray[ JsonString["a"], JsonNumber[1], JsonBoolean[true]]]
 * </pre></blockquote>
 * @author Nerjal Nosk
 * @since JDK 11
 */
public abstract class JsonParser {

    /**
     * Allows to get a String version of the given JsonElement
     * (automatically calls for the value's toString method if primitive)
     * @param json the JsonElement to parse to String
     * @return String: the string version of the JsonElement
     */
    public static String stringify(JsonElement json) throws RecursiveJsonElementException {
        return stringify(json, 0, 2, ' ');
    }

    /**
     * Allows to get a String version of the given JsonElement
     * (automatically calls for the value's toString method if primitive)
     * @param json the JsonElement to parse to String
     * @param space the indentation level (for recursive parsing, uses 0 by default)
     * @return String: the string version of the JsonElement
     */
    public static String stringify(JsonElement json, int space) throws RecursiveJsonElementException {
        return stringify(json, space, 2, ' ');
    }

    /**
     * Allows to get a String version of the given JsonElement
     * (automatically calls for the given object's toString method if primitive)
     * @param json the JsonElement to parse to String
     * @param space the indentation level (for recursive parsing, uses 0 by default)
     * @param tabulation the number of tabChar added in the beginning of each line by indentation level
     * @param tabChar the character ti use for indentations
     * @return String: the string version of the JsonElement
     */
    public static String stringify(JsonElement json, int space, int tabulation, char tabChar)
            throws RecursiveJsonElementException {
        String tab = String.format("%c",tabChar).repeat(tabulation);
        String indentation = tab.repeat(space);
        return json.stringify(indentation,tab);
    }

    /**
     * Parses a string to a JsonElement. The JsonElement
     * type will depend on the String content.<br>
     * Warning: all {@code null} value will be considered
     * as a null {@link com.nerjal.json.elements.JsonString}
     * @param s String: the String to parse
     * @return the parsed JsonElement. Can be an instance of
     *         any class implementing JsonElement
     * @throws JsonParseException if the given string cannot
     *         be parsed (missing quotes, braces, commas...)
     */
    public static JsonElement jsonify(String s) throws JsonParseException {
        StringParser parser = new StringParser(s);
        return parser.parse();
    }
}
