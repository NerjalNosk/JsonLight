package com.nerjal.json.parser;

import com.nerjal.json.elements.JsonBoolean;
import com.nerjal.json.elements.JsonElement;
import com.nerjal.json.elements.JsonString;

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
 */
public abstract class AbstractState implements ParserState {
    protected StringParser parser;
    protected ParserState olderState;

    /**
     * Instantiates a new
     * {@link StringParser} state.
     * @param stringParser the state's
     *                     affiliated
     *                     parser
     * @param olderState the parser's
     *                   older state
     */
    public AbstractState(StringParser stringParser, ParserState olderState) {
        this.parser = stringParser;
        this.olderState = olderState;
    }

    @Override
    public void openObject() {

    }

    @Override
    public void closeObject() {

    }

    @Override
    public void openArray() {

    }

    @Override
    public void closeArray() {

    }

    @Override
    public void openString() {

    }

    @Override
    public void closeString() {

    }

    @Override
    public void openNum() {

    }

    @Override
    public void closeNum() {

    }

    @Override
    public final void openComment() {
        this.parser.forward(1);
        switch (this.parser.getActual()) {
            case '/' -> this.parser.switchState(new CommentState(this.parser, this, false));
            case '*' -> this.parser.switchState(new CommentState(this.parser, this, true));
            default -> this.error("unexpected character '/'");
        }
    }

    @Override
    public void closeComment() {

    }

    @Override
    public final void readBool(char c) {
        switch (c) {
            case 't', 'T':
                if (String.valueOf(this.parser.getNext(3)).equalsIgnoreCase("rue")) {
                    this.parser.forward(3);
                    this.addSubElement(new JsonBoolean(true));
                } else this.error(String.format("unexpected character '%c'",c));
                break;
            case 'f', 'F':
                if (String.valueOf(this.parser.getNext(4)).equalsIgnoreCase("alse")) {
                    this.parser.forward(4);
                    this.addSubElement(new JsonBoolean(false));
                } else this.error(String.format("unexpected character '%c'", c));
        }
    }

    @Override
    public final void readNull(char c) {
        if (c == 'n' || c == 'N') {
            if (String.valueOf(this.parser.getNext(3)).equalsIgnoreCase("ull")) {
                this.parser.forward(3);
                this.addSubElement(new JsonString());
                return;
            }
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
