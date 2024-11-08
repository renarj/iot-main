package com.oberasoftware.home.rules.blocklyv1;

import com.oberasoftware.home.rules.blockly.BlocklyParseException;
import org.w3c.dom.Element;

/**
 * @author Renze de Vries
 */
public interface XMLBlockParser<T> {
    boolean supportsType(String type);

    T parse(Element node) throws BlocklyParseException;
}
