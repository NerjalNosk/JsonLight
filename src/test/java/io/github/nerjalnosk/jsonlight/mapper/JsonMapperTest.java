package io.github.nerjalnosk.jsonlight.mapper;

import io.github.nerjalnosk.jsonlight.elements.JsonObject;
import io.github.nerjalnosk.jsonlight.elements.JsonString;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;

import static org.junit.jupiter.api.Assertions.*;

class JsonMapperTest {
    static Logger LOGGER;

    @BeforeAll
    static void first() {
        LOGGER = LoggerFactory.getLogger(JsonMapperTest.class);
    }

    @Test
    void mapEnum() throws Exception {
        JsonObject o = new JsonObject();
        o.add("f1", new JsonString("A"));
        C1 c1 = JsonMapper.map(o, C1.class);

        assertEquals(c1.f1, TestEnum.A);
    }

    enum TestEnum {
        A
    }

    static class C1 {
        TestEnum f1;
    }
}
