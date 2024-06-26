package io.github.nerjalnosk.jsonlight.elements;


import io.github.nerjalnosk.jsonlight.parser.options.CommentParseOptions;
import io.github.nerjalnosk.jsonlight.parser.options.ParseSet;

/**
 * <p>Represents a JSON comment, inside a
 * JSON structure. Thus allowing to keep
 * them inside a JSON file despite editing
 * it.
 * </p>
 * For functional reasons, they are ignored
 * while iterating through composite
 * elements with basic methods. Although
 * some methods, such as
 * {@link JsonArray#forAll}, allow to still
 * get them, and therefore edit them if
 * wished to.
 * @author nerjal
 */
public final class JsonComment extends JsonElement {
    private String value;
    private boolean isBlock;
    private boolean lockBlock = false;
    private transient CommentParseOptions parseOptions = new CommentParseOptions();

    /**
     * Instantiates a new in-line JsonComment
     * with a {@code null} value.
     */
    public JsonComment() {
        this(null, false);
    }

    /**
     * Instantiates a new JsonComment with
     * the specified value.<br>
     * By default, the comment is inline,
     * unless the specified value has more
     * than one line ({@code s.split('\n')})
     * @param s the new comment's value
     */
    public JsonComment(String s) {
        this(s, false);
    }

    /**
     * Instantiates a new JsonComment with
     * a null value.<br>
     * Whether the new comment is block or
     * inline is set by the specified boolean.
     * @param b whether the comment shall be
     *          in-line or not.
     */
    public JsonComment(boolean b) {
        this(null, b);
    }

    /**
     * Sets a new JsonComment with the
     * specified value and specified 'block'
     * property.
     * @param s the new comment's value
     * @param b whether the comment shall
     *          be in-line or not
     * @throws UnsupportedOperationException if
     *         the new comment is specified to
     *         be in-line but the value is
     *         multi-line.
     */
    public JsonComment(String s, boolean b) {
        this.value = s;
        if (s != null && s.split("\n").length > 1)
            this.lockBlock = true;
        if (!b && this.lockBlock)
            throw new UnsupportedOperationException("Multi-line comment cannot be set as non-block");
        this.isBlock = b;
    }

    /**
     * Sets the comment value to the specified
     * string.<br>
     * If the specified string is multi-line,
     * the comment is set to block.
     * @param s the comment's new value
     */
    public void setValue(String s) {
        this.value = s;
        if (s != null && s.split("\n").length > 1) {
            this.isBlock = true;
            this.lockBlock = true;
        } else this.lockBlock = false;
    }

    /**
     * Sets whether the comment shall be blocked
     * or not (in-line).
     * @param block whether the comment shall
     *              be blocked or not
     * @throws UnsupportedOperationException if
     *         the new comment is specified to
     *         be in-line but the value is
     *         multi-line.
     */
    public void setBlock(boolean block) throws UnsupportedOperationException {
        if (!block && this.lockBlock)
            throw new UnsupportedOperationException("Multi-line comment cannot be set as non-block");
        isBlock = block;
    }

    /**
     * Returns whether the comment is a block
     * comment.
     * @return whether the comment is a block
     *         comment
     */
    public boolean isBlock() {
        return isBlock;
    }

    /**
     * Returns the comment's stringification
     * options.
     * @return the comment's stringification
     *         options
     */
    public CommentParseOptions getParseOptions() {
        return parseOptions;
    }

    /**
     * Sets the comment's stringification options
     * @param parseOptions the comment's new
     *                     stringification options
     */
    public void setParseOptions(CommentParseOptions parseOptions) {
        this.parseOptions = parseOptions;
    }

    /**
     * Returns he comment's value, split by
     * line.
     * @return the comment's value, split by
     *         line
     */
    public String[] getSplitValue() {
        return this.value.split("\n");
    }


    @Override
    public boolean isComment() {
        return true;
    }

    @Override
    public String typeToString() {
        return "Comment";
    }

    @Override
    public JsonComment getAsJsonComment() {
        return this;
    }

    @Override
    public String getAsString() {
        return this.value;
    }

    @Override
    protected String stringify(ParseSet parseSet, String indentation, String indentIncrement, ExplorationStack stack) {
        if (parseSet == null) parseSet = new ParseSet();
        CommentParseOptions setOptions = (CommentParseOptions) parseSet.getOptions(this.getClass());
        CommentParseOptions options = parseOptions.isChanged() ? parseOptions :
                setOptions == null ? parseOptions : setOptions;
        if (!this.isBlock) return "// "+this.value;
        StringBuilder b = new StringBuilder("/*");
        for (String s : this.getSplitValue()) {
            if (s.isEmpty()) continue;
            if (options.usesIndent()) b.append('\n').append(indentation);
            if (options.doesNewlineAsterisk()) b.append("* ");
            b.append(s);
        }
        if (options.usesIndent() && options.doesNewlineAsterisk())
            b.append('\n').append(indentation);
        b.append("*/");
        return b.toString();
    }

    @Override
    public JsonComment clone() {
        JsonComment copy = new JsonComment(this.value, this.isBlock);
        if (this.parseOptions.isChanged()) {
            copy.setParseOptions(this.parseOptions.clone());
        }
        return copy;
    }
}
