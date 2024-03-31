package io.github.nerjalnosk.jsonlight.parser.options;

import io.github.nerjalnosk.jsonlight.elements.JsonComment;

/**
 * <p>Stringification options for
 * {@link JsonComment} elements.
 * </p>
 * <p>Allows to set whether should
 * the comment be a block comment,
 * as well as whether should new
 * lines be indented, and/or if
 * they should be preceded by an
 * asterisk, for block comments.
 * </p>
 * <p>Hence only two types of
 * stringification options are
 * available: inline comments,
 * which don't require any
 * further elements, and block
 * comments, which uses the two
 * said properties.</p>
 * @author Nerjal Nosk
 */
public class CommentParseOptions extends AbstractParseOptions<JsonComment> {
    private boolean indent;
    private boolean newlineAsterisk;
    private final CommentType type;

    /**
     * Instantiates new comment
     * stringification options for the
     * specified type, usage of indent and
     * asterisks on new lines.
     * @param indent whether stringification
     *               of a block comment should
     *               indent new lines to the
     *               same level as
     *               stringification
     * @param newlineAsterisk whether
     *                        stringification
     *                        of a block
     *                        comment should
     *                        add an asterisk
     *                        at the
     *                        beginning of all
     *                        new line
     * @param type the type of comment these
     *             options should stringify
     */
    public CommentParseOptions(boolean indent, boolean newlineAsterisk, CommentType type) {
        this.indent = indent;
        this.newlineAsterisk = newlineAsterisk;
        this.type = type;
        ping();
    }

    /**
     * Instantiates new block comment
     * stringification options with the
     * specified usage of indent and
     * asterisks on new lines.
     * @param indent whether stringification
     *               should indent new lines
     *               to the same level as
     *               stringification
     * @param newlineasterisk whether
     *                        stringification
     *                        should add an
     *                        asterisk at the
     *                        beginning of all
     *                        new line
     * @return the newly instantiated options
     */
    public static CommentParseOptions blockCommentParseOptions(boolean indent, boolean newlineasterisk) {
        return new CommentParseOptions(indent, newlineasterisk, CommentType.BLOCK);
    }

    /**
     * Instantiates new in-line comment
     * stringification options.<br>
     * By default, usage of indent and
     * asterisks on new lines for block
     * comments are set to {@code true}.
     */
    public CommentParseOptions() {
        this.indent = true;
        this.newlineAsterisk = true;
        this.type = CommentType.LINE;
    }

    public CommentParseOptions(CommentType type) {
        this.indent = this.newlineAsterisk = type == CommentType.BLOCK;
        this.type = type;
    }

    @Override
    public CommentParseOptions clone() {
        return new CommentParseOptions(this.indent, this.newlineAsterisk, this.type);
    }

    /**
     * Sets the use of indentation for
     * block comments to the specified
     * value.
     * @param b whether a block comment
     *          should use indentation
     *          on new lines
     */
    public void setUseIndent(boolean b) {
        this.indent = b;
        ping();
    }

    /**
     * Sets the use of asterisks on new
     * lines for block comments to the
     * specified value.
     * @param b whether a block comment
     *          should use asterisks on
     *          new lines
     */
    public void setDoesNewlineAsterisk(boolean b) {
        this.newlineAsterisk = b;
        ping();
    }

    /**
     * Returns whether the options
     * are set for block comments to
     * use indentation for new lines.
     * @return whether the options
     *         are set for block
     *         comments to use
     *         indentation for new
     *         lines
     */
    public boolean usesIndent() {
        return this.indent;
    }

    /**
     * Returns whether the options
     * are set for block comments to
     * use asterisks at the beginning
     * of new lines.
     * @return whether the options
     *         are set for block
     *         comments to use
     *         asterisks at the
     *         beginning of new
     *         lines
     */
    public boolean doesNewlineAsterisk() {
        return this.newlineAsterisk;
    }

    /**
     * Returns whether the options
     * are set to stringify a comment
     * as an in-line comment.
     * @return whether the options
     *         are set to stringify
     *         a comment as an
     *         in-line comment
     */
    public boolean isLineComment() {
        return this.type == CommentType.LINE;
    }

    /**
     * Returns whether the options
     * are set to stringify a comment
     * as a block comment.
     * @return whether the options
     *         are set to stringify
     *         a comment as a block
     *         comment
     */
    public boolean isBlockComment() {
        return this.type == CommentType.BLOCK;
    }

    /**
     * Different available
     * stringification types
     * for {@link JsonComment}
     */
    public enum CommentType {
        LINE,
        BLOCK
    }
}
