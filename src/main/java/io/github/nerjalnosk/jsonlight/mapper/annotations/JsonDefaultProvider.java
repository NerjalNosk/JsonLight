package io.github.nerjalnosk.jsonlight.mapper.annotations;

import java.lang.annotation.*;

@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface JsonDefaultProvider {
    /**
     * The name of the default provider method
     */
    String value();

    /**
     * Leave as default to target the current class
     */
    Class<?> clazz() default JsonDefaultProvider.class;

    /**
     * Whether to initiate the field with the specified provider
     * only after all non-defaulted fields are initialized.
     * E.g. if you want your provider to depend on your other
     * fields' values.
     */
    boolean postInit() default false;

    /**
     * Allows to sort out late-init fields when using
     * {@link #postInit()}
     */
    int priority() default 1;
}
