package io.github.nerjalnosk.jsonlight.mapper.annotations;

import java.lang.annotation.*;
import io.github.nerjalnosk.jsonlight.elements.JsonElement;

/**
 * Field annotation to allow specifying a default value initializer
 * rather than being limited to base constructors
 */
@Documented
@Target({ElementType.FIELD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface JsonInstanceProvider {
    /**
     * Leave as default to target the current class
     */
    Class<?> clazz() default JsonInstanceProvider.class;

    /**
     * Leave as default (empty) for a generic constructor
     */
    String builder() default "";

    /**
     * Set to {@code false} to pass on the current {@link JsonElement}
     * rather than proceed to automated mapping.
     * <p>
     * The provider is then expected to accept a {@link JsonElement}
     * as a parameter.
     */
    boolean autoMapping() default true;

    /**
     * Specifies an array of <b>nullable</b> parameters for
     * the specified builder or constructor.
     * <p>
     * If {@link #autoMapping()} is disabled, it is expected for
     * the required {@link JsonElement} to either be at the beginning
     * or the end of the parameters, prioritizing the first
     * if the parameter type array matches.
     */
    Class<?>[] nullableArgs() default {};
}
