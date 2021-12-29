import com.nerjal.json.JsonParser;
import com.nerjal.json.elements.JsonBoolean;
import com.nerjal.json.elements.JsonElement;
import com.nerjal.json.elements.JsonNumber;
import com.nerjal.json.elements.JsonString;
import com.nerjal.json.mapper.JsonCastingError;
import com.nerjal.json.mapper.JsonMapper;
import org.junit.Test;

import static org.junit.Assert.*;

public class JsonMapperTest {

    @Test
    public void intValue() throws Exception {
        JsonElement element = new JsonNumber(Integer.MIN_VALUE);
        assertEquals(Integer.MIN_VALUE, JsonMapper.map(element, int.class).intValue());
    }

    @Test
    public void intInvalidValue() {
        assertThrows(JsonCastingError.class, () -> {
            JsonElement element = new JsonString("7");
            JsonMapper.map(element, Integer.class);
        });
    }

    @Test
    public void longValue() throws Exception {
        JsonElement element = new JsonNumber(Long.MAX_VALUE);
        assertEquals(Long.MAX_VALUE, JsonMapper.map(element, Long.class).longValue());
    }

    @Test
    public void longInvalidValue() {
        assertThrows(JsonCastingError.class, () -> {
            JsonElement element = new JsonString("7");
            JsonMapper.map(element, long.class);
        });
    }

    @Test
    public void booleanValue() throws Exception {
        JsonElement element = new JsonBoolean(true);
        assertEquals(true, JsonMapper.map(element, Boolean.class));
    }

    @Test
    public void booleanInvalidValue() {
        assertThrows(JsonCastingError.class, () -> {
            JsonElement element = new JsonString("7");
            JsonMapper.map(element, boolean.class);
        });
    }


    @Test
    public void intArray() throws Exception {
        JsonElement element = JsonParser.parseString("[1, 2, 3]");
        assertTrue(element.isJsonArray());
        assertArrayEquals(new int[] {1, 2, 3}, JsonMapper.map(element, int[].class));
    }
}
