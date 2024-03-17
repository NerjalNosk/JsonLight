package io.github.nerjalnosk.jsonlight.elements;

import io.github.nerjalnosk.jsonlight.JsonError;
import io.github.nerjalnosk.jsonlight.parser.options.ArrayParseOptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class JsonArrayTest {
    static Logger LOGGER;

    JsonArray array;
    Random random = new Random();

    @BeforeAll
    static void init() {
        LOGGER = LoggerFactory.getLogger(JsonArrayTest.class);
    }

    @BeforeEach
    void setUp() {
        array = new JsonArray();
    }

    @Test
    void size() {
        assertEquals(0, array.size());
        array.add(new JsonArray());
        assertEquals(1, array.size());
    }

    @Test
    void get$int() {
        JsonArray jsonArray = new JsonArray();
        array.add(jsonArray);
        array.add(array);

        assertEquals(jsonArray, array.get(0));
        assertEquals(array, array.get(1));
    }

    @Test
    void add$JsonElement$int() {
        JsonArray jsonArray = new JsonArray();
        array.add(array);
        array.add(0, jsonArray);

        assertEquals(jsonArray, array.get(0));
        assertEquals(array, array.get(1));
    }

    @Test
    void getNumber$int() throws JsonError.JsonElementTypeException {
        JsonNumber number = new JsonNumber(1);
        array.add(number);
        array.add(new JsonArray());

        assertEquals(number, array.get(0));
        assertEquals(1, array.getNumber(0).intValue());
        assertThrows(JsonError.JsonElementTypeException.class, () -> array.getNumber(1));
    }

    @Test
    void getString$int() throws JsonError.JsonElementTypeException {
        JsonString string = new JsonString("test");
        array.add(string);
        array.add(new JsonArray());

        assertEquals(string, array.get(0));
        assertEquals("test", array.getString(0));
        assertThrows(JsonError.JsonElementTypeException.class, () -> array.getString(1));
    }

    @Test
    void getBoolean$int() throws JsonError.JsonElementTypeException {
        JsonBoolean bool = new JsonBoolean(false);
        array.add(bool);
        array.add(new JsonArray());

        assertEquals(bool, array.get(0));
        assertFalse(array.getBoolean(0));
        assertThrows(JsonError.JsonElementTypeException.class, () -> array.getBoolean(1));
    }

    @Test
    void getArray$int() throws JsonError.JsonElementTypeException {
        JsonArray arr = new JsonArray();
        array.add(arr);
        array.add(new JsonNumber());

        assertEquals(arr, array.get(0));
        assertEquals(arr, array.getArray(0));
        assertThrows(JsonError.JsonElementTypeException.class, () -> array.getArray(1));
    }

    @Test
    void getObject$int() throws JsonError.JsonElementTypeException {
        JsonObject object = new JsonObject();
        array.add(object);
        array.add(new JsonArray());

        assertEquals(object, array.get(0));
        assertEquals(object, array.getObject(0));
        assertThrows(JsonError.JsonElementTypeException.class, () -> array.getObject(1));
    }

    @Test
    void getAll$int$int() {
        int m = random.nextInt(32);
        for (int i = 0; i < m; i++) {
            array.add(new JsonNumber(Math.random()));
        }

        JsonElement[] elements = array.getAll(0, m);
        assertEquals(m, elements.length);
        for (int i = 0; i < m; i++) {
            assertEquals("Number", elements[i].typeToString());
        }
    }

    @Test
    void getAllComments() {
        int m = random.nextInt(32);
        int count = 0;
        for (int i = 0; i < m; i++) {
            if (random.nextBoolean()) {
                int n = random.nextInt(20);
                char[] c = new char[n];
                for (int j = 0; j < n; j++) {
                    c[j] = (char) (random.nextInt(26)+64);
                }
                array.add(new JsonComment(String.valueOf(c)));
                count ++;
            } else {
                array.add(new JsonNumber(Math.random()));
            }
        }

        JsonComment[] comments = array.getAllComments();
        assertEquals(count, comments.length);
        for (int i = 0; i < count; i++) {
            assertEquals("Comment", comments[i].typeToString());
        }
    }

    @Test
    void remove$JsonElement() {
        JsonNumber number1 = new JsonNumber();
        JsonNumber number2 = new JsonNumber(1);

        array.add(number1);

        assertFalse(array.remove(number2));
        assertTrue(array.remove(number1));
        assertFalse(array.remove(number1));

        array.add(number2);

        assertTrue(array.remove(number2));
    }

    @Test
    void remove$int() {
        JsonNumber number = new JsonNumber();

        assertThrows(IndexOutOfBoundsException.class, () -> array.remove(0));

        array.add(number);

        assertEquals(number, array.remove(0));
    }

    @Test
    void removeAll$Collection$JsonElement() {
        Collection<JsonElement> collection = new ArrayList<>();
        int m = random.nextInt(32);
        for (int i = 0; i < m; i++) {
            JsonNumber number = new JsonNumber(random.nextInt(20));
            if (random.nextBoolean()) {
                collection.add(number);
            }
            array.add(number);
        }

        assertTrue(collection.containsAll(array.removeAll(collection)));
        assertEquals(array.size() + collection.size(), m);
    }

    @Test
    void addAll$iterable() throws JsonError.JsonElementTypeException {
        JsonArray arr = new JsonArray();
        arr.add(new JsonNumber(0));
        arr.add(new JsonNumber(0));

        array.addAll(arr);

        assertEquals(2, array.size());
        assertEquals(0, array.get(0).getAsInt());

        List<JsonElement> list = new ArrayList<>();
        array.addAll(list);

        assertEquals(2, array.size());
    }

    @Test
    void addAll$array() {
        JsonElement[] arr = new JsonElement[5];
        array.addAll(arr);

        assertEquals(0, array.size());

        arr[1] = new JsonNumber(0);
        array.addAll(arr);

        assertEquals(1, array.size());
        assertEquals(arr[1], array.get(0));
    }

    @Test
    void stringify$singleLine() throws JsonError.RecursiveJsonElementException {
        for (int i = 0; i < 5; ++i) {
            JsonNumber n = new JsonNumber(i);
            array.add(n);
        }
        ArrayParseOptions options = new ArrayParseOptions(ArrayParseOptions.ArrayFormat.INLINE, 0, false, true);
        assertEquals("[0, 1, 2, 3, 4]", array.stringify());
    }
}