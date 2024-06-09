package io.github.nerjalnosk.jsonlight.mapper.annotations;

import java.lang.annotation.*;

/**
 * Flag a field not to raise any casting/value exception occurring during Json mapping.
 * This annotation doesn't flag a field as to be a mapping target.
 * <p>
 * Can also be set using {@link JsonNode}
 * @author Nerjal Nosk
 */
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface JsonIgnoreExceptions {
    /**
     * Flags a field not to raise any casting exception occurring during Json mapping.
     * These are thrown when the Json element's type is not compatible with the field's.
     * (e.g. an array for a map field)
     */
    @Documented
    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface IgnoreCastingError {}

    /**
     * Flags a field not to raise any ChildNotFound exception occurring during Json mapping.
     * These are thrown when trying to map a field to a non-existing Json object's node.
     * These issues can also be prevented by specifying a default provider, using
     * {@link JsonDefaultProvider}
     */
    @Documented
    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface IgnoreNoChildException {}

    /**
     * Flags a field not to raise any IllegalAccess exception occurring during Json mapping.
     * These are thrown when the mapper cannot modify the field's value.
     * In the case of final fields, which will raise this issue, they should rather be flagged
     * with {@link JsonIgnore} and instantiated in the default or specified constructor by
     * annotating the class with {@link JsonInstanceProvider}
     */
    @Documented
    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface IgnoreNoAccess {}
}
