package io.github.nerjalnosk.jsonlight.parser;

import io.github.nerjalnosk.jsonlight.JsonError;
import io.github.nerjalnosk.jsonlight.elements.JsonElement;
import io.github.nerjalnosk.jsonlight.elements.JsonNumber;

import java.util.Optional;

public class IdState extends AbstractState {
    private Boolean isRef = null;
    private Integer since = null;

    protected IdState(StringParser stringParser, ParserState olderState) {
        super(stringParser, olderState);
    }

    private void readAlt() {
        if (this.isRef != null) {
            this.unexpectedCharError(this.parser.getActual());
            return;
        }
        this.isRef = false;
    }

    private void readHash() {
        if (this.isRef != null) {
            this.unexpectedCharError(this.parser.getActual());
            return;
        }
        this.isRef = true;
    }

    private void readNum() {
        if (this.isRef == null) {
            this.unexpectedCharError(this.parser.getActual());
            return;
        }
        if (this.since == null) {
            this.since = this.parser.getIndex();
        }
    }

    @Override
    public void closeId() {
        if (this.isRef == null || this.since == null) {
            this.unexpectedCharError(this.parser.getActual());
            return;
        }
        char[] n = this.parser.getPrecedents(this.parser.getActual() - this.since);
        String s = String.valueOf(n);
        try {
            this.storedId = Integer.parseInt(s);
        } catch (NumberFormatException ex) {
            this.error("unable to parse ID " + s);
            return;
        }
        if (this.isRef) {
            this.olderState.addSubElement(this.getElem());
        } else {
            this.olderState.feedId(this.storedId);
        }
        this.parser.switchState(this.olderState);
    }

    @Override
    public void read(char c) {
        if (c == '\n' || c == '\r') this.parser.increaseLine();
        if (Character.isWhitespace(c)) return;

        switch (c) {
            case '@':
                this.readAlt();
                break;
            case '#':
                this.readHash();
                break;
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
                this.readNum();
                break;
            case '>':
                this.closeId();
                break;
            default:
                this.unexpectedCharError(c);
        }
    }

    @Override
    public JsonElement getElem() {
        Optional<JsonElement> opt = this.parser.retrieveElement(this.storedId);
        if (opt.isPresent()) return opt.get();
        this.parser.error(new JsonError.NoSuchIdCircularJsonException(String.format(
                "Error parsing %s to json element: no known id '%d' referenced at index %d of line %d",
                parser.getParserDataKey(), this.storedId, parser.getLineIndex(), parser.getLine()
        )));
        return new JsonNumber(0); // non-null to avoid error conflicts
    }
}
