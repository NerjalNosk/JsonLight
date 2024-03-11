package io.github.nerjalnosk.jsonlight.parser;

import io.github.nerjalnosk.jsonlight.elements.JsonElement;

/**
 * <p>The interface for all the states
 * used by the {@link StringParser}
 * class in order to parse a string.
 * </p>
 * <p>All different state correspond
 * to a type of element, and thus are
 * responsible for parsing each
 * corresponding part of the string
 * being parsed.
 * </p>
 * Methods here are used for:<br>
 * <ul>
 * <li>Opening an other state</li>
 * <li>Closing the current state</li>
 * <li>Reading an element which
 * doesn't require a specific
 * a state</li>
 * <li>Marking the parsing process
 * as throwing an error</li>
 * <li>Reading the next char (and
 * executing the corresponding
 * method)</li>
 * <li>Getting/editing the state's
 * parsing result</li>
 * </ul>
 * @author nerjal
 */
public interface ParserState {

    /**
     * Switches the affiliated
     * {@link StringParser} to a new
     * Object state, if the current
     * state allows it.
     */
    void openObject();

    /**
     * Closes a current Object state,
     * and switch back the affiliated
     * {@link StringParser} to its
     * precedent state.
     */
    void closeObject();

    /**
     * Switches the affiliated
     * {@link StringParser} to a new
     * Array state, if the current
     * state allows it.
     */
    void openArray();

    /**
     * Closes a current Array state,
     * and switch back the affiliated
     * {@link StringParser} to its
     * precedent state.
     */
    void closeArray();

    /**
     * Switches the affiliated
     * {@link StringParser} to a new
     * String state, if the current
     * state allows it.
     */
    void openString();

    /**
     * Closes a current String state,
     * and switch back the affiliated
     * {@link StringParser} to its
     * precedent state.
     */
    void closeString();

    /**
     * Switches the affiliated
     * {@link StringParser} to a new
     * Number state, if the current
     * state allows it.
     */
    void openNum();

    /**
     * Closes a current Number state,
     * and switch back the affiliated
     * {@link StringParser} to its
     * precedent state.
     */
    void closeNum();

    /**
     * Switches the affiliated
     * {@link StringParser} to a new
     * ID state, if the current
     * state allows it.
     */
    void openId();

    /**
     * Provides the specified ID
     * as instantiation for the
     * upcoming element.
     * @param id The ID to be set
     *           for the upcoming
     *           element.
     */
    void feedId(int id);

    /**
     * Closes a current ID state and
     * switches back the affiliated
     * {@link StringParser}
     * to its precedent state.
     */
    void closeId();

    /**
     * Switches the affiliated
     * {@link StringParser} to a new
     * Comment state, if the current
     * state allows it.
     */
    void openComment();

    /**
     * Closes a current Comment state,
     * and switch back the affiliated
     * {@link StringParser} to its
     * precedent state.
     */
    void closeComment();

    /**
     * Reads a boolean, and adds it
     * to the current state's element,
     * if the state allows it.
     * @param c the first char of the
     *          supposed boolean
     */
    void readBool(char c);

    /**
     * Reads a null string, and adds it
     * to the current state's element,
     * if the state allows it.
     * @param c the first char of the
     *          supposed {@code null}
     */
    void readNull(char c);

    /**
     * Marks the affiliated
     * {@link StringParser} as throwing an
     * error, with the specified string
     * as error message
     * @param s the thrown error's message
     */
    void error(String s);

    /**
     * Calls for another method, chosen
     * depending on the specified char.
     * @param c the char at the affiliated
     *          {@link StringParser}'s
     *          cursor's current position,
     *          to be read by the state.
     */
    void read(char c);

    /**
     * Returns the element resulting from
     * the state's processing.
     * @return the element resulting from
     *         the state's processing
     */
    JsonElement getElem();

    /**
     * Adds the specified JSON element to
     * the state's element's inner elements,
     * if the state allow it.
     * @param element the element to add to
     *                the result element.
     */
    void addSubElement(JsonElement element);
}
