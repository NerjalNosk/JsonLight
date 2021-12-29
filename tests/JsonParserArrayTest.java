import com.nerjal.json.JsonError;
import com.nerjal.json.JsonParser;
import com.nerjal.json.elements.JsonElement;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class JsonParserArrayTest {
    @Test
    public void emptyArray() throws JsonError.JsonParseException, JsonError.JsonElementTypeException {
        JsonElement jsonElement = JsonParser.parseString("[]");
        assertTrue(jsonElement.isJsonArray());
        assertEquals(0, jsonElement.getAsJsonArray().size());
    }

    @Test
    public void integerArray() throws JsonError.JsonParseException, JsonError.JsonElementTypeException {
        JsonElement jsonElement = JsonParser.parseString("[1, 2, -5]");
        assertTrue(jsonElement.isJsonArray());
        assertEquals(3, jsonElement.getAsJsonArray().size());
        var res = new ArrayList<Integer>();
        for (JsonElement element : jsonElement.getAsJsonArray()) {
            assertTrue(element.isNumber());
            res.add(element.getAsInt());
        }
        System.out.println(res);
        System.out.println(jsonElement.getAsJsonArray().size());
        assertArrayEquals(new Integer[] {1, 2, -5}, res.toArray(new Integer[0]));
    }

    @Test
    public void stringArray() throws JsonError.JsonParseException, JsonError.JsonElementTypeException {
        JsonElement jsonElement = JsonParser.parseString("[\"a\", 'b', \"'c'\", '\"\"d']");
        assertTrue(jsonElement.isJsonArray());
        assertEquals(4, jsonElement.getAsJsonArray().size());
        var res = new ArrayList<String>();
        for (JsonElement element : jsonElement.getAsJsonArray()) {
            assertTrue(element.isString());
            res.add(element.getAsString());
        }
        assertArrayEquals(new String[] {"a", "b", "'c'", "\"\"d"}, res.toArray(new String[0]));
    }

    @Test
    public void emptyObjectArray() throws JsonError.JsonParseException, JsonError.JsonElementTypeException {
        JsonElement jsonElement = JsonParser.parseString("[{}, {}, {}]");
        assertTrue(jsonElement.isJsonArray());
        assertEquals(3, jsonElement.getAsJsonArray().size());
        for (JsonElement element : jsonElement.getAsJsonArray()) {
            assertTrue(element.isJsonObject());
            assertTrue(element.getAsJsonObject().isEmpty());
        }
    }

    @Test
    public void emptyArrayArray() throws JsonError.JsonParseException, JsonError.JsonElementTypeException {
        JsonElement jsonElement = JsonParser.parseString("[[], [], []]");
        assertTrue(jsonElement.isJsonArray());
        assertEquals(3, jsonElement.getAsJsonArray().size());
        for (JsonElement element : jsonElement.getAsJsonArray()) {
            assertTrue(element.isJsonArray());
            assertEquals(0, element.getAsJsonArray().size());
        }
    }

    @Test
    public void twoDArray() throws JsonError.JsonParseException, JsonError.JsonElementTypeException {
        JsonElement jsonElement = JsonParser.parseString("[[1, 2], [3, 4], [5, 6]]");
        assertTrue(jsonElement.isJsonArray());
        assertEquals(3, jsonElement.getAsJsonArray().size());
        for (JsonElement element : jsonElement.getAsJsonArray()) {
            assertTrue(element.isJsonArray());
            assertEquals(2, element.getAsJsonArray().size());
        }
    }

    @Test
    public void mixedArray() throws JsonError.JsonParseException, JsonError.JsonElementTypeException {
        JsonElement jsonElement = JsonParser.parseString("[1, '2', [], {},]");
        assertTrue(jsonElement.isJsonArray());
        assertEquals(4, jsonElement.getAsJsonArray().size());
    }
}
