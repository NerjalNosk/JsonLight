package com.nerjal.json.parser;

import com.nerjal.json.JsonError;
import com.nerjal.json.elements.JsonElement;

import java.io.*;
import java.io.FileNotFoundException;

import static com.nerjal.json.JsonError.*;

/**
 * <p>A parser to get a JSON5 structure from a
 * file
 * </p>
 * <p>Once initiated, use {@link #parse()} to
 * parse the file and get the corresponding
 * {@link JsonElement}.<br>
 * Alternatively, use the static method
 * {@link #parse(File)} to directly parse
 * a file to the corresponding element.
 * </p>
 * @see com.nerjal.json.JsonParser
 * @since JDK 16
 * @author Nerjal Nosk
 */
public class FileParser extends StringParser {
    private final InputStreamReader reader;
    private boolean reachFileEnd;
    private int readIndex = 0;
    private int stateLength;
    private char act;
    private final StringBuilder str = new StringBuilder();

    public FileParser(File f) throws FileNotFoundException {
        reader = new FileReader(f);
        super.state = new EmptyState(this,null);
    }

    private FileParser(InputStreamReader stream) {
        reader = stream;
        super.state = new EmptyState(this, null);
    }

    /**
     * Reads the file's content until
     * reaching the actual parsing
     * index.
     */
    private void readFileToIndex() {
        try {
            while (readIndex >= str.length()-1 &! reachFileEnd) {
                char c = (char)reader.read();
                if (c == '\uFFFF') reachFileEnd = true;
                else str.append(c);
            }
            act = str.charAt(readIndex);
        } catch (IOException|IndexOutOfBoundsException e) {
            e.printStackTrace();
            reachFileEnd = true;
        }
        if (reachFileEnd) {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void setParseString(String s) {
    }

    @Override
    public JsonElement parse() throws JsonParseException {
        read();
        return getElem();
    }

    /**
     * Static methods that instantiate a new
     * parser with the specified file and
     * runs it directly, only returning the
     * parsing output.<br>
     * All parsing exceptions are passed to
     * the caller.
     * @param s the path of the file to be
     *          parsed
     * @return the {@link JsonElement}
     *         corresponding to the specified
     *         file
     * @throws JsonParseException if any
     *         exception is raised while
     *         trying to parse the
     *         specified file
     */
    public static JsonElement parse(String s) throws JsonParseException {
        try {
            return parse(new File(s));
        } catch (FileNotFoundException e) {
            throw new JsonError.FileNotFoundException(e);
        }
    }

    /**
     * Static methods that instantiate a new
     * parser with the specified file and
     * runs it directly, only returning the
     * parsing output.<br>
     * All parsing exceptions are passed to
     * the caller.
     * @param f the file to be parsed
     * @return the {@link JsonElement}
     *         corresponding to the specified
     *         file
     * @throws JsonParseException if any
     *         exception is raised while
     *         trying to parse the
     *         specified file
     */
    public static JsonElement parse(File f) throws JsonParseException, FileNotFoundException {
        FileParser parser = new FileParser(f);
        return parser.parse();
    }

    public static JsonElement parseStream(InputStreamReader streamReader) throws JsonParseException {
        FileParser parser = new FileParser(streamReader);
        return parser.parse();
    }

    @Override
    public void read() throws JsonParseException, NullPointerException {
        this.run = true;
        while (!this.stop) {
            if (isErrored) throw buildError();
            readFileToIndex();
            if (stop || reachFileEnd || act == '\uFFFF') break;
            System.out.println(act);
            state.read(act);
            stateLength++;
            readIndex++;
            super.incrementIndexes();
        }
        this.stop = true;
        this.run = false;
    }

    @Override
    public void switchState(ParserState parserState) {
        str.replace(0, stateLength, "");
        readIndex = 0;
        stateLength = 0;
        super.switchState(parserState);
    }

    @Override
    public void forward() {
        readIndex++;
        stateLength++;
        readFileToIndex();
        super.forward();
    }

    @Override
    public void forward(int i) {
        readIndex += i;
        stateLength += i;
        readFileToIndex();
        super.forward(i);
    }

    @Override
    public char getNext() {
        readIndex++;
        readFileToIndex();
        readIndex--;
        return str.charAt(readIndex+1);
    }

    @Override
    public char[] getNext(int length) {
        readIndex+=length;
        readFileToIndex();
        readIndex-=length;
        return str.substring(readIndex+1, readIndex+1+length).toCharArray();
    }

    @Override
    public char getActual() {
        return act;
    }

    @Override
    public char getPrecedent() {
        return str.charAt(readIndex-1);
    }

    @Override
    public char[] getPrecedents(int i) {
        return str.substring(readIndex-i, readIndex).toCharArray();
    }
}
