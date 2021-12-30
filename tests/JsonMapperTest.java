import com.nerjal.json.JsonParser;
import com.nerjal.json.elements.JsonBoolean;
import com.nerjal.json.elements.JsonElement;
import com.nerjal.json.elements.JsonNumber;
import com.nerjal.json.elements.JsonString;
import com.nerjal.json.mapper.errors.JsonCastingError;
import com.nerjal.json.mapper.JsonMapper;
import com.nerjal.json.mapper.annotations.JsonIgnore;
import com.nerjal.json.mapper.annotations.JsonNode;
import com.nerjal.json.mapper.annotations.JsonRequired;
import com.nerjal.json.mapper.errors.JsonMapperFieldRequiredError;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public void floatValue() throws Exception {
        JsonElement element = new JsonNumber(Float.MAX_VALUE);
        assertEquals(Float.MAX_VALUE, JsonMapper.map(element, Float.class), .1);
    }

    @Test
    public void floatInvalidValue() {
        assertThrows(JsonCastingError.class, () -> {
            JsonElement element = new JsonString("7");
            JsonMapper.map(element, float.class);
        });
    }

    @Test
    public void doubleValue() throws Exception {
        JsonElement element = new JsonNumber(Double.MAX_VALUE);
        assertEquals(Double.MAX_VALUE, JsonMapper.map(element, Double.class), .1);
    }

    @Test
    public void doubleInvalidValue() {
        assertThrows(JsonCastingError.class, () -> {
            JsonElement element = new JsonString("7");
            JsonMapper.map(element, double.class);
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
        assertArrayEquals(new int[] {1, 2, 3}, JsonMapper.map(element, int[].class));
    }

    @Test
    public void intInvalidArray() {
        assertThrows(JsonCastingError.class, () -> JsonMapper.map(JsonParser.parseString("[0, true, false]"), int[].class));
    }

    @Test
    public void booleanArray() throws Exception {
        JsonElement element = JsonParser.parseString("[true, false, true]");
        assertArrayEquals(new Boolean[] {true, false, true}, JsonMapper.map(element, Boolean[].class));
    }

    @Test
    public void booleanInvalidArray() {
        assertThrows(JsonCastingError.class, () -> JsonMapper.map(JsonParser.parseString("[true, false, 'a']"), Boolean[].class));
    }


    @Test
    public void int2dArray() throws Exception {
        JsonElement element = JsonParser.parseString("[[1, 2], [2, 5], [-3]]");
        assertArrayEquals(new int[][] {{1, 2}, {2, 5}, {-3}}, JsonMapper.map(element, int[][].class));
    }

    @Test
    public void int2dInvalidArray() {
        assertThrows(JsonCastingError.class, () -> JsonMapper.map(JsonParser.parseString("[[1, 2], [2, 5], -3]"), int[][].class));
    }

    public static class TestClass1 {
        Integer a;
        String b;
        boolean c;
    }
    @Test
    public void class1() throws Exception {
        JsonElement element = JsonParser.parseString("{a: 1, \"b\": 'string', c: true,}");
        var res = JsonMapper.map(element, TestClass1.class);
        assertEquals(Integer.valueOf(1), res.a);
        assertEquals("string", res.b);
        assertTrue(res.c);
    }
    @Test
    public void class1Invalid1() {
        assertThrows(JsonCastingError.class, () -> {
            JsonElement element = JsonParser.parseString("{a: 1, \"b\": 6.9, c: 'true',}");
            JsonMapper.map(element, TestClass1.class);
        });
    }
    @Test
    public void class1Invalid2() {
        assertThrows(JsonCastingError.class, () -> {
            JsonElement element = JsonParser.parseString("[3, 5]");
            JsonMapper.map(element, TestClass1.class);
        });
    }

    public static class TestClass2 {
        List<TestClass1> list;
        @JsonRequired
        int req;
    }
    @Test
    public void class2Empty() throws Exception {
        JsonElement element = JsonParser.parseString("{req: 1, list: []}");
        var res = JsonMapper.map(element, TestClass2.class);
        assertEquals(1, res.req);
        assertEquals(0, res.list.size());
    }
    @Test
    public void class2() throws Exception {
        JsonElement element = JsonParser.parseString("{req: 1, list: [{a: 1}, {c: true, b: 'test',},],}");
        var res = JsonMapper.map(element, TestClass2.class);
        assertEquals(1, res.req);
        assertEquals(2, res.list.size());
        assertEquals(Integer.valueOf(1), res.list.get(0).a);
        assertNull(res.list.get(0).b);
        assertFalse(res.list.get(0).c);
        assertNull(res.list.get(1).a);
        assertEquals("test", res.list.get(1).b);
        assertTrue(res.list.get(1).c);
    }
    @Test
    public void class2MissingReq() {
        assertThrows(JsonMapperFieldRequiredError.class, () -> {
            JsonElement element = JsonParser.parseString("{list: []}");
            JsonMapper.map(element, TestClass2.class);
        });
    }
    @Test
    public void class2Invalid() {
        assertThrows(JsonCastingError.class, () -> {
            JsonElement element = JsonParser.parseString("{req: -4, list: true}");
            JsonMapper.map(element, TestClass2.class);
        });
    }

    public static class TestClass3 {
        @JsonNode("numbers")
        HashMap<String, Double> map;

        @JsonNode(value = "some float", required = true)
        float number;

        @JsonIgnore
        int constant = 7;
    }
    @Test
    public void class3() throws Exception {
        JsonElement element = JsonParser.parseString("{'some float': 1e-4, numbers: {a: 1.9, b: +7}, constant: -5}");
        var res = JsonMapper.map(element, TestClass3.class);
        assertEquals(1e-4, res.number, .1);
        assertEquals(7, res.constant);
        assertTrue(res.map.containsKey("a"));
        assertEquals(1.9, res.map.get("a"), .1);
        assertTrue(res.map.containsKey("b"));
        assertEquals(7, res.map.get("b"), 0);
    }
    @Test
    public void class3MissingReq() {
        assertThrows(JsonMapperFieldRequiredError.class, () -> {
            JsonElement element = JsonParser.parseString("{}");
            JsonMapper.map(element, TestClass3.class);
        });
    }
    @Test
    public void class3Invalid() {
        assertThrows(JsonCastingError.class, () -> {
            JsonElement element = JsonParser.parseString("{'some float': 2., numbers: [.1, 3e7]}");
            JsonMapper.map(element, TestClass3.class);
        });
    }

    public static class TestClass4 {
        Map<Boolean, String> map;
    }
    @Test
    public void class4Invalid() {
        assertThrows(JsonCastingError.class, () -> {
            JsonElement element = JsonParser.parseString("{map: {\"true\": 'value'}}");
            JsonMapper.map(element, TestClass4.class);
        });
    }


    @Test
    public void notAllowedCollection() {
        assertThrows(JsonCastingError.class, () -> {
            JsonElement element = JsonParser.parseString("[1, -5, 80]");
            JsonMapper.map(element, List.class);
        });
    }

    @Test
    public void notAllowedMap() {
        assertThrows(JsonCastingError.class, () -> {
            JsonElement element = JsonParser.parseString("{a: 1, b: -5}");
            JsonMapper.map(element, HashMap.class);
        });
    }
}
