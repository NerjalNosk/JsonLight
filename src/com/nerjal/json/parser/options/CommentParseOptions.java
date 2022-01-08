package com.nerjal.json.parser.options;

public class CommentParseOptions {
    private boolean indent;
    private boolean newlineAsterisk;
    private CommentType type;

    protected CommentParseOptions(boolean indent, boolean newlineasterisk, CommentType type) {
        this.indent = indent;
        this.newlineAsterisk = newlineasterisk;
        this.type = type;
    }

    public static CommentParseOptions blockCommentParseOptions(boolean indent, boolean newlineasterisk) {
        return new CommentParseOptions(indent, newlineasterisk, CommentType.BLOCK);
    }

    public CommentParseOptions() {
        this(true, true, CommentType.LINE);
    }

    public void setUseIndent(boolean b) {
        this.indent = b;
    }

    public void setDoesNewlineAsterisk(boolean b) {
        this.newlineAsterisk = b;
    }

    public void setCommentType(CommentType type) {
        this.type = type;
    }

    public boolean usesIndent() {
        return this.indent;
    }

    public boolean doesNewlineAsterisk() {
        return this.newlineAsterisk;
    }

    public boolean isLineComment() {
        return this.type == CommentType.LINE;
    }

    public boolean isBlockComment() {
        return this.type == CommentType.BLOCK;
    }

    public enum CommentType {
        LINE,
        BLOCK
    }
}
