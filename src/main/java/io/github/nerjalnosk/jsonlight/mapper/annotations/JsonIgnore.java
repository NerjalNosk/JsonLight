package io.github.nerjalnosk.jsonlight.mapper.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Flag a field to be ignored by the mapper
 * @author CodedSakura
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface JsonIgnore {
    boolean fromJson() default true;
    boolean toJson() default true;
}
