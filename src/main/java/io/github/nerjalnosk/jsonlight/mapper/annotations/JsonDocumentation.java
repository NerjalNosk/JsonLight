package io.github.nerjalnosk.jsonlight.mapper.annotations;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.FIELD})
public @interface JsonDocumentation {
    String[] value();

    /**
     * Determines how the generated comment will be formatted after serialization
     * using the {@link io.github.nerjalnosk.jsonlight.mapper.JsonUnmapper}.
     *
     * <ul>
     *     <li><b>AUTO</b> - <i>default</i> Sets the unmapper to automatically
     *     determine whether your documentation is single-lined or multi-lined.
     *     By default, the value array is split with each inner value on a new line.
     *     Just as well, by default, multi-line documentation will be parsed as a block comment.</li>
     *     <li><b>LINE</b> - Sets the upmapper to register your documentation as single-lined comment.</li>
     *     <li><b>BLOCK</b> - Sets the unmapper to register your documentation as a block comment</li>
     * </ul>
     */
    Format format() default Format.AUTO;

    enum Format {
        AUTO, LINE, BLOCK
    }
}
