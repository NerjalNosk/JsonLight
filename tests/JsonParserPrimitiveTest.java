import com.nerjal.json.JsonError;
import com.nerjal.json.JsonParser;
import com.nerjal.json.elements.JsonElement;
import org.junit.Test;

import static org.junit.Assert.*;

public class JsonParserPrimitiveTest {

    @Test
    public void integer() throws JsonError.JsonParseException, JsonError.JsonElementTypeException {
        JsonElement jsonElement = JsonParser.parseString("12");
        assertTrue(jsonElement.isNumber());
        assertEquals(12, jsonElement.getAsInt());
    }

    @Test
    public void hexInteger() throws JsonError.JsonParseException, JsonError.JsonElementTypeException {
        JsonElement jsonElement = JsonParser.parseString("0x12");
        assertTrue(jsonElement.isNumber());
        assertEquals(0x12, jsonElement.getAsInt());
    }

    @Test
    public void binaryInteger() throws JsonError.JsonParseException, JsonError.JsonElementTypeException {
        JsonElement jsonElement = JsonParser.parseString("0b1010");
        assertTrue(jsonElement.isNumber());
        assertEquals(0b1010, jsonElement.getAsInt());
    }

    @Test
    public void invalidInteger() {
        assertThrows(JsonError.JsonParseException.class, () -> {
            JsonParser.parseString("1a2");
        });
    }

    @Test
    public void floatingPoint() throws JsonError.JsonParseException, JsonError.JsonElementTypeException {
        JsonElement jsonElement = JsonParser.parseString("1.23");
        assertTrue(jsonElement.isNumber());
        assertEquals(1.23, jsonElement.getAsFloat(), 0);
    }
}
