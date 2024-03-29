package io.github.nerjalnosk.jsonlight.parser;

import io.github.nerjalnosk.jsonlight.JsonError;
import io.github.nerjalnosk.jsonlight.elements.JsonBoolean;
import io.github.nerjalnosk.jsonlight.elements.JsonElement;
import io.github.nerjalnosk.jsonlight.elements.JsonString;

/**
 * Abstract class stating some final
 * overrides for methods which don't
 * need any more from
 * {@link ParserState}, as well as
 * default overrides for a cleaner
 * parsing states code.<br>
 * Also sets links to the parser and
 * its older state.
 * @since JDK 12
 * @author nerjal
 */
public abstract class AbstractState implements ParserState {
    protected final StringParser parser;
    protected final ParserState olderState;
    protected Integer storedId = null;

    protected final void unexpectedCharError(char c) {
        this.error(String.format("unexpected character '%c'", c));
    }

    protected final void disabledError(String s) {
        this.parser.error(new JsonError.DisabledFeatureException("Disabled JSON parsing feature "+s+", enable it or modify your source"));
    }

    /**
     * Instantiates a new
     * {@link StringParser} state.
     * @param stringParser the state's
     *                     affiliated
     *                     parser
     * @param olderState the parser's
     *                   older state
     */
    protected AbstractState(StringParser stringParser, ParserState olderState) {
        this.parser = stringParser;
        this.olderState = olderState;
    }

    @Override
    public boolean isFinal() {
        return false;
    }

    @Override
    public void openObject() {
    }

    @Override
    public void openArray() {
    }

    @Override
    public void openString() {
    }

    @Override
    public void openNum() {
    }

    @Override
    public void openId() {
    }

    protected final void unexpectedIdError() {
        this.error("unexpected ID declaration");
    }

    @Override
    public final void feedId(int id) {
        if (this.storedId != null) {
            this.unexpectedIdError();
        } else {
            this.storedId = id;
        }
    }

    @Override
    public final void openComment() {
        if (!this.parser.options.json5) {
            this.disabledError("comments (json5)");
            return;
        }
        this.parser.forward(1);
        switch (this.parser.getActual()) {
            case '/': {
                this.parser.switchState(new CommentState(this.parser, this, false));
                break;
            }
            case '*': {
                this.parser.switchState(new CommentState(this.parser, this, true));
                break;
            }
            default: this.unexpectedCharError('/');
        }
    }

    @Override
    public final void readBool(char c) {
        switch (c) {
            case 't':
            case 'T':
                if (String.valueOf(this.parser.getNext(3)).equalsIgnoreCase("rue")) {
                    this.parser.forward(3);
                    this.addSubElement(new JsonBoolean(true));
                } else this.unexpectedCharError(c);
                break;
            case 'f':
            case 'F':
                if (String.valueOf(this.parser.getNext(4)).equalsIgnoreCase("alse")) {
                    this.parser.forward(4);
                    this.addSubElement(new JsonBoolean(false));
                } else this.unexpectedCharError(c);
                break;
            default:
                //
        }
    }

    @Override
    public final void readNull(char c) {
        if ((c == 'n' || c == 'N') && String.valueOf(this.parser.getNext(3)).equalsIgnoreCase("ull")) {
            this.parser.forward(3);
            this.addSubElement(new JsonString());
            return;
        }
        this.openNum();
    }

    @Override
    public final void error(String s) {
        this.parser.error(s);
    }


    @Override
    public void read(char c) {

    }
    @Override
    public JsonElement getElem() {
        return null;
    }

    @Override
    public void addSubElement(JsonElement element) {}
}
