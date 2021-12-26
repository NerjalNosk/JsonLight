package com.nerjal.json.parser;

import com.nerjal.json.elements.JsonNumber;

public class NumberState extends AbstractState {
    private int powerOfTen = 1; // requires a first number to be opened
    private int decimalNumber = 0;
    private int powerE = 0;
    private boolean foundE = false;
    private boolean foundDecimal = false;

    public NumberState(StringParser stringParser, ParserState olderState) {
        super(stringParser, olderState);
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
            case '0','1','2','3','4','5','6','7','8','9':
                if (this.foundE) this.powerE++;
                else if (this.foundDecimal) this.decimalNumber++;
                else this.powerOfTen++;
            case 'e','E':
                if (this.foundE) this.error("scientific notation with double E");
                else this.foundE = true;
            case '.':
                if (this.foundDecimal) this.error("unexpected decimal character '.'");
                else this.foundDecimal = true;
            default:
                this.closeNum();
        }
    }

    @Override
    public JsonNumber getElem() {
        if (this.foundE) {
            if (this.foundDecimal) return new JsonNumber(Double.parseDouble(String.valueOf(
                    this.parser.getOlder(this.powerE+this.decimalNumber+powerOfTen+1))+this.parser.getActual()));
            return new JsonNumber(Long.parseLong(
                    String.valueOf(this.parser.getOlder(this.powerOfTen+this.powerE))+this.parser.getActual()));
        } if (this.foundDecimal) return new JsonNumber(Float.parseFloat(String.valueOf(this.parser.getOlder(
                this.powerOfTen+this.decimalNumber+1))+this.parser.getActual()));
        return new JsonNumber(Integer.parseInt(
                String.valueOf(this.parser.getOlder(this.powerOfTen))+this.parser.getActual()));
    }
}
