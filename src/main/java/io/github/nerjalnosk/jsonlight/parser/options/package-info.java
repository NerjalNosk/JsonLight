/**
 * This package holds all the stringification options classes
 * for all the {@link io.github.nerjalnosk.jsonlight.elements}' element classes.
 * <br>
 * The base class {@link io.github.nerjalnosk.jsonlight.parser.options.AbstractParseOptions}
 * shall be registered as superclass for all custom
 * {@link io.github.nerjalnosk.jsonlight.elements.JsonElement}'s stringification option.
 * <br>
 * This allows to:<br>
 * &nbsp; - Set a string's quoting<br>
 * &nbsp; - Set how should a number be stringified (as an int, with decimals...)<br>
 * &nbsp; - Should an array's elements be split per line, or all in one<br>
 * &nbsp; - And much more...<br>
 */
package io.github.nerjalnosk.jsonlight.parser.options;