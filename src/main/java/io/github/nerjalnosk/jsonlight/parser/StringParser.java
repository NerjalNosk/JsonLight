package io.github.nerjalnosk.jsonlight.parser;

import static io.github.nerjalnosk.jsonlight.JsonError.*;

import io.github.nerjalnosk.jsonlight.JsonParser;
import io.github.nerjalnosk.jsonlight.elements.JsonElement;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * <p>A parser to get a JSON5 structure from a
 * string
 * </p>
 * <p>Once initiated, use {@link #parse()} to
 * parse the string and get the corresponding
 * {@link JsonElement}.<br>
 * Alternatively, use the static method
 * {@link #parse(String)} to directly parse
 * a string to the corresponding element.
 * </p>
 * @see JsonParser
 * @since JDK 16
 * @author nerjal
 */
public class StringParser {
    protected ParserState state;
    private String readStr;
    private int index = 0;
    private int line = 1;
    private int lineIndex = 0;
    protected boolean run = false;
    protected boolean stop = false;
    protected boolean isErrored = false;
    protected final ParserOptions options;
    protected JsonParseException storedErr = null;
    private Consumer<String> logReceiver = System.err::println;
    private final Map<Integer, JsonElement> idMap = new HashMap<>();

    /**
     * Empty StringParser.<br>
     * Use {@link #setParseString(String)}
     * to set the string to be parsed.
     */
    public StringParser() {
        this.options = new ParserOptions.Builder().json5().classic().build();
    }

    /**
     * Instantiates a parser with the
     * specified string as the string to
     * parse, thus ready to run.
     * @param s the string to be parsed
     */
    public StringParser(String s) {
        this.readStr = s;
        this.state = new EmptyState(this);
        this.options = new ParserOptions.Builder().json5().classic().build();
    }

    public StringParser(ParserOptions options) {
        this.options = options;
    }

    public StringParser(String s, ParserOptions options) {
        this.readStr = s;
        this.state = new EmptyState(this);
        this.options = options;
    }

    protected final void log(Object o) {
        this.log(o.toString());
    }

    protected final void log(String s) {
        this.logReceiver.accept(s);
    }

    public final void setLogReceiver(Consumer<String> receiver) {
        this.logReceiver = receiver;
    }

    /**
     * Increments indexes.<br>
     * Used for superclasses, while
     * keeping {@code index} and
     * {@code lineIndex} private.
     */
    protected final void incrementIndexes() {
        this.index++;
        this.lineIndex++;
    }

    /**
     * Sets or changes the string to be
     * parsed for the specified one.<br>
     * Also resets all parsing attributes,
     * allowing to use the same parser to
     * parse multiple strings.
     * @param s the string to be parsed
     */
    public void setParseString(String s) {
        this.readStr = s;
        this.state = new EmptyState(this);
        this.index = 0;
        this.line = 1;
        this.run = false;
        this.stop = false;
    }

    /**
     * Static methods that instantiate a new
     * parser with the specified string and
     * runs it directly, only returning the
     * parsing output.<br>
     * All parsing exceptions are passed to
     * the caller.
     * @param s the string to be parsed
     * @return the {@link JsonElement}
     *         corresponding to the specified
     *         string
     * @throws JsonParseException if any
     *         exception is raised while
     *         trying to parse the
     *         specified string
     */
    public static JsonElement parse(String s) throws JsonParseException {
        StringParser parser = new StringParser();
        parser.setParseString(s);
        parser.read();
        return parser.getElem();
    }

    /**
     * Parses the instance's string to the
     * corresponding {@link JsonElement},
     * and returns it.<br>
     * All parsing exceptions are passed to
     * the caller.
     * @return the instance's string's
     *         corresponding
     *         {@link JsonElement}
     * @throws JsonParseException if any
     *         exception is raised while
     *         trying to parse the
     *         specified string
     */
    public JsonElement parse() throws JsonParseException {
        if (this.readStr == null) throw new JsonParseException("Cannot parse null");
        this.read();
        return this.getElem();
    }

    /**
     * Parses the instance's string to the
     * corresponding {@link JsonElement}.<br>
     * All parsing exceptions are passed to
     * the caller.
     * @throws JsonParseException if any
     *         exception is raised while
     *         trying to parse the
     *         specified string
     * @throws NullPointerException if
     *         the instance's string is
     *         null
     */
    public void read() throws JsonParseException, NullPointerException {
        this.run = true;
        while (!this.stop) {
            if (this.isErrored) throw this.storedErr;
            this.state.read(this.getActual());
            this.index++;
            this.lineIndex++;
            if (this.index >= this.readStr.length()) {
                while (!this.state.isFinal()) {
                    this.state.read(Character.MIN_VALUE);
                }
                this.stop = true;
            }
        }
        this.run = false;
    }

    /**
     * Returns the parser's parsing result
     * @return the parser's parsing result
     * @throws JsonParseException if the
     *         parser hasn't run or has
     *         since been reset
     */
    public final JsonElement getElem() throws JsonParseException {
        if (!this.stop) throw new JsonParseException("Parser haven't parsed any string");
        return this.state.getElem();
    }

    // state manipulation

    /**
     * Changes the parser's state with the specified
     * one if the parser is running.
     * @param parserState the parser's new state
     * @throws UnsupportedOperationException is the
     *         parser is not running when trying to
     *         apply the changes.
     */
    public void switchState(ParserState parserState) {
        if (!this.run) throw new UnsupportedOperationException("Cannot change a not running parser's state.");
        this.state = parserState;
    }

    // readStr chars and indexes manipulation

    /**
     * Moves forward the parser's cursor if it is
     * already running.
     * @throws UnsupportedOperationException if the
     *         parser is not running when trying to
     *         apply the changes.
     */
    public void forward() {
        if (!this.run) throw new UnsupportedOperationException("Cannot move a not running parser's cursor");
        this.index++;
        this.lineIndex++;
    }

    /**
     * Moves the parser's cursor of the specified
     * index change if it is already running.<br>
     * E.g.  {@code #formard(-4)} ->
     * {@code cursor -= 4}
     * @param i the cursor position move factor
     *          (positive being forward)
     * @throws UnsupportedOperationException if the
     *         parser is not running when trying to
     *         apply the changes.
     */
    public void forward(int i) {
        if (!this.run) throw new UnsupportedOperationException("Cannot move a not running parser's cursor");
        this.index += i;
        this.lineIndex += i;
    }

    /**
     * Increases the parser's line index if it is
     * already running.
     * @throws UnsupportedOperationException if the
     *         parser is not running when trying to
     *         apply the changes.
     */
    public final void increaseLine() {
        if (!this.run) throw new UnsupportedOperationException("Cannot move a not running parser's line index");
        this.line++;
        this.lineIndex = 0;
    }

    /**
     * Returns the parser's current cursor line.
     * @return the parser's current cursor line.
     */
    public final int getLine() {
        return line;
    }

    /**
     * Returns the parser's current cursor position
     * in the current line.
     * @return the parser's current cursor position
     *         in the current line.
     */
    public final int getLineIndex() {
        return  this.lineIndex;
    }

    /**
     * Returns the parser's current cursor position
     * @return the parser's current cursor position
     */
    public final int getIndex() {
        return this.index;
    }

    /**
     * Returns the char at the cursor's pos + 1
     * @return the char at the cursor's pos + 1
     */
    public char getNext() {
        return this.readStr.charAt(this.index+1);
    }

    /**
     * Returns the specified number of chars
     * positioned right after the parser's cursor.
     * @param length the number of character to
     *               return
     * @return the specified number of chars
     *         positioned right after the parser's
     *         cursor
     */
    public char[] getNext(int length) {
        return this.readStr.substring(this.index+1, this.index+1+length).toCharArray();
    }

    /**
     * Returns the char at the exact cursor's
     * position.
     * @return the char at the exact cursor's
     *         position
     */
    public char getActual() {
        return this.readStr.charAt(this.index);
    }

    /**
     * Returns the char at the cursor's pos - 1
     * @return the char at the cursor's pos - 1
     */
    public char getPrecedent() {
        return this.index < 1 ? 0 : this.readStr.charAt(this.index-1);
    }

    /**
     * Returns the specified number of chars
     * positioned right before the parser's
     * cursor.
     * @param i the number of character to
     *          return
     * @return the specified number of chars
     *         positioned right before the
     *         parser's cursor
     */
    public char[] getPrecedents(int i) {
        return this.readStr.substring(this.index-i,this.index).toCharArray();
    }

    // circular processing

    /**
     * Adds the provided element to the map at
     * the specified ID if there is none
     * matching yet, and returns whether
     * there was already one mapped for said ID.
     * @param i the ID to which map the provided
     *          element.
     * @param e The element to map to the
     *          specified ID.
     * @return Whether there already was an
     *         element mapped for the specified
     *         ID other than the provided one.
     */
    public final boolean feedId(int i, JsonElement e) {
        JsonElement f = this.idMap.putIfAbsent(i, e);
        return f != e;
    }

    /**
     * Retrieves the mapped element for the
     * specified ID, if any.
     * @param i The ID to which should be
     *          mapped the returned element.
     * @return The element mapped to the
     *         specified ID, if any.
     */
    public final Optional<JsonElement> retrieveElement(int i) {
        return Optional.ofNullable(this.idMap.getOrDefault(i, null));
    }

    // errors

    public String getParserDataKey() {
        return "string";
    }

    /**
     * Sets the parser to throw an error
     * upon reading the next char if it is
     * already running.
     * @param s the error's inner message
     * @throws UnsupportedOperationException if the
     *         parser is not running when trying to
     *         apply the changes
     */
    public final void error(String s) {
        if (!this.run) throw new UnsupportedOperationException("Cannot set an error on a not running parser");
        this.isErrored = true;
        this.storedErr = buildError(s);
    }

    /**
     * Sets the parser to throw the
     * specified error upon reading
     * the next char if it is
     * already running.
     * @param e the error to throw
     * @throws UnsupportedOperationException if the
     *         parser is not running when trying to
     *         execute this method.
     */
    public final void error(JsonParseException e) {
        if (!this.run) throw new UnsupportedOperationException("Cannot set an error on a not running parser");
        this.isErrored = true;
        this.storedErr = e;
    }

    /**
     * Builds and returns a new
     * {@link JsonParseException} for the
     * running methods.
     * @param s The built error's message.
     * @return the parser's throw exception
     *         to throw
     */
    protected final JsonParseException buildError(String s) {
        return new JsonParseException(String.format(
                "Error parsing %s to json element: %s at index %d of line %d",
                this.getParserDataKey(), s, this.lineIndex, this.line
        ));
    }
}
