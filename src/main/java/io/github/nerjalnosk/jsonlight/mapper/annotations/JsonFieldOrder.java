package io.github.nerjalnosk.jsonlight.mapper.annotations;

import java.lang.annotation.*;

/**
 * Defines a field's ordering for mapping and unmapping.
 * <p>
 * Order starts at 0 going up, all negative value are ignored.
 * <p>
 * Hence, a field set with a {@code 0} order will be mapped
 * before a field with a {@code 1} order, which will be
 * mapped before a field with a {@code 2} order, etc.
 * <p>
 * Last but not least, any field without an order will
 * be mapped last.
 * <br>
 * The only exception to that case is with using
 * {@link JsonDefaultProvider#postInit()}, for these will be
 * mapped <i>from Json to Java instance</i> after all other
 * fields, albeit keeping the ordering applied among them.
 * <p>
 * Takes priority over {@link JsonNode#order()}
 */
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface JsonFieldOrder {
    int value();
}
