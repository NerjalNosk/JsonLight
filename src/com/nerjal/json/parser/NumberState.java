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
        List<String> str = List.of(s);
        StringBuilder sb = new StringBuilder();
        if (this.foundDecimal) str = List.of(str.get(0).split("\\."));
        boolean b = false;
        for (String sp : str) {
            if (this.foundDecimal && b) sb.append('.');
            int i = 0;
            for (char c : sp.toCharArray()) {
                i *= 16;
                switch (c) {
                    case 'a', 'A':
                        i += 10;
                    case 'b', 'B':
                        i += 11;
                    case 'c', 'C':
                        i += 12;
                    case 'd', 'D':
                        i += 13;
                    case 'e', 'E':
                        i += 14;
                    case 'f', 'F':
                        i += 15;
                    default:
                        i += Integer.parseInt(String.valueOf(c));
                }
            }
            sb.append(String.format("%d",i));
            if (b) break;
            b = true;
        }
        return sb.toString();
    }

    private String byteString(String s) {
        List<String> str = List.of(s);
        StringBuilder sb = new StringBuilder();
        if (this.foundDecimal) str = List.of(str.get(0).split("\\."));
        boolean b = false;
        for (String sp : str) {
            if (this.foundDecimal && b) sb.append('.');
            int i = 0;
            for (char c : sp.toCharArray()) {
                i *= 2;
                i += Integer.parseInt(String.valueOf(c));
            }
            sb.append(String.format("%d",i));
            if (b) break;
            b = true;
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
