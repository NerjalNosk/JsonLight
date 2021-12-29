package com.nerjal.json.mapper.annotations;

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
    String value();
    boolean required() default false;
}
