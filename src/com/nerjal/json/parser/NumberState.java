package com.nerjal.json.parser;

import com.nerjal.json.elements.JsonNumber;

import java.util.List;

public class NumberState extends AbstractState {
    private int charCount = 0;
    private boolean foundE = false;
    private boolean foundDecimal = false;
    private boolean isHex = false;

    public NumberState(StringParser stringParser, ParserState olderState) {
        super(stringParser, olderState);
        if (this.parser.getActual() == '.') this.foundDecimal = true;
    }

    private void foundX() {
        if (this.charCount > 0 || this.parser.getPrecedent() != '0')
            this.error(String.format("unexpected character %c", this.parser.getActual()));
        else {
            this.isHex = true;
            this.charCount++;
        }
    }

    private String hexString(String s) {
        List<String> str = List.of(s.split("\\."));
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

    private void readNaN() {
        if (this.charCount == 0 && (this.parser.getPrecedent() == 'n' || this.parser.getPrecedent() == 'N') &&
                (this.parser.getNext() == 'n' || this.parser.getNext() == 'N')) {
            this.parser.forward();
            this.olderState.addSubElement(new JsonNumber(Float.NaN));
            this.parser.switchState(this.olderState);
        } else this.error(String.format("unexpected character %c", this.parser.getActual()));
    }

    private void readInfinity() {
        if (this.charCount != 0) {
            this.parser.error(String.format("unexpected character %c", this.parser.getActual()));
            return;
        }
        boolean negative = false;
        char c = this.parser.getActual();
        switch (c) {
            case 'i', 'I':
                if (this.parser.getPrecedent() == '-') negative = true;
                else if (this.parser.getPrecedent() == '+') break;
                else {
                    this.error(String.format("unexpected character %c", c));
                    return;
                }
                break;
            case 'n', 'N':
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
        this.parser.switchState(this.olderState);
        this.olderState.addSubElement(this.getElem());
    }

    @Override
    public void read(char c) {
        switch (c) {
            case '0', '1', '2','3','4','5','6','7','8','9':
                break;
            case 'e','E':
                if (this.isHex) break;
                else if (this.foundE) this.error("scientific notation with double E");
                else this.foundE = true;
                break;
            case '.':
                if (this.foundDecimal) this.error("unexpected decimal character '.'");
                else this.foundDecimal = true;
                break;
            case 'x', 'X':
                this.foundX();
                break;
            case 'a', 'A':
                if (this.isHex) break;
                else if (this.charCount == 0) this.readNaN();
                else this.error(String.format("unexpected character %c", c));
                break;
            case 'b', 'B', 'c', 'C', 'd', 'D', 'f', 'F':
                if (!this.isHex) this.error(String.format("unexpected character %c",c));
                break;
            case 'i','I','n','N':
                if (this.charCount < 2) this.readInfinity();
                else this.error(String.format("unexpected character %c", c));
                break;
            case '-', '+':
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
        String s = String.valueOf(this.parser.getPrecedents(this.charCount))+this.parser.getActual();
        Number n;
        if (this.isHex) s = this.hexString(s);
        if (this.foundE) {
            n = Double.parseDouble(s);
        } else if (this.foundDecimal) n = Float.parseFloat(s);
        else n = Integer.parseInt(s);
        return new JsonNumber(n);
    }
}
