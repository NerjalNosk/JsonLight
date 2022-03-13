package com.nerjal.json.parser;

import com.nerjal.json.elements.JsonNumber;
import com.nerjal.json.parser.options.NumberParseOptions;

import java.util.Arrays;
import java.util.List;

/**
 * The {@link StringParser} JSON
 * number parsing state class.<br>
 * Cannot hold other elements<br>
 * Closes on any not corresponding
 * char.
 * @author Nerjal Nosk
 */
public class NumberState extends AbstractState {
    private int charCount = 0;
    private boolean foundE = false;
    private boolean foundDecimal = false;
    private boolean isHex = false;

    public NumberState(StringParser stringParser, ParserState olderState) {
        super(stringParser, olderState);
        if (this.parser.getActual() == '.') {
            this.foundDecimal = true;
            this.charCount ++;
        }
    }

    /**
     * Specifies the state to be
     * parsing a hexadecimal
     * number, if an {@code 'x'}
     * char has been found at a
     * matching position.
     */
    private void foundX() {
        if (this.charCount > 0 || this.parser.getPrecedent() != '0')
            this.error(String.format("unexpected character %c", this.parser.getActual()));
        else {
            this.isHex = true;
        }
    }

    /**
     * Transforms the specified
     * hex number string to a
     * decimal number string
     * @param s the hex string to
     *          transform into
     *          a decimal
     * @return the decimal string
     *         of the specified
     *         hex number string
     */
    private String hexString(String s) {
        List<String> str = Arrays.asList(s.substring(2).split("\\."));
        StringBuilder sb = new StringBuilder();
        int i = 0;
        for (char c : str.get(0).toCharArray()) {
            i *= 16;
            i += Integer.parseInt(String.valueOf(c),16);
        }
        sb.append(String.format("%d",i));
        if (this.foundDecimal) {
            double d = 1;
            double r = 0;
            for (char c : str.get(1).toCharArray()) {
                d /= 16;
                r += d * Integer.parseInt(String.valueOf(c),16);
            }
            sb.append(String.format("%f",r).substring(1));
        }
        return sb.toString();
    }

    /**
     * Reads a {@code NaN} value
     * from the parser's string,
     * and closes the state.<br>
     * Appends the NaN value to
     * the parent state if
     * the parsing ends
     * successfully.
     */
    private void readNaN() {
        if (this.charCount == 0 && (this.parser.getPrecedent() == 'n' || this.parser.getPrecedent() == 'N') &&
                (this.parser.getNext() == 'n' || this.parser.getNext() == 'N')) {
            this.parser.forward();
            this.olderState.addSubElement(new JsonNumber(Float.NaN));
            this.parser.switchState(this.olderState);
        } else this.error(String.format("unexpected character %c", this.parser.getActual()));
    }

    /**
     * Reads a {@code infinity}
     * value from the parser's
     * string, and closes the
     * state.<br>
     * Appends the {@code +/-}
     * infinity value to the
     * parent state if the
     * parsing ends successfully.
     */
    private void readInfinity() {
        if (this.charCount != 0) {
            this.parser.error(String.format("unexpected character %c", this.parser.getActual()));
            return;
        }
        boolean negative = false;
        char c = this.parser.getActual();
        switch (c) {
            case 'i':
            case 'I':
                if (this.parser.getPrecedent() == '-') negative = true;
                else if (this.parser.getPrecedent() == '+') break;
                else {
                    this.error(String.format("unexpected character %c", c));
                    return;
                }
                break;
            case 'n':
            case 'N':
                if (this.parser.getPrecedent() != 'i' && this.parser.getPrecedent() != 'I') {
                    this.error(String.format("unexpected character %c", c));
                }
                break;
            default:
                this.error(String.format("unexpected character %c", c));
                return;
        }
        if (negative &! String.valueOf(this.parser.getNext(7)).equalsIgnoreCase("nfinity")) {
            this.error(String.format("unexpected character %c", this.parser.getActual()));
        } else if (!String.valueOf(this.parser.getNext(6)).equalsIgnoreCase("finity")) {
            this.error(String.format("unexpected character %c", this.parser.getActual()));
        } else {
            this.parser.forward(negative ? 7 : 6);
            this.olderState.addSubElement(new JsonNumber(negative ? Float.NEGATIVE_INFINITY : Float.POSITIVE_INFINITY));
            this.parser.switchState(this.olderState);
        }
    }

    @Override
    public void closeNum() {
        this.parser.forward(-1);
        this.olderState.addSubElement(this.getElem());
        this.parser.switchState(this.olderState);
    }

    @Override
    public void read(char c) {
        switch (c) {
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
                break;
            case 'e':
            case 'E':
                if (this.isHex) break;
                else if (this.foundE) this.error("scientific notation with double E");
                else this.foundE = true;
                break;
            case '.':
                if (this.foundDecimal) this.error("unexpected decimal character '.'");
                else this.foundDecimal = true;
                break;
            case 'x':
            case 'X':
                this.foundX();
                break;
            case 'a':
            case 'A':
                if (this.isHex) break;
                else if (this.charCount == 0) this.readNaN();
                else this.error(String.format("unexpected character %c", c));
                break;
            case 'b':
            case 'B':
            case 'c':
            case 'C':
            case 'd':
            case 'D':
            case 'f':
            case 'F':
                if (!this.isHex) this.error(String.format("unexpected character %c",c));
                break;
            case 'i':
            case 'I':
            case 'n':
            case 'N':
                if (this.charCount < 2) this.readInfinity();
                else this.error(String.format("unexpected character %c", c));
                break;
            case '-':
            case '+':
                if (this.charCount != 0 && (this.parser.getPrecedent() == 'e' || this.parser.getPrecedent() == 'E'))
                    break;
            default:
                this.closeNum();
                return;
        }
        this.charCount++;
    }

    @Override
    public JsonNumber getElem() {
        String s;
        try {
            s = String.valueOf(this.parser.getPrecedents(this.charCount))+this.parser.getActual();
        } catch (IndexOutOfBoundsException e) {
            s = String.valueOf(this.parser.getPrecedents(this.charCount));
        }
        NumberParseOptions options = new NumberParseOptions();
        if (this.isHex) {
            s = this.hexString(s);
            options.setFormat(NumberParseOptions.NumberFormat.HEXADECIMAL);
        }
        if (this.foundE) {
            options.setFormat(NumberParseOptions.NumberFormat.SCIENTIFIC);
            return new JsonNumber(Double.parseDouble(s), options);
        } else if (this.foundDecimal) return JsonNumber.fromFloatString(s, options);
        else return JsonNumber.fromIntegerString(s, options);
    }
}
