/**
 * This package holds all the stringification options classes
 * for all the {@link com.nerjal.json.elements}' element classes.
 * <br>
 * The base class {@link com.github.nerjalnosk.jsonlight.parser.options.AbstractParseOptions}
 * shall be registered as superclass for all custom
 * {@link com.github.nerjalnosk.jsonlight.elements.JsonElement}'s stringification option.
 * <br>
 * This allows to:<br>
 * &nbsp; - Set a string's quoting<br>
 * &nbsp; - Set how should a number be stringified (as an int, with decimals...)<br>
 * &nbsp; - Should an array's elements be split per line, or all in one<br>
 * &nbsp; - And much more...<br>
 */
package com.github.nerjalnosk.jsonlight.parser.options;