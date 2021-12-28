package com.nerjal.json.parser;

import com.nerjal.json.elements.JsonNumber;

import java.util.List;

public class NumberState extends AbstractState {
    private int charCount = 0;
    private boolean foundE = false;
    private boolean foundDecimal = false;
    private boolean isHex = false;
    private boolean isByte = false;

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

    private void foundB() {
        if (this.isHex) return;
        if (this.charCount > 0 || this.parser.getPrecedent() != '0')
            this.error(String.format("unexpected character %c", this.parser.getActual()));
        else {
            this.isByte = true;
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

    private String byteString(String s) {
        List<String> str = List.of(s.split("\\."));
        StringBuilder sb = new StringBuilder();
        int i = 0;
        for (char c : str.get(0).toCharArray()) {
            i *= 2;
            i += Integer.parseInt(String.valueOf(c),2);
        }
        sb.append(String.format("%d",i));
        if (this.foundDecimal) {
            double d = 1;
            double r = 0;
            for (char c : str.get(1).toCharArray()) {
                d /= 2;
                r += d * Integer.parseInt(String.valueOf(c), 2);
            }
            sb.append(String.format("%f",r).substring(1));
        }
        return sb.toString();
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
            case '0', '1':
                break;
            case '2','3','4','5','6','7','8','9':
                if (this.isByte) this.error(String.format("unexpected character %c",c));
            case 'e','E':
                if (this.isHex) break;
                else if (this.foundE) this.error("scientific notation with double E");
                else this.foundE = true;
            case '.':
                if (this.foundDecimal) this.error("unexpected decimal character '.'");
                else this.foundDecimal = true;
            case 'x', 'X':
                this.foundX();
            case 'b', 'B':
                this.foundB();
            case 'a', 'A', 'c', 'C', 'd', 'D', 'f', 'F':
                if (!this.isHex) this.error(String.format("unexpected character %c",c));
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
        if (this.isByte) s = this.byteString(s);
        if (this.isHex) s = this.hexString(s);
        if (this.foundE) {
            if (this.foundDecimal) n = Double.parseDouble(s);
            else n = Long.parseLong(s);
        } else if (this.foundDecimal) n = Float.parseFloat(s);
        else n = Integer.parseInt(s);
        return new JsonNumber(n);
    }
}
