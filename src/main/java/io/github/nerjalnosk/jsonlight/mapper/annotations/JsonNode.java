package io.github.nerjalnosk.jsonlight.mapper.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Flag a field be referenced by another name in the JSON.
 * Optionally specify if mapper should halt if field not present (see {@link JsonRequired})
 * @author CodedSakura
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface JsonNode {
    /**
     * The Json object key target
     */
    String value();

    /**
     * Whether an exception should be raised if there is no node with the specified value upon mapping an object
     */
    boolean required() default false;

    /**
     * Whether cast/value exceptions occurring during mapping should be ignored
     */
    boolean ignoreExceptions() default false;
}
