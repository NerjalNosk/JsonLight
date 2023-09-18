package io.github.nerjalnosk.jsonlight;

import io.github.nerjalnosk.jsonlight.elements.JsonElement;
import io.github.nerjalnosk.jsonlight.elements.JsonString;
import io.github.nerjalnosk.jsonlight.parser.FileParser;
import io.github.nerjalnosk.jsonlight.parser.StringParser;
import io.github.nerjalnosk.jsonlight.parser.options.ParseSet;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import static io.github.nerjalnosk.jsonlight.JsonError.*;

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
 * @author nerjal
 * @since JDK 16
 * @version 1.1
 */
public abstract class JsonParser {

    /**
     * Allows to get a String version of the given JsonElement
     * (automatically calls for the value's toString method if primitive)
     * @param json the JsonElement to parse to String
     * @return String: the string version of the JsonElement
     */
    public static String stringify(JsonElement json) throws RecursiveJsonElementException {
        return stringify(json, new ParseSet(), 0, 2, ' ');
    }

    /**
     * Allows to get a String version of the given JsonElement
     * (automatically calls for the value's toString method if primitive)
     * @param json the JsonElement to parse to String
     * @return String: the string version of the JsonElement
     */
    public static String stringify(JsonElement json, ParseSet parseSet) throws RecursiveJsonElementException {
        return stringify(json, parseSet, 0, 2, ' ');
    }

    /**
     * Allows to get a String version of the given JsonElement
     * (automatically calls for the value's toString method if primitive)
     * @param json the JsonElement to parse to String
     * @param space the indentation level (for recursive parsing, uses 0 by default)
     * @return String: the string version of the JsonElement
     */
    public static String stringify(JsonElement json, int space) throws RecursiveJsonElementException {
        return stringify(json, new ParseSet(), space, 2, ' ');
    }

    /**
     * Allows to get a String version of the given JsonElement
     * (automatically calls for the value's toString method if primitive)
     * @param json the JsonElement to parse to String
     * @param space the indentation level (for recursive parsing, uses 0 by default)
     * @return String: the string version of the JsonElement
     */
    public static String stringify(JsonElement json, ParseSet parseSet, int space)
            throws RecursiveJsonElementException {
        return stringify(json, parseSet, space, 2, ' ');
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
    public static String stringify(JsonElement json, ParseSet parseSet, int space, int tabulation, char tabChar)
            throws RecursiveJsonElementException {
        String tab = string_repeat(String.format("%c",tabChar),tabulation);
        String indentation = string_repeat(tab,space);
        return json.stringify(parseSet, indentation, tab);
    }

    /**
     * Parses a string to a JsonElement. The JsonElement
     * type will depend on the String content.<br>
     * Warning: all {@code null} value will be considered
     * as a null {@link JsonString}
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

    public static JsonElement parseFile(String s) throws IOException, JsonParseException {
        File f = new File(s);
        return parseFile(f);
    }

    public static JsonElement parseFile(File f) throws IOException, JsonParseException {
        FileParser parser = new FileParser(f);
        return parser.parse();
    }

    /**
     * JDK 11+ String#repeat(int):String backport
     */
    private static String string_repeat(String s, int i) {
        if (i < 0) throw new IllegalArgumentException("count is negative "+i);
        if (i == 1) return s;
        int len = s.length();
        if (len != 0 && i != 0) {
            if (len == 1) {
                byte[] single = new byte[i];
                Arrays.fill(single, s.getBytes()[0]);
                return new String(single);
            } else if (2147483647 / i < len) {
                throw new OutOfMemoryError("Repeating " + len + " bytes String " + i + " times will produce a String exceeding maximum size.");
            } else {
                int limit = len * i;
                byte[] multiple = new byte[limit];
                System.arraycopy(s.getBytes(), 0, multiple, 0, len);

                int copied;
                for(copied = len; copied < limit - copied; copied <<= 1) {
                    System.arraycopy(multiple, 0, multiple, copied, copied);
                }

                System.arraycopy(multiple, 0, multiple, copied, limit - copied);
                return new String(multiple);
            }
        } else {
            return "";
        }
    }
}
