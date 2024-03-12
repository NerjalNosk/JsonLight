package io.github.nerjalnosk.jsonlight.mapper;

import io.github.nerjalnosk.jsonlight.JsonError;
import io.github.nerjalnosk.jsonlight.elements.JsonElement;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;

import static org.junit.jupiter.api.Assertions.*;

public class JsonUnmapperTest {
    static Logger LOGGER;

    @BeforeAll
    static void first() {
        LOGGER = LoggerFactory.getLogger(JsonUnmapperTest.class);
    }

    @Test
    void unmapEnum() throws JsonError.JsonElementTypeException {
        TestEnum e = TestEnum.A;
        JsonElement elem = JsonUnmapper.serialize(e);

        assertTrue(elem.isString());
        assertEquals("A", elem.getAsString());
    }

    enum TestEnum {
        A
    }
}
