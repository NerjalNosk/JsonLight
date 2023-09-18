package io.github.nerjalnosk.jsonlight.mapper.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Flag a field not to raise any casting/value exception occurring during Json mapping.
 * This annotation doesn't flag a field as to be a mapping target.
 * <p>
 * Can also be set using {@link JsonNode}
 * @author nerjal
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface JsonIgnoreExceptions {
}
