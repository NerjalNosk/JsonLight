package io.github.nerjalnosk.jsonlight.parser;

import io.github.nerjalnosk.jsonlight.JsonError;

/**
 * {@link StringParser} parsing options.
 * Defines how a source is to be parsed,
 * and more specifically what features should
 * be enabled or disabled.
 * <p>
 * By default, accepts Json5 but custom
 * JsonLight features are disabled.
 * <p>
 * Use the {@link Builder} to instantiate.
 */
public class ParserOptions {
    /**
     * Whether to use the Json5 or Json4 syntax.
     * This includes but is not limited to:
     * <ul>
     *     <li>Unquoted object keys</li>
     *     <li>Single-quoted strings</li>
     *     <li>Trailing commas</li>
     *     <li>Comments in general</li>
     * </ul>
     * See the <a href="https://json5.org">Json5</a>
     * documentation for more info.
     */
    public final boolean json5;
    /**
     * Whether to use circular structure
     * parsing by ID declaration/referencing.
     * May arise {@link JsonError.NoSuchIdCircularJsonException}
     * if the source is not properly formatted.
     */
    public final boolean circular;
    /**
     * Whether to dynamically close open
     * container and string elements if
     * reaching the source end during parsing.
     */
    public final boolean autoClose;
    /**
     * Whether to allow parsing line break as
     * possible iteration separators in commas'
     * stead for iterable elements (arrays
     * and objects)
     */
    public final boolean lineIter;
    /**
     * Whether to accept root comment outside a container
     * element and pass them on the recognised root
     * comment.
     */
    public final boolean rootComment;
    /**
     * Whether to allow parsing unicode escaped
     * codes as a single character.
     * <p>
     * E.g. {@code \\u2722} -> `{@code \u2722}`
     * <p>
     * Warning: unless specified differently,
     * parsed strings will remember the escaped
     * character as to be parsed back as an
     * escaped code, and will apply it to all
     * instances of this character.
     */
    public final boolean parseUnicode;

    private ParserOptions(boolean json5, boolean circular, boolean autoClose, boolean lineIter, boolean rootComment, boolean parseUnicode) {
        this.json5 = json5;
        this.circular = circular;
        this.autoClose = autoClose;
        this.lineIter = lineIter;
        this.rootComment = rootComment;
        this.parseUnicode = parseUnicode;
    }

    /**
     * {@link ParserOptions} builder class
     */
    public static class Builder {
        boolean j5;
        boolean circ;
        boolean close;
        boolean line;
        boolean rootC;
        boolean uni;

        /**
         * Default builder instance, for generic
         * Json5 support, but additional
         * functionalities disabled.
         */
        public Builder() {
            this.j5 = true;
            this.rootC = true;
        }

        /**
         * Builds the configured parser options.
         * @return the configured parser options.
         */
        ParserOptions build() {
            return new ParserOptions(this.j5, this.circ, this.close, this.line, this.rootC, uni);
        }

        /**
         * Disabled all custom JsonLight json
         * extension, such as circular structures,
         * automatic closing at the end of source,
         * and line break iterable split.
         * @return this
         */
        Builder classic() {
            this.circ = false;
            this.close = false;
            this.line = false;
            return this;
        }

        /**
         * Enables all custom JsonLight json
         * extension, such as circular structures,
         * automatic closing at the end of source,
         * and line break iterable split.
         * @return this
         */
        Builder extended() {
            this.circ = true;
            this.close = true;
            this.line = true;
            return this;
        }

        /**
         * Sets the options to be built to
         * support the Json5 syntax.
         * @return this
         * @see ParserOptions#json5
         */
        Builder json5() {
            this.j5 = true;
            return this;
        }

        /**
         * Sets the options to be built to
         * not support the json5 syntax,
         * but only the json4.
         * @return this
         * @see ParserOptions#json5
         */
        Builder json4() {
            this.j5 = false;
            return this;
        }

        /**
         * Sets the options to be built to
         * support root comments outside
         * the root element.
         * Might come raise issues if the
         * root element cannot store comments.
         * @return this
         * @see ParserOptions#rootComment
         */
        Builder rootComment() {
            this.rootC = true;
            return this;
        }

        /**
         * Sets toe options to be built not
         * to support root comments outside
         * the root element.
         * @return this
         * @see ParserOptions#rootComment
         */
        Builder noRootComment() {
            this.rootC = false;
            return this;
        }

        /**
         * Sets the options to be built to
         * support circular structures.
         * @return this
         * @see ParserOptions#circular
         */
        Builder circular() {
            this.circ = true;
            return this;
        }

        /**
         * Sets the options to be built not
         * to support circular structures.
         * @return this
         * @see ParserOptions#circular
         */
        Builder noCircular() {
            this.circ = false;
            return this;
        }

        /**
         * Sets the options to be built to
         * support auto closing at source
         * end.
         * @return this
         * @see ParserOptions#autoClose
         */
        Builder autoClose() {
            this.close = true;
            return this;
        }

        /**
         * Sets the options to be built not
         * to support auto closing at
         * source end.
         * @return this
         * @see ParserOptions#autoClose
         */
        Builder noAutoClose() {
            this.close = false;
            return this;
        }

        /**
         * Sets the options to be built to
         * support using line breaks as
         * iteration splitters if no comma
         * is found.
         * @return this
         * @see ParserOptions#lineIter
         */
        Builder lineIter() {
            this.line = true;
            return this;
        }

        /**
         * Sets the options to be built not
         * to support using line breaks as
         * iteration splitters.
         * @return this
         * @see ParserOptions#lineIter
         */
        Builder noLineIter() {
            this.line = false;
            return this;
        }

        /**
         * Sets the options to be built to
         * support auto parsing escaped
         * unicode codes as unicode
         * characters.
         * @return this
         * @see ParserOptions#parseUnicode
         */
        Builder unicodeParse() {
            this.uni = true;
            return this;
        }

        /**
         * Sets the options to be built not
         * to support auto parsing
         * escaped unicode codes as
         * unicode characters.
         * @return this
         * @see ParserOptions#parseUnicode
         */
        Builder noUnicodeParse() {
            this.uni = false;
            return this;
        }
    }
}
