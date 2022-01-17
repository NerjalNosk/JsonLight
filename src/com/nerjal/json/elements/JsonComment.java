package com.nerjal.json.elements;


import com.nerjal.json.parser.options.CommentParseOptions;

import java.util.Collection;

/**
 * The {@code JsonComment} class represents a JSON comment.<br>
 * Those are structured the same way as Java or JS comments.
 * <p><br>
 * Single line comments start with // and continue to the end
 * of the line.<br>
 * Block comments are opened with the {@code /*} characters
 * sequence, and closed whenever comes the exact opposite
 * sequence.
 * <p><br>
 * Those are contained in container nodes ( {@link JsonObject}
 * and {@link JsonArray} ) but are not included in iterations
 * such as {@code jsonArray.forEach(e -> {})} or
 * <blockquote><pre>
 *     for (JsonElement element : jsonArray) {
 *         ...
 *     }
 * </pre></blockquote>
 * Unless using {@code jsonArray.forAll(e -> {})}
 */
public class JsonComment extends JsonElement {
    private String value;
    private boolean isBlock;
    private final CommentParseOptions parseOptions = new CommentParseOptions();

    public JsonComment(String s, boolean b) {
        this.value = s;
        this.isBlock = b;
    }

    public JsonComment(String s) {
        this(s, false);
    }

    public JsonComment(boolean b) {
        this(null, b);
    }

    public JsonComment() {
        this(null, false);
    }

    public void setValue(String s) {
        this.value = s;
    }

    public void setBlock(boolean block) {
        isBlock = block;
    }

    public boolean isBlock() {
        return isBlock;
    }

    public CommentParseOptions getParseOptions() {
        return parseOptions;
    }

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
    public String stringify(String indentation, String indentIncrement) {
        if (!this.isBlock) return "//"+this.value;
        StringBuilder b = new StringBuilder("/*\n");
        for (String s : this.getSplitValue()) {
            if (this.parseOptions.usesIndent()) b.append(indentation);
            if (this.parseOptions.doesNewlineAsterisk()) b.append("* ");
            b.append(s);
        }
        b.append("*/");
        return b.toString();
    }
}
