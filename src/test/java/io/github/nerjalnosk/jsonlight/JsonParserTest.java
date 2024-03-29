package io.github.nerjalnosk.jsonlight;

import io.github.nerjalnosk.jsonlight.elements.*;
import io.github.nerjalnosk.jsonlight.parser.options.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

class JsonParserTest {
    static Logger LOGGER;

    JsonNumber number;
    JsonString str;
    JsonArray array;
    JsonObject object;
    //TODO: JsonNumber
    //TODO: JsonBoolean

    @BeforeAll
    static void first() {
        LOGGER = LoggerFactory.getLogger(JsonParserTest.class);
    }

    @BeforeEach
    void setUp() {
        number = new JsonNumber();
        str = new JsonString("");
        array = new JsonArray();
        object = new JsonObject();
    }

    /**
     * Test {@link JsonParser#stringify(JsonElement)}
     */
    @Test
    @Order(1)
    void stringify() throws JsonError.RecursiveJsonElementException {
        assertEquals("0", JsonParser.stringify(number));
        assertEquals("\"\"", JsonParser.stringify(str));
        assertEquals("[]", JsonParser.stringify(array));
        assertEquals("{}", JsonParser.stringify(object));
    }

    /**
     * Test {@link JsonParser#stringify(JsonElement, ParseSet)}
     */
    @Test
    @Order(2)
    void testStringify() throws JsonError.RecursiveJsonElementException {
        // options
        ParseSet set1 = new ParseSet();
        ParseSet set2 = new ParseSet();
        ParseSet set3 = new ParseSet();
        NumberParseOptions numberOptions = new NumberParseOptions(true, NumberParseOptions.NumberFormat.HEXADECIMAL, 2);
        NumberParseOptions numberOptions2 = new NumberParseOptions(true, NumberParseOptions.NumberFormat.SCIENTIFIC, 2);
        StringParseOptions stringOptions = new StringParseOptions(StringParseOptions.QuoteFormat.SINGLE_QUOTES);
        ArrayParseOptions arrayOptions = new ArrayParseOptions(ArrayParseOptions.ArrayFormat.INLINE);
        ObjectParseOptions objectOptions = new ObjectParseOptions(ObjectParseOptions.ObjectFormat.UNQUOTED_KEYS);

        set1.addOptions(JsonNumber.class, numberOptions);
        set1.addOptions(JsonString.class, stringOptions);
        set2.addOptions(JsonArray.class, arrayOptions);
        set2.addOptions(JsonObject.class, objectOptions);
        set3.addOptions(JsonNumber.class, numberOptions2);

        assertEquals(numberOptions, set1.getOptions(JsonNumber.class));
        assertEquals(numberOptions2, set3.getOptions(JsonNumber.class));
        assertEquals(stringOptions, set1.getOptions(JsonString.class));
        assertEquals(arrayOptions, set2.getOptions(JsonArray.class));
        assertEquals(objectOptions, set2.getOptions(JsonObject.class));
        // number
        number.setValue(65+1/16f);
        //assertEquals("0x3b.1", JsonParser.stringify(number,set1)); // Hex my behated

        number.setValue(6.34e2);
        assertEquals("6.34E2", JsonParser.stringify(number, set3));

        // string
        str.setValue("logcat");
        assertEquals("'logcat'", JsonParser.stringify(str, set1));

        // array
        array.add(new JsonString("test"));
        assertEquals("[ \"test\" ]", JsonParser.stringify(array, set2));

        // object
        object.add("a", new JsonNumber(1));
        assertEquals("{\n  a: 1\n}", JsonParser.stringify(object, set2));
    }

    /**
     * Test {@link JsonParser#stringify(JsonElement, int)}
     */
    @Test
    @Order(3)
    void testStringify1() throws JsonError.RecursiveJsonElementException {
        String s = "logically";
        str.setValue(s);
        assertEquals("\""+s+"\"", JsonParser.stringify(str, 9));
        // array
        array.add(new JsonNumber(1));
        assertEquals("[\n        1\n      ]", JsonParser.stringify(array,3));
        // object
        object.add("a", new JsonBoolean(false));
        assertEquals("{\n    \"a\": false\n  }", JsonParser.stringify(object, 1));
    }

    /**
     * Test {@link JsonParser#stringify(JsonElement, ParseSet, int)}
     */
    @Test
    @Order(4)
    void testStringify2() {
    }

    /**
     * Test {@link JsonParser#stringify(JsonElement, ParseSet, int, int, char)}
     */
    @Test
    @Order(5)
    void testStringify3() {
    }

    /**
     * Test {@link JsonParser#jsonify(String)}
     */
    @Test
    @Order(6)
    void jsonify() throws JsonError.JsonParseException, JsonError.JsonElementTypeException {
        number = JsonParser.jsonify("6e4").getAsJsonNumber();

        assertEquals(6e4, number.getAsInt());
    }

    /**
     * Test {@link JsonParser#parseFile(File)}
     */
    @Test
    @Order(7)
    void parseFile() {
    }

    @Test
    @Order(8)
    void testParseFile() {
    }
}
